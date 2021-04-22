package com.mo.service;

import com.mo.request.UserLoginRequest;
import com.mo.request.UserRegisterRequest;
import com.mo.utils.JsonData;
import com.mo.vo.UserVO;

/**
 * Created by mo on 2021/4/21
 */
public interface UserService {

    JsonData register(UserRegisterRequest request);

    JsonData login(UserLoginRequest request);

    UserVO findUserDetail();
}
