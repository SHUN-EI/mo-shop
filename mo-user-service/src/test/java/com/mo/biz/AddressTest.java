package com.mo.biz;

import com.mo.UserApplication;
import com.mo.service.AddressService;
import com.mo.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mo on 2021/4/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class AddressTest {

    @Autowired
    private AddressService addressService;

    @Test
    public void testAddressDetail() {
        AddressVO addressVO = addressService.detail(1L);
        log.info(addressVO.toString());

        Assert.assertNotNull(addressVO);
    }
}
