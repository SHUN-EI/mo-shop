drop table if exists mp_coupon_record;
CREATE TABLE mp_coupon_record
(
    id              bigint(11)     unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    coupon_id       bigint(11)     DEFAULT NULL COMMENT '优惠券id',
    use_state       varchar(32)    DEFAULT NULL COMMENT '使用状态：NEW-可用,USED-已使用,EXPIRED-过期;',
    user_id         bigint(11)     DEFAULT NULL COMMENT '用户id',
    user_name       varchar(128)   DEFAULT NULL COMMENT '用户名',
    coupon_title    varchar(128)   DEFAULT NULL COMMENT '优惠券标题',
    start_time      datetime       DEFAULT NULL COMMENT '开始时间',
    end_time        datetime       DEFAULT NULL COMMENT '结束时间',
    order_id        bigint(11)     DEFAULT NULL COMMENT '订单id',
    price           decimal(16, 2) DEFAULT NULL COMMENT '抵扣价格',
    condition_price decimal(16, 2) DEFAULT NULL COMMENT '满多少才可以使用',
    create_time     datetime       DEFAULT NULL COMMENT '创建时间获得时间',
    update_time     datetime       DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='优惠券领劵记录表';