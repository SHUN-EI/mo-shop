package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.config.RabbitMQConfig;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.LockStateEnum;
import com.mo.enums.OrderStateEnum;
import com.mo.exception.BizException;
import com.mo.feign.OrderFeignService;
import com.mo.mapper.ProductTaskMapper;
import com.mo.model.MpProductDO;
import com.mo.mapper.MpProductMapper;
import com.mo.model.ProductMessage;
import com.mo.model.ProductTaskDO;
import com.mo.request.LockProductRequest;
import com.mo.request.OrderItemRequest;
import com.mo.service.ProductService;
import com.mo.utils.JsonData;
import com.mo.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mo
 * @since 2021-04-25
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private MpProductMapper productMapper;
    @Autowired
    private ProductTaskMapper productTaskMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private OrderFeignService orderFeignService;


    /**
     * 释放商品库存
     *
     * @param productMessage
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public boolean releaseProductStock(ProductMessage productMessage) {
        //查询product_task 商品库存锁定任务是否存在
        ProductTaskDO productTaskDO = productTaskMapper.selectOne(new QueryWrapper<ProductTaskDO>().eq("id", productMessage.getProductTaskId()));

        if (null == productTaskDO) {
            log.warn("商品库存锁定任务不存在，消息体={}", productMessage);
            return true;//消息消费
        }

        //lock状态才处理
        if (productTaskDO.getLockState().equalsIgnoreCase(LockStateEnum.LOCK.name())) {
            //查询订单状态,远程调用order微服务查询订单状态接口
            JsonData jsonData = orderFeignService.queryOrderState(productMessage.getOutTradeNo());
            //判断查询订单状态接口是否正常响应
            if (jsonData.getCode() == 0) {
                //判断订单状态
                String state = jsonData.getData().toString();

                //若订单状态为 NEW-新建未支付订单，则消息需要返回队列，重新投递
                //正常不会查到状态为NEW的订单，因为商品库存锁定的消息队列设置的延迟时间是比订单消息队列要长的
                //则订单服务那边应该会先查支付状态，根据支付状态修改订单状态为PAY或CANCEL
                if (OrderStateEnum.NEW.name().equalsIgnoreCase(state)) {
                    log.warn("订单状态为NEW,消息需要返回队列，重新投递:{}", productMessage);
                    return false;//消息需要返回队列，重新投递
                }

                //若订单状态为PAY-已经支付订单,需要修改商品库存锁定任务的记录Task的状态为FINISH
                if (OrderStateEnum.PAY.name().equalsIgnoreCase(state)) {
                    productTaskDO.setLockState(LockStateEnum.FINISH.name());
                    productTaskMapper.update(productTaskDO, new QueryWrapper<ProductTaskDO>().eq("id", productMessage.getProductTaskId()));
                    log.info("订单已经支付，修改商品库存锁定记录状态为FINISH:{}", productMessage);
                    return true;//消息消费
                }
            }

            //若订单不存在或订单状态为CANCEL-超时取消订单，确认并消费消息，修改product_task状态为CANCEL,恢复商品的lock_stock
            log.warn("订单不存在，或订单超时被取消，确认并消费消息，修改product_task状态为CANCEL,恢复商品的lock_stock,message:{}", productMessage);
            //恢复商品的锁定库存, 商品的锁定库存=锁定库存值-购买数量值
            productMapper.unlockProductStock(productTaskDO.getProductId(), productTaskDO.getBuyNum());

            //修改商品库存锁定任务的锁定状态为 CANCEL
            productTaskDO.setLockState(LockStateEnum.CANCEL.name());
            productTaskMapper.update(productTaskDO, new QueryWrapper<ProductTaskDO>().eq("id", productMessage.getProductTaskId()));

            return true;//消息消费
        } else {
            log.warn("商品库存锁定状态不是lock,state={},消息体={}", productTaskDO.getLockState(), productMessage);
            return true;//消息消费
        }

    }

    /**
     * 锁定商品库存
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData lockProducts(LockProductRequest request) {

        String orderOutTradeNo = request.getOrderOutTradeNo();
        List<OrderItemRequest> orderItemList = request.getOrderItemList();

        //找出所有商品id
        List<Long> productIds = orderItemList.stream().map(OrderItemRequest::getProductId).collect(Collectors.toList());
        //根据id批量查询商品
        List<ProductVO> productVOList = findProductByIdBatch(productIds);
        //根据商品id把商品分组
        Map<Long, ProductVO> productMap = productVOList.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        //遍历商品，锁定每个商品购买数量
        orderItemList.forEach(obj -> {
            //锁定商品库存
            int rows = productMapper.lockProductStock(obj.getProductId(), obj.getBuyNum());

            if (rows != 1) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
            } else {
                ProductVO productVO = productMap.get(obj.getProductId());
                //插入商品锁定库存任务
                ProductTaskDO productTaskDO = ProductTaskDO.builder()
                        .productId(obj.getProductId())
                        .buyNum(obj.getBuyNum())
                        .lockState(LockStateEnum.LOCK.name())
                        .outTradeNo(orderOutTradeNo)
                        .productName(productVO.getTitle())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();

                productTaskMapper.insert(productTaskDO);

                //每一次锁定商品，都要发送延迟消息,解锁商品库存
                ProductMessage message = new ProductMessage();
                message.setOutTradeNo(orderOutTradeNo);
                //mybaitsPlus插入数据后，自动返回数据库自增id,但mybatis不会默认返回id,需配置
                message.setProductTaskId(productTaskDO.getId());

                rabbitTemplate.convertAndSend(rabbitMQConfig.getProductEventExchange(),
                        rabbitMQConfig.getProductReleaseDelayRoutingKey(), message);

                log.info("商品库存锁定信息发送成功:{}", message);
            }
        });

        return JsonData.buildSuccess();
    }

    /**
     * 根据id批量查询商品
     *
     * @param productIds
     * @return
     */
    @Override
    public List<ProductVO> findProductByIdBatch(List<Long> productIds) {

        //根据id批量查询商品
        List<MpProductDO> productDOList = productMapper.selectList(new QueryWrapper<MpProductDO>().in("id", productIds));
        List<ProductVO> productVOList = productDOList.stream().map(obj -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(obj, productVO);
            return productVO;
        }).collect(Collectors.toList());

        return productVOList;
    }

    /**
     * 商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ProductVO findById(Long productId) {

        MpProductDO productDO = productMapper.selectOne(new QueryWrapper<MpProductDO>().eq("id", productId));

        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);

        return productVO;
    }

    /**
     * 分页查询商品列表
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Map<String, Object> pageProductList(int page, int size) {

        Page<MpProductDO> pageInfo = new Page<>(page, size);

        IPage<MpProductDO> productDOPage = productMapper.selectPage(pageInfo, null);


        HashMap<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", productDOPage.getTotal());
        //总页数
        pageMap.put("total_page", productDOPage.getPages());

        //组装返回前端的对象
        List<ProductVO> productVOList = productDOPage.getRecords().stream()
                .map(obj -> {
                    ProductVO productVO = new ProductVO();
                    BeanUtils.copyProperties(obj, productVO);

                    //商品库存为 总库存-已购买下单的锁定库存
                    productVO.setStock(obj.getStock() - obj.getLockStock());
                    return productVO;
                }).collect(Collectors.toList());

        pageMap.put("current_data", productVOList);

        return pageMap;
    }
}
