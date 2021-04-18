package com.mo.biz;

import com.mo.UserApplication;
import com.mo.model.MpAddressDO;
import com.mo.service.MpAddressService;
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
    private MpAddressService addressService;

    @Test
    public void testAddressDetail() {
        MpAddressDO addressDO = addressService.detail(1L);
        log.info(addressDO.toString());

        Assert.assertNotNull(addressDO);
    }
}
