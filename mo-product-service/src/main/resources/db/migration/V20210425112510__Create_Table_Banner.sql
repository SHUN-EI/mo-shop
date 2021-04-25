drop table if exists banner;
CREATE TABLE banner
(
    id     int(11)      unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    img    varchar(524) DEFAULT NULL COMMENT '图片',
    url    varchar(524) DEFAULT NULL COMMENT '跳转地址',
    weight int(11)      DEFAULT NULL COMMENT '权重',
    create_time         datetime       DEFAULT NULL COMMENT '创建时间',
    update_time         datetime       DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='商品轮播图表' ;