package com.mo.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

/**
 * Created by mo on 2021/5/3
 */
public class AlipayConfig {


    /**
     * 支付宝 APPID
     */
    public static final String APP_ID = "2016102400748421";

    /**
     * 支付宝网关地址
     */
    public static final String PAY_GATEWAY = "https://openapi.alipaydev.com/gateway.do";

    /**
     * 签名类型
     */
    public static final String SIGN_TYPE = "RSA2";

    /**
     * 字符编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 返回参数格式
     */
    public static final String FORMAT = "json";

    /**
     * 应用私钥
     */
    public static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQD4MT2PCCuThtdhgjlTwsxKw4NaAMmn1KgqScRif/lBbBHvskHeykcPIYF+KMWctIsxMS6P9BwCa0wF4Tim0f3ehmhWlJF0umKiawhtMqCjsOO9I4X/WkyybJNfQtmXnJZAEC1nNsGj7bh5kUH0gEOuC7MVYxQ8pmxSsALVJNY/EvYAf6yEDH0LdPJ/oiEQgREDQdR5+uSnzz6mvPv7GtBVSTCG7KofvGUVGWNXCyV0XBnMUbhFIylze4JwKGLYRi+sUire7Khy+25bh9X//OkY41135gHnIfIs1drZpVuwzM6rMuHgOf2TQ+qispXUj3rvtBx+t2FlZCIKvP0ByGpHAgMBAAECggEAGQhOXyjBzGVjP9B0m8Af/sJtcxx1Olo2g91g01Z4+2I9BOIDxsa8GAiXGPRfyghjqZh+S8KhOJU927H1ls3DPzhrOaBOc2mBHUSSWwEqNphWv/LfRvwClFRjIM6LR8FhXiDDk49wRGprmsdN0a1GJj3D2DpgBiX68vQRtRG8EixSjnQzFVjx3rO07rilP6hMAkBmuOJW7Qg6iQUzhIMPayXFuHDQK9OhqhzThcITbVJDFCLCJKk+iqPOZDOvD9BgiX+PdmEx1eM/cyLrUN1A0GtHcY0jBFnJ57BSSC0ufcYnMf+qsTw0h6K0KJz4caavfWH/lLg+yMEQXgQwD8ovkQKBgQD9j9PXPNovSitKDkhi3TfW8i+1aNuWS1jmrkshFuJNM6z1Cb6n7KrtXB0/h1sLkz08/uWoZnlKrnXNxNYGrvma3MXLhp54dbxKic2EH2ctRgymcGGgByDR0P0d8BRMpqYo+65u+K4FYWyjxUSXpv4rRyepYfk/FcBzsuBBqftC9QKBgQD6lDICTIo/04HAhAjM1Aeh6spbb0VEoDGTzCYUdEVffgyrGSkHDfkqWgB9321w43TTaiBzqJ8OkOEa/c/fAgyqHdrgx74bRBRA7cDi40YecNmp6B+96lpFZ3TlK+RM+CfZWDA5jOK7ZWFkoqwmqieJSOhHKmHSpSjJ4GZfUsHKywKBgQDVCMA9OF1AGhqgz7s1hRNjPnui+QR1E70PoyRwGp/rqR0JizRqNceuQRQ5yelOolhzJN2jTOVfP5CuX1BKsTv5Rr/QLbx7DMA0B+9psFCpkw8Rz53WK+cBIYtoWctUUzeLe1mk1r6uupniRt1IZP9rMQ87mpsky4u0KXeM89ms+QKBgAJGZCnXLg4kSnJIAJhzeFpE+m9YFEXqOmkAGnxnDdYvG8hV4yR4QQHcbVK3O5QhzIa62O6T4lXMhO6szT1WFG1a9gjCZ3xx4skwH2cv7wwJtEAxplAgF4+3v7zw6BK20g3MGiv7lndjFT5Hp852DwPVEJ7MyLST4Unr6qIE61N/AoGBAKpgfhvGkFNpyLXoegThho7JF9prjkVUkatWeF4a60DCDliDidt41ww/hJYWFtJ347GGjtZ0SajqtjS5msWKxnPvj6B1r2otMtalcpNf6BD+3/36vJTyN0Jl1js8VwPxYVX0oB4iP7YJS4v6w201yHQEys7CWfoI0QYLquR+5Eaf";

    /**
     * 支付宝公钥
     */
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu4W7GtyEfTW6D5NKPxAT9CX9VSsjrgj7pUxCPOy7UF3LbB7gv4UmB2XEAyE3c22y0GoF7VGIAlLzUdHXPNDQrF+C+4yZsgAQE/QgO8jJiEnmtm4R/QpXJc7M48vIY6ZRPOpFl1QlOkUTfLf+H/J8WVZMSjKfHmiAXDUa9zq/NpFGJJGG/ntziP+l4dBz98dO0OBh4fTB34uVTHnXmMnw1YXMAvm/aIYwhjYxV4XWecSTcIPDG5mSQ1kxmfXl2S5LtVnyOi5aBnzGFvOAIrslMK5mS3pT95LhUp2FCeQNR36ubIvhzTE+3dcG8gSwkIsFUKRYkn4uSgBV1JeNw83cwQIDAQAB";

    /**
     * 构造函数私有化
     */
    private AlipayConfig() {
    }

    /**
     * volatile禁止指令重排
     */
    private volatile static AlipayClient instance = null;

    /**
     * 单例模式获取，双重检查锁定 （Double-Checked-Locking）
     *
     * @return
     */
    public static AlipayClient getInstance() {
        if (instance == null) {
            synchronized (AlipayConfig.class) {
                if (instance == null) {
                    instance = new DefaultAlipayClient(PAY_GATEWAY, APP_ID, APP_PRIVATE_KEY,
                            FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
                }
            }
        }
        return instance;
    }

}
