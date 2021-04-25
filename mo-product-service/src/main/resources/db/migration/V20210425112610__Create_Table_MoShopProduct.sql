drop table if exists mp_product;
CREATE TABLE mp_product
(
    id          bigint(11)     unsigned NOT NULL AUTO_INCREMENT COMMENT '商品id',
    title       varchar(128)   DEFAULT NULL COMMENT '标题',
    cover_img   varchar(128)   DEFAULT NULL COMMENT '封面图',
    detail      varchar(256)   DEFAULT '' COMMENT '详情',
    old_price   decimal(16, 2) DEFAULT NULL COMMENT '老价格',
    price       decimal(16, 2) DEFAULT NULL COMMENT '新价格',
    stock       int(11)        DEFAULT NULL COMMENT '库存',
    lock_stock  int(11)        DEFAULT '0' COMMENT '锁定库存',
    create_time datetime       DEFAULT NULL COMMENT '创建时间',
    update_time datetime       DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 comment ='商品表';