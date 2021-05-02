package com.mo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.feign.ProductFeignService;
import com.mo.interceptor.LoginInterceptor;
import com.mo.model.LoginUserDTO;
import com.mo.request.CartItemRequest;
import com.mo.service.CartService;
import com.mo.utils.JsonData;
import com.mo.vo.CartItemVO;
import com.mo.vo.CartVO;
import com.mo.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
