package com.mo.service;

import com.mo.enums.SendCodeEnum;
import com.mo.utils.JsonData;

/**
 * Created by mo on 2021/4/20
 */
public interface NotifyService {

    JsonData sendCode(SendCodeEnum sendCodeEnum, String to);
}
