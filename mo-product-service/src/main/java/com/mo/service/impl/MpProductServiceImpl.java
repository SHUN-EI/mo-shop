package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.model.MpProductDO;
import com.mo.mapper.MpProductMapper;
import com.mo.service.MpProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mo
 * @since 2021-04-25
 */
@Service
public class MpProductServiceImpl implements MpProductService {

    @Autowired
    private MpProductMapper productMapper;


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

    @Override
    public ProductVO findById(Long productId) {

        MpProductDO productDO = productMapper.selectOne(new QueryWrapper<MpProductDO>().eq("id", productId));

        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);

        return productVO;
    }

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
