-- 注意此处0.7.0+ 增加字段 context
CREATE TABLE undo_log
(
    id            bigint(20)   NOT NULL AUTO_INCREMENT,
    branch_id     bigint(20)   NOT NULL COMMENT '分支事务id',
    xid           varchar(100) NOT NULL COMMENT '全局事务的编号id',
    context       varchar(128) NOT NULL,
    rollback_info longblob     NOT NULL,
    log_status    int(11)      NOT NULL,
    log_created   datetime     NOT NULL,
    log_modified  datetime     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;