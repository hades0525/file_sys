create table if not exists file_metadata
(
    id                varchar(32)  not null comment '主键ID'
        primary key,
    filename          varchar(100) not null comment '文件名',
    original_filename varchar(100) not null comment '原始文件名',
    file_size         bigint       not null comment '文件大小',
    file_path         varchar(100) not null comment '文件存放路径',
    content_type      varchar(100) not null comment 'ContentType',
    create_time       date         not null comment '创建时间',
    create_user       varchar(32)  null comment '文件所有者',
    remark            varchar(200) null comment '备注'
)
    comment '文件元数据信息' charset = utf8;

create table if not exists file_relation_ship
(
    id      bigint auto_increment comment '主键ID'
        primary key,
    biz_id  varchar(100) not null comment '业务主键',
    file_id varchar(32)  not null comment '文件ID'
)
    comment '文件关联关系表' charset = utf8;

