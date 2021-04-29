package com.mo.service;

import com.mo.model.MpProductDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.model.ProductMessage;
import com.mo.request.LockProductRequest;
import com.mo.utils.JsonData;
import com.mo.vo.ProductVO;

import java.util.List;
import java.util.Map;

/**
 * @author mo
 * @since 2021-04-25
 */
public interface ProductService {

    Map<String, Object> pageProductList(int page, int size);

    ProductVO findById(Long productId);

    List<ProductVO> findProductByIdBatch(List<Long> productIds);

    JsonData lockProducts(LockProductRequest request);

    boolean releaseProductStock(ProductMessage productMessage);
}
