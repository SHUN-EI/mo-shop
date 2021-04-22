drop table if exists mp_coupon;
CREATE TABLE mp_coupon
(
    id              bigint(20)     NOT NULL AUTO_INCREMENT COMMENT 'id',
    category        varchar(11)    DEFAULT NULL COMMENT '优惠卷类型[NEW_USER-注册赠券，TASK-任务卷，PROMOTION-促销劵]',
    publish         varchar(11)    DEFAULT NULL COMMENT '发布状态, PUBLISH-发布，DRAFT-草稿，OFFLINE-下线',
    coupon_img      varchar(524)   DEFAULT NULL COMMENT '优惠券图片',
    coupon_title    varchar(128)   DEFAULT NULL COMMENT '优惠券标题',
    price           decimal(16, 2) DEFAULT NULL COMMENT '抵扣价格',
    user_limit      int(11)        DEFAULT NULL COMMENT '每人限制张数',
    start_time      datetime       DEFAULT NULL COMMENT '优惠券开始有效时间',
    end_time        datetime       DEFAULT NULL COMMENT '优惠券失效时间',
    publish_count   int(11)        DEFAULT NULL COMMENT '优惠券总量',
    stock           int(11)        DEFAULT '0' COMMENT  '库存',
    condition_price decimal(16, 2) DEFAULT NULL COMMENT '满多少才可以使用',
    create_time     datetime       DEFAULT NULL COMMENT '创建时间',
    update_time     datetime       DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='优惠券表';