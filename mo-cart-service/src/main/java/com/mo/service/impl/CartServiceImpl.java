package com.mo.service.impl;

import com.alibaba.fastjson.JSON;
import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.interceptor.LoginInterceptor;
import com.mo.model.LoginUserDTO;
import com.mo.request.CartItemRequest;
import com.mo.service.CartService;
import com.mo.service.MpProductService;
import com.mo.vo.CartItemVO;
import com.mo.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2021/4/25
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private MpProductService productService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void clean() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);
    }

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
            ProductVO productVO = productService.findById(productId);
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

}
