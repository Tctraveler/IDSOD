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

create table consequence(
    id int unsigned primary key auto_increment comment 'ID',
    patient_id int unsigned comment '用户id',
    consequence varchar(1024) default '' comment '医嘱',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '修改时间',
    constraint fk_p_id_1 foreign key (patient_id) references user(id) on delete cascade on update cascade
)comment '医嘱';