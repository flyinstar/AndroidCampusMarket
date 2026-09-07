-- =============================================================
-- 校园二手交易平台 数据库初始化脚本（schema）
-- 对应《开发文档.md》第二部分
-- 适用 MySQL 5.7+/8.0/8.4/9.x  (utf8mb4)
-- 执行方式: mysql -uroot -p < schema.sql
-- =============================================================
CREATE DATABASE IF NOT EXISTS campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_trade;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱（登录账号）',
  `password_hash` CHAR(64) NOT NULL COMMENT 'SHA-256加密密码',
  `salt` CHAR(10) NOT NULL COMMENT '随机盐值（用于密码加密）',
  `nickname` VARCHAR(50) DEFAULT '校园用户' COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `phone` VARCHAR(15) DEFAULT NULL COMMENT '手机号',
  `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级（如：2023级）',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `credit_score` INT(11) DEFAULT 100 COMMENT '信用分（初始100）',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0禁用 1正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` INT(11) DEFAULT 0 COMMENT '父级分类ID（0为顶级）',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 初始化分类数据（幂等：仅当分类表为空时插入）
INSERT INTO `category` (`name`, `parent_id`, `sort_order`)
SELECT tmp.name, tmp.parent_id, tmp.sort_order FROM (
  SELECT '教材教辅' AS name, 0 AS parent_id, 1 AS sort_order
  UNION ALL SELECT '电子产品', 0, 2
  UNION ALL SELECT '生活用品', 0, 3
  UNION ALL SELECT '服饰鞋包', 0, 4
  UNION ALL SELECT '运动户外', 0, 5
  UNION ALL SELECT '其他', 0, 99
) tmp
WHERE NOT EXISTS (SELECT 1 FROM `category`);

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL COMMENT '商品标题',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格（元）',
  `user_id` INT(11) NOT NULL COMMENT '发布者ID',
  `category_id` INT(11) NOT NULL COMMENT '分类ID',
  `condition` TINYINT(1) DEFAULT 3 COMMENT '新旧程度：1全新 2几乎全新 3有使用痕迹 4老旧',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0下架 1上架中 2已预约 3已售出',
  `view_count` INT(11) DEFAULT 0 COMMENT '浏览量',
  `favorite_count` INT(11) DEFAULT 0 COMMENT '收藏数',
  `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '发布位置纬度（可选）',
  `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '发布位置经度（可选）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_product_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品图片表
CREATE TABLE IF NOT EXISTS `product_image` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `product_id` INT(11) NOT NULL COMMENT '商品ID',
  `image_url` VARCHAR(255) NOT NULL COMMENT '图片URL',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序（第一张为封面）',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_image_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order_info` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（唯一）',
  `product_id` INT(11) NOT NULL COMMENT '商品ID',
  `buyer_id` INT(11) NOT NULL COMMENT '买家ID',
  `seller_id` INT(11) NOT NULL COMMENT '卖家ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '成交价格',
  `status` TINYINT(1) DEFAULT 0 COMMENT '0待确认 1交易中 2已完成 3已取消',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `meeting_time` DATETIME DEFAULT NULL COMMENT '约定见面时间',
  `meeting_place` VARCHAR(255) DEFAULT NULL COMMENT '约定见面地点',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`),
  CONSTRAINT `fk_order_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `product_id` INT(11) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_fav_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `product_id` INT(11) NOT NULL,
  `user_id` INT(11) NOT NULL COMMENT '评论者ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_comment_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS `chat_message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID（用于轮询分页）',
  `from_user_id` INT(11) NOT NULL COMMENT '发送者ID',
  `to_user_id` INT(11) NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '消息内容（支持文本和图片链接）',
  `msg_type` TINYINT(1) DEFAULT 0 COMMENT '0文本 1图片',
  `is_read` TINYINT(1) DEFAULT 0 COMMENT '0未读 1已读',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_from_to` (`from_user_id`, `to_user_id`),
  KEY `idx_to_read` (`to_user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';
