-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE seckill;
-- 书用数据库
use seckill;
-- 创建秒杀库存表
CREATE TABLE seckill (
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Item ID',
`name` VARCHAR(120) NOT NULL COMMENT 'Item Name',
`number` int NOT NULL COMMENT 'Item Amount',
`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
`start_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Start Time',
`end_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'End Time',
PRIMARY KEY (seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='Item Table';

-- 初始化数据
INSERT INTO seckill(name, number, start_time,end_time)
VALUES
    ('100RMB for IPhone6', 100, '2017-12-31 00:00:00', '2018-01-01 00:00:00'),
    ('200RMB for IPad2', 200, '2017-12-31 00:00:00', '2018-01-01 00:00:00'),
    ('300RMB for IPods', 300, '2017-12-31 00:00:00', '2018-01-01 00:00:00'),
    ('400RMB for IMac', 400, '2017-12-31 00:00:00', '2018-01-01 00:00:00'),
    ('PAST', 100, '2016-01-01 00:00:00', '2016-01-02 00:00:00'),
    ('NOW', 100, '2017-01-01 00:00:00', '2018-01-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息
CREATE TABLE success_killed (
`seckill_id` bigint NOT NULL COMMENT '秒杀商品ID',
`user_phone` bigint NOT NULL COMMENT '用户手机号',
`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态标示: -1 -> 无效; 0 -> 成功; 1 -> 已付款; 2 -> 已发货',
`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id, user_phone), /*联合主键*/
KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 连接数据库控制台
mysql -uroot -p

-- Useful commands
-- 1. show tables;
-- 2. show create table seckill\G;

-- 为什么手写DDL
-- 记录每次上线的DDL修改
-- 上线V1.1
ALTER TABLE seckill
DROP INDEX idx_create_time,
ADD INDEX idx_c_s(start_time, create_time);

-- 上线V.2
-- DDL

-- use docker to set up mysql:
-- docker run -d -e MYSQL_ROOT_PASSWORD=root —name myLocalDB -p 3306:3306 mysql
-- docker exec -it myLocalDB ./bin/bash
-- mysql -u root -p