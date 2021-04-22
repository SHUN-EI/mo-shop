package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.mapper.MpUserMapper;
import com.mo.model.MpUserDO;
import com.mo.model.LoginUserDTO;
import com.mo.request.UserLoginRequest;
import com.mo.request.UserRegisterRequest;
import com.mo.service.NotifyService;
import com.mo.service.UserService;
import com.mo.utils.CommonUtil;
import com.mo.utils.JWTUtil;
import com.mo.utils.JsonData;
import com.mo.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    public UserVO findUserDetail() {

        //根据token在threadlocal里面取
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        MpUserDO userDO = userMapper.selectOne(new QueryWrapper<MpUserDO>().eq("id", loginUserDTO.getId()));

        //返回前端数据
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO, userVO);

        return userVO;
    }

    @Override
    public JsonData login(UserLoginRequest request) {

        //根据mail去找有没有这记录
        List<MpUserDO> list = userMapper.selectList(new QueryWrapper<MpUserDO>().eq("mail", request.getMail()));

        if (list != null && list.size() == 1) {
            //已注册
            MpUserDO userDO = list.get(0);
            String cryptPwd = Md5Crypt.md5Crypt(request.getPassword().getBytes(), userDO.getSecret());

            //用密钥+用户传递的明文密码，进行加密，与数据库的密码(密文)进行匹配
            if (cryptPwd.equals(userDO.getPassword())) {
                //登录成功,生成token

                //Lombok写法，需要添加@Builder注解
                // LoginUserDTO build = LoginUserDTO.builder().build();
                LoginUserDTO loginUserDTO = new LoginUserDTO();
                BeanUtils.copyProperties(userDO, loginUserDTO);
                String token = JWTUtil.generateJsonWebToken(loginUserDTO);

                return JsonData.buildSuccess(token);
            } else {
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }

        } else {
            //未注册
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
    }

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

        //生成用户密码的密钥，盐
        userDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));
        //密码加密, 密码+盐处理
        String cryptPwd = Md5Crypt.md5Crypt(request.getPassword().getBytes(), userDO.getSecret());
        userDO.setPassword(cryptPwd);

        //账号唯一性检查
        if (checkUnique(userDO.getMail())) {

            int rows = userMapper.insert(userDO);
            log.info("rows:{},注册成功:{}", rows, userDO.toString());

            //TODO 新用户注册成功，初始化信息，福利发放
            userRegisterInitTask(userDO);

            return JsonData.buildSuccess(userDO);
        } else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }
    }

    /**
     * 校验用户账号唯一
     *
     * @param mail
     * @return
     */
    private Boolean checkUnique(String mail) {

        QueryWrapper<MpUserDO> queryWrapper = new QueryWrapper<MpUserDO>().eq("mail", mail);

        List<MpUserDO> list = userMapper.selectList(queryWrapper);

        return list.size() > 0 ? false : true;
    }

    /**
     * 用户注册，初始化福利信息
     *
     * @param userDO
     */
    private void userRegisterInitTask(MpUserDO userDO) {

    }
}
