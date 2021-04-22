package com.mo.enums;

import lombok.Getter;

/**
 * Created by mo on 2021/4/22
 * 收货地址状态
 */
public enum AddressStatusEnum {

    /**
     * 默认收货地址
     */
    DEFAULT_STATUS(1),

    /**
     * 非默认收货地址
     */
    COMMON_STATUS(0);

    @Getter
    private int status;

    private AddressStatusEnum(int status) {
        this.status = status;
    }

}
