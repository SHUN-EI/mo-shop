drop table if exists mp_order_detail;
CREATE TABLE mp_order_detail
(
    id               bigint(11)     unsigned NOT NULL AUTO_INCREMENT COMMENT '订单详情id',
    product_order_id bigint(11)     DEFAULT NULL COMMENT '订单号',
    out_trade_no     varchar(32)    DEFAULT NULL COMMENT '订单唯一标识',
    product_id       bigint(11)     DEFAULT NULL COMMENT '产品id',
    product_name     varchar(128)   DEFAULT NULL COMMENT '商品名称',
    product_img      varchar(524)   DEFAULT NULL COMMENT '商品图片',
    buy_num          int(11)        DEFAULT NULL COMMENT '购买数量',
    amount           decimal(16, 0) DEFAULT NULL COMMENT '购物项商品单价',
    total_amount     decimal(16, 2) DEFAULT NULL COMMENT '购物项商品总价格',
    create_time      datetime       DEFAULT NULL COMMENT '创建时间',
    update_time      datetime       DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 comment ='订单详情表';