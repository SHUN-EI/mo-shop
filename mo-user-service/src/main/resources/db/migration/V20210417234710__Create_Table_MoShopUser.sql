drop  table  if exists moshop_user;

create table  moshop_user (
    id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    user_name  varchar(128) DEFAULT NULL COMMENT '用户名',
    password  varchar(124) DEFAULT NULL COMMENT '密码',
    head_img  varchar(524) DEFAULT NULL COMMENT '头像',
    slogan  varchar(524) DEFAULT NULL COMMENT '用户签名',
    sex tinyint(2) DEFAULT '1' COMMENT '0表示女，1表示男',
    points  int(10) DEFAULT '0' COMMENT '积分',
    mail  varchar(64) DEFAULT NULL COMMENT '邮箱',
    secret varchar(12) DEFAULT NULL COMMENT '盐，用于个人敏感信息处理',
    create_time  datetime DEFAULT NULL COMMENT '创建时间',
    update_time datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY  mail_idx (mail)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;