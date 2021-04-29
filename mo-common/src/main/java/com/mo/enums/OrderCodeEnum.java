package com.mo.enums;

import com.mo.constant.OrderCodeConstant;
import lombok.Data;
import lombok.Getter;

/**
 * Created by mo on 2021/4/29
 * 订单号类型
 * 可以自定义不同类型的订单，去定制不同的订单号，最大支持亿级订单数量
 */
@Getter
public enum OrderCodeEnum {

    /**
     * 订单
     * 自定义订单号格式为
     * 第一种:2位订单类型简称+12位日期数+6位流水号+4位随机数=24位订单号
     * 自动生成的订单号:XD2104291129420000006763
     * 这种格式，日期是  210501102836(8位日期数)，再加4位随机数，可以保证订单号肯定是全局唯一的
     * 但是当订单量大的时候，有可能会出现redis里面存在大量key的情况，会导致redis存储容量不够
     * <p>
     * 第二种：2位订单类型简称+6位日期数+8位流水号+4位随机数=20位订单号
     * <p>
     * 8位流水号:00000000,最高可以支持1亿订单号，
     * 自动生成的订单号:XD210429000000018073
     * redis里面的key是 ORDER_CODE_CACHE:210501(6位日期数)，可以方便统计每一天的订单总数
     */
    XD("XD", OrderCodeConstant.SERIAL_YYMMDD_PREFIX, 8, 4, 20);

    /**
     * 订单号前缀
     * 为空时填""
     */
    private String prefix;

    /**
     * 时间格式表达式
     * 如:yyyyMMdd
     */
    private String datePattern;

    /**
     * 流水号长度
     */
    private Integer serialLength;

    /**
     * 随机数长度
     */
    private Integer randomLength;

    /**
     * 总长度
     */
    private Integer totalLength;

    OrderCodeEnum(String prefix, String datePattern, Integer serialLength,
                  Integer randomLength, Integer totalLength) {

        this.prefix = prefix;
        this.datePattern = datePattern;
        this.serialLength = serialLength;
        this.randomLength = randomLength;
        this.totalLength = totalLength;
    }
}
