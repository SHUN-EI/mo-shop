package com.mo.service.impl;

import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.exception.BizException;
import com.mo.mapper.MpUserMapper;
import com.mo.model.MpUserDO;
import com.mo.request.UserRegisterRequest;
import com.mo.service.NotifyService;
import com.mo.service.UserService;
import com.mo.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by mo on 2021/4/21
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private NotifyService notifyService;
    @Autowired
    private MpUserMapper userMapper;

    @Override
    public JsonData register(UserRegisterRequest request) {

        boolean checkCode = false;
        //邮箱验证码验证
        if (StringUtils.isNoneBlank(request.getMail())) {
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, request.getMail(), request.getCode());
        }

        //取反
        if (!checkCode) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }

        //插入数据库
        MpUserDO userDO = new MpUserDO();
        BeanUtils.copyProperties(request, userDO);
        userDO.setCreateTime(new Date());
        userDO.setUpdateTime(new Date());
        userDO.setSlogan("人生需要动态规划，学习需要贪心算法");

        //账号唯一性检查

        int rows = userMapper.insert(userDO);
        log.info("rows:{},注册成功:{}",rows,userDO.toString());

        //密码加密

        //TODO 新用户注册成功，初始化信息，福利发放

        return null;
    }
}
