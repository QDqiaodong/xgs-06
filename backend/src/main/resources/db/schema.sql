CREATE DATABASE IF NOT EXISTS leather_craft DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE leather_craft;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS t_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  nickname VARCHAR(50),
  avatar VARCHAR(500),
  password VARCHAR(100) NOT NULL,
  bio VARCHAR(500),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  type VARCHAR(20) NOT NULL COMMENT 'category-品类, craft-工艺',
  icon VARCHAR(200),
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_type (type),
  INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_work (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  cover VARCHAR(500),
  content TEXT,
  materials TEXT,
  craft_steps TEXT,
  category_id BIGINT,
  craft_type_id BIGINT,
  view_count INT DEFAULT 0,
  favorite_count INT DEFAULT 0,
  status TINYINT DEFAULT 1 COMMENT '1-发布, 0-下架',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_user_id (user_id),
  INDEX idx_category_id (category_id),
  INDEX idx_craft_type_id (craft_type_id),
  INDEX idx_status (status),
  INDEX idx_create_time (create_time),
  INDEX idx_view_count (view_count),
  INDEX idx_favorite_count (favorite_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_work_image (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  type TINYINT DEFAULT 1 COMMENT '1-成品图, 2-过程图',
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_work_id (work_id),
  INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_favorite (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  work_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_work (user_id, work_id),
  INDEX idx_user_id (user_id),
  INDEX idx_work_id (work_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO t_category (name, type, sort) VALUES
('钱包', 'category', 1),
('卡包', 'category', 2),
('皮带', 'category', 3),
('手拿包', 'category', 4),
('单肩包', 'category', 5),
('托特包', 'category', 6),
('钥匙包', 'category', 7),
('护照夹', 'category', 8),
('笔记本封皮', 'category', 9),
('其他', 'category', 99);

INSERT INTO t_category (name, type, sort) VALUES
('新手教程', 'craft', 1),
('基础缝制', 'craft', 2),
('皮料裁切', 'craft', 3),
('边缘处理', 'craft', 4),
('封边上色', 'craft', 5),
('五金安装', 'craft', 6),
('起鼓塑形', 'craft', 7),
('皮雕工艺', 'craft', 8),
('编织工艺', 'craft', 9),
('进阶工艺', 'craft', 10);

INSERT INTO t_user (username, nickname, password, bio) VALUES
('admin', '皮艺达人', '123456', '热爱手工皮具，专注植鞣革创作');

INSERT INTO t_work (user_id, title, cover, content, materials, craft_steps, category_id, craft_type_id, view_count, favorite_count, status) VALUES
(1, '手工植鞣革短夹制作教程', 'https://picsum.photos/400/300?random=1', 
 '分享一款简约风格的植鞣革短夹制作全过程，适合新手入门学习。', 
 '意大利植鞣革2.0mm，麻线，黄铜四合扣', 
 '1. 裁下料片\n2. 打斩\n3. 缝制\n4. 封边\n5. 安装五金',
 1, 1, 128, 45, 1),
(1, '复古风卡包制作分享', 'https://picsum.photos/400/300?random=2',
 '做了一个复古风格的多卡位卡包，可以放8张卡，还有一个零钱位。',
 '日本枥木皮1.5mm，酒红色，铜质气眼',
 '1. 设计版型\n2. 裁切皮料\n3. 制作卡位\n4. 组合缝制\n5. 磨边封边',
 2, 3, 256, 89, 1),
(1, '手工皮带的封边技巧', 'https://picsum.photos/400/300?random=3',
 '很多朋友问封边怎么才能做到镜面效果，这里分享一下我的经验。',
 '意大利植鞣革皮带条，床面处理剂，砂纸400-2000目',
 '1. 裁皮带\n2. 削薄尾部\n3. 粗磨\n4. 床面处理\n5. 细磨\n6. 抛光',
 3, 5, 512, 156, 1);

INSERT INTO t_work_image (work_id, image_url, type, sort) VALUES
(1, 'https://picsum.photos/600/450?random=11', 1, 0),
(1, 'https://picsum.photos/600/450?random=12', 1, 1),
(1, 'https://picsum.photos/600/450?random=13', 2, 0),
(1, 'https://picsum.photos/600/450?random=14', 2, 1),
(2, 'https://picsum.photos/600/450?random=21', 1, 0),
(2, 'https://picsum.photos/600/450?random=22', 1, 1),
(2, 'https://picsum.photos/600/450?random=23', 2, 0),
(3, 'https://picsum.photos/600/450?random=31', 1, 0),
(3, 'https://picsum.photos/600/450?random=32', 2, 0),
(3, 'https://picsum.photos/600/450?random=33', 2, 1);
