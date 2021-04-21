package com.mo.service;

import com.mo.request.UserRegisterRequest;
import com.mo.utils.JsonData;

/**
 * Created by mo on 2021/4/21
 */
public interface UserService {

    JsonData register(UserRegisterRequest request);
}
