create database idsod;
use idsod;
create table user
(
    id          int unsigned primary key auto_increment comment 'ID',
    username    varchar(20)  not null unique comment '用户名',
    password    varchar(128) not null comment '密码',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '更新时间'
)comment '用户表';