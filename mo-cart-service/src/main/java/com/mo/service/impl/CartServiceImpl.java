package com.mo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.config.RabbitMQConfig;
import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.LockStateEnum;
import com.mo.exception.BizException;
import com.mo.feign.OrderFeignService;
import com.mo.feign.ProductFeignService;
import com.mo.interceptor.LoginInterceptor;
import com.mo.mapper.CartTaskMapper;
import com.mo.model.CartMessage;
import com.mo.model.CartTaskDO;
import com.mo.model.LoginUserDTO;
import com.mo.request.CartItemRequest;
import com.mo.request.LockCartItemsRequest;
import com.mo.request.OrderItemRequest;
import com.mo.service.CartService;
import com.mo.utils.JsonData;
import com.mo.vo.CartItemVO;
import com.mo.vo.CartVO;
import com.mo.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by mo on 2021/4/25
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private CartTaskMapper cartTaskMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private OrderFeignService orderFeignService;


    /**
     * 恢复购物车里面的商品项目
     * 用于创建订单失败，需要恢复购物车里面的购物项
     *
     * @param cartMessage
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public boolean recoverCartItems(CartMessage cartMessage) {

        CartTaskDO cartTaskDO = cartTaskMapper.selectOne(new QueryWrapper<CartTaskDO>().eq("id", cartMessage.getCartTaskId()));

        if (null == cartTaskDO) {
            log.warn("购物车商品锁定任务不存在，消息体={}", cartMessage);
            return true;//消息消费
        }

        LoginUserDTO loginUserDTO = LoginUserDTO.builder().id(cartTaskDO.getUserId()).build();
        LoginInterceptor.threadLocal.set(loginUserDTO);

        //查询订单状态,远程调用order微服务查询订单状态接口
        JsonData jsonData = orderFeignService.queryOrderState(cartMessage.getOutTradeNo());
        if (jsonData.getCode() != 0) {
            //订单不存在，恢复购物车的商品项目
            CartItemRequest cartItemRequest = new CartItemRequest();
            cartItemRequest.setProductId(cartTaskDO.getProductId());
            cartItemRequest.setBuyNum(cartTaskDO.getBuyNum());
            addToCart(cartItemRequest);

            //修改购物车商品锁定任务状态为 CANCEL
            cartTaskDO.setLockState(LockStateEnum.CANCEL.name());
            cartTaskDO.setUpdateTime(new Date());
            cartTaskMapper.update(cartTaskDO, new QueryWrapper<CartTaskDO>().eq("id", cartTaskDO.getId()));
            return true;//消息消费
        }
        return true;
    }

    /**
     * 锁定购物车商品项目
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData lockCartItems(LockCartItemsRequest request) {

        Long userId = request.getUserId();
        String orderOutTradeNo = request.getOrderOutTradeNo();
        List<OrderItemRequest> orderItemList = request.getOrderItemList();

        //找出所有商品id
        List<Long> productIds = orderItemList.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());
        JsonData productListData = productFeignService.findProductsByIdList(productIds);

        if (productListData.getCode() != 0) {
            log.error("获取商品失败,msg={}", productListData);
            throw new BizException(BizCodeEnum.PRODUCT_NOT_EXISTS);
        }

        //购物车所有商品
        List<ProductVO> productVOList = productListData.getData(new TypeReference<>() {
        });
        //根据商品id把商品分组
        Map<Long, ProductVO> productMap = productVOList.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        //记录购物车商品项目的相关信息
        orderItemList.forEach(obj -> {
            ProductVO productVO = productMap.get(obj.getProductId());
            //插入购物车商品项目锁定任务
            CartTaskDO cartTaskDO = CartTaskDO.builder()
                    .userId(userId)
                    .productId(obj.getProductId())
                    .buyNum(obj.getBuyNum())
                    .productName(productVO.getTitle())
                    .lockState(LockStateEnum.LOCK.name())
                    .outTradeNo(orderOutTradeNo)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            cartTaskMapper.insert(cartTaskDO);

            //每一次锁定购物车商品，都要发送延迟消息，若订单创建成功，cartTask为FINISH状态
            CartMessage message = new CartMessage();
            message.setOutTradeNo(orderOutTradeNo);
            message.setCartTaskId(cartTaskDO.getId());
            rabbitTemplate.convertAndSend(rabbitMQConfig.getCartEventExchange(),
                    rabbitMQConfig.getCartReleaseDelayRoutingKey(), message);

            log.info("购物车商品项目锁定信息发送成功:{}", message);
        });

        return JsonData.buildSuccess();
    }

    /**
     * 获取对应订单购物车里面的商品信息
     *
     * @param productIds
     * @return
     */
    @Override
    public List<CartItemVO> confirmOrderCartItems(List<Long> productIds) {

        //获取购物车中全部最新的购物项目
        List<CartItemVO> latestCartItems = getAllCartItem(true);

        //根据用户选中的商品id进行过滤，并清空对应的购物项目
        List<CartItemVO> cartItemVOList = latestCartItems.stream().filter(obj -> {
            if (productIds.contains(obj.getProductId())) {
                //删除购物车里面的购物项目
                deleteItem(obj.getProductId());
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return cartItemVOList;
    }

    /**
     * 修改购物车商品数量
     *
     * @param request
     */
    @Override
    public void changeItemNum(CartItemRequest request) {

        Long productId = request.getProductId();
        //获取购物车
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        //根据商品id取商品数据
        Object cacheObj = myCart.get(productId);

        if (null == cacheObj) {
            throw new BizException(BizCodeEnum.PRODUCT_NOT_EXISTS);
        }

        String item = (String) cacheObj;
        CartItemVO cartItemVO = JSON.parseObject(item, CartItemVO.class);
        cartItemVO.setBuyNum(request.getBuyNum());
        myCart.put(productId, JSON.toJSONString(cartItemVO));
    }

    /**
     * 删除购物车商品
     *
     * @param productId
     */
    @Override
    public void deleteItem(Long productId) {

        //获取购物车
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        myCart.delete(productId);

    }

    /**
     * 查看我的购物车
     *
     * @return
     */
    @Override
    public CartVO findMyCart() {

        //获取购物车中全部购物项目
        List<CartItemVO> cartItemVOList = getAllCartItem(false);

        //封装成CartVO
        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);

        return cartVO;
    }


    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);
    }

    /**
     * 添加商品到购物车
     *
     * @param request
     */
    @Override
    public void addToCart(CartItemRequest request) {

        Long productId = request.getProductId();
        Integer buyNum = request.getBuyNum();

        //获取当前用户的购物车
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();

        //根据商品id取商品数据
        Object cacheObj = myCart.get(productId);

        //商品的json数据value
        String result = "";

        if (null != cacheObj) {
            result = (String) cacheObj;
        }

        if (StringUtils.isBlank(result)) {
            //若商品不存在，则新建一个商品加入购物车
            CartItemVO cartItemVO = new CartItemVO();

            //根据商品id 找出商品
            ProductVO productVO = getCartItem(productId);
            if (null == productVO) {
                throw new BizException(BizCodeEnum.CART_FAIL);
            }

            cartItemVO.setProductId(productId);
            cartItemVO.setBuyNum(buyNum);
            cartItemVO.setAmount(productVO.getAmount());
            cartItemVO.setProductImg(productVO.getCoverImg());
            cartItemVO.setProductTitle(productVO.getTitle());

            //加入购物车
            myCart.put(productId, JSON.toJSONString(cartItemVO));

        } else {
            //若商品存在，则修改购物车里面的商品的数量
            CartItemVO cartItemVO = JSON.parseObject(result, CartItemVO.class);
            cartItemVO.setBuyNum(cartItemVO.getBuyNum() + buyNum);
            myCart.put(productId, JSON.toJSONString(cartItemVO));
        }
    }


    /**
     * 获取购物车中最新的购物项目
     *
     * @param latestAmount 是否获取最新的商品价格
     * @return
     */
    private List<CartItemVO> getAllCartItem(boolean latestAmount) {
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        //获取购物车中所有购物项目
        List<Object> items = myCart.values();

        List<CartItemVO> cartItemVOList = new ArrayList<>();

        //拼接商品id集合去查询商品的最新价格
        List<Long> productIds = new ArrayList<>();

        items.forEach(item -> {
            CartItemVO cartItemVO = JSON.parseObject((String) item, CartItemVO.class);
            cartItemVOList.add(cartItemVO);

            productIds.add(cartItemVO.getProductId());
        });

        //需要查询最新的商品价格
        if (latestAmount) {
            setProductLatestAmount(cartItemVOList, productIds);
        }

        return cartItemVOList;
    }

    /**
     * 设置商品最新价格
     *
     * @param cartItemVOList
     * @param productIds
     */
    private void setProductLatestAmount(List<CartItemVO> cartItemVOList, List<Long> productIds) {
        //根据id批量查询商品
        List<ProductVO> productVOList = findProductsByIdBatch(productIds);

        //根据商品id把商品分组
        Map<Long, ProductVO> maps = productVOList.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        cartItemVOList.forEach(item -> {
            ProductVO productVO = maps.get(item.getProductId());
            item.setProductTitle(productVO.getTitle());
            item.setProductImg(productVO.getCoverImg());
            item.setAmount(productVO.getAmount());//修改商品的最新价格
        });
    }


    /**
     * 获取当前用户的购物车
     * BoundHashOperations<H, HK, HV>
     * H 为外层的key, HK为内层map的key,HV为内层map的value
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getMyCartOps() {

        String cartKey = getCartKey();
        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * 获取购物车的key
     *
     * @return
     */
    private String getCartKey() {

        //获取当前用户
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        String cartKey = String.format(CacheKey.CART_KEY, loginUserDTO.getId());
        return cartKey;
    }

    /**
     * 根据商品id批量查询商品
     *
     * @param productIds
     * @return
     */
    private List<ProductVO> findProductsByIdBatch(List<Long> productIds) {
        JsonData products = productFeignService.findProductsByIdList(productIds);
        if (products.getCode() != 0) {
            log.error("获取商品信息失败,msg:{}", products);
            throw new BizException(BizCodeEnum.PRODUCT_NOT_EXISTS);
        }

        List<ProductVO> productVOList = products.getData(new TypeReference<>() {
        });

        return productVOList;
    }


    /**
     * 根据商品id获取商品详情
     *
     * @param productId
     * @return
     */
    private ProductVO getCartItem(Long productId) {
        JsonData data = productFeignService.detail(productId);

        if (data.getCode() != 0) {
            log.error("获取购物车商品详情失败，msg:{}", data);
            throw new BizException(BizCodeEnum.PRODUCT_NOT_EXISTS);
        }

        ProductVO productVO = data.getData(new TypeReference<>() {
        });

        return productVO;

    }

}
