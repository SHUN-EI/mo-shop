drop table if exists  product_task;
CREATE TABLE product_task
(
    id           bigint(11)  unsigned NOT NULL AUTO_INCREMENT,
    product_id   bigint(11)   DEFAULT NULL COMMENT '商品id',
    buy_num      int(11)      DEFAULT NULL COMMENT '购买数量',
    product_name varchar(128) DEFAULT NULL COMMENT '商品名称',
    lock_state   varchar(32)  DEFAULT NULL COMMENT '锁定状态，锁定-LOCK,完成-FINISH,取消CANCEL',
    out_trade_no varchar(32)  DEFAULT NULL COMMENT  '订单唯一标识',
    create_time  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB  DEFAULT CHARSET = utf8mb4 comment ='商品库存锁定任务表';