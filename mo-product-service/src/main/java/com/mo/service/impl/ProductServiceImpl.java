package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.config.RabbitMQConfig;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.LockStateEnum;
import com.mo.exception.BizException;
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
