-- auto-generated definition

create table t_user
(
    id             bigint auto_increment comment 'id'
        primary key,
    userAccount    varchar(20) collate utf8mb4_bin        not null,
    nickName       varchar(30) collate utf8mb4_bin        null comment '用户昵称',
    userPassword   varchar(60)                            null comment '密码',
    userPhone      varchar(20)                            null comment '用户手机号(后期允许拓展区号和国际号码）',
    userEmail      varchar(254) collate utf8mb4_bin       null comment '用户邮箱',
    userGender     int                                    not null comment '用户性别',
    userAvatar     varchar(1024)                          null comment '用户头像',
    userProfile    varchar(512)                           null comment '用户简介',
    userRole       varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除',
    constraint unique_userInfo unique (userAccount, userEmail)
) comment '用户' collate = utf8mb4_unicode_ci;