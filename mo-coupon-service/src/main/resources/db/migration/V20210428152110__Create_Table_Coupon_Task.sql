drop table if exists coupon_task;
CREATE TABLE coupon_task
(
    id                bigint(11) unsigned NOT NULL AUTO_INCREMENT,
    coupon_record_id  bigint(11)  DEFAULT NULL COMMENT '优惠券记录id',
    out_trade_no      varchar(64) DEFAULT NULL COMMENT '订单唯一标识',
    lock_state        varchar(32) DEFAULT NULL COMMENT '锁定状态，锁定-LOCK,完成-FINISH,取消CANCEL',
    create_time       datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4 comment ='优惠券库存锁定任务表';