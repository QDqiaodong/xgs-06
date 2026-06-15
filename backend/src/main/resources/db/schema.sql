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

CREATE TABLE IF NOT EXISTS t_work_step (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  step_name VARCHAR(100) NOT NULL COMMENT '步骤名称',
  step_type VARCHAR(50) COMMENT '步骤类型: cutting-裁切, sewing-缝制, edge-封边, hardware-五金安装, shaping-塑形, carving-皮雕, other-其他',
  materials VARCHAR(500) COMMENT '本步骤材料',
  tips VARCHAR(1000) COMMENT '注意点',
  description TEXT COMMENT '步骤描述',
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_work_id (work_id),
  INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_work_image (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  step_id BIGINT COMMENT '关联工序步骤ID，NULL表示作品级别图片',
  image_url VARCHAR(500) NOT NULL,
  type TINYINT DEFAULT 1 COMMENT '1-成品图, 2-过程图',
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_work_id (work_id),
  INDEX idx_step_id (step_id),
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

INSERT INTO t_work_step (work_id, step_name, step_type, materials, tips, description, sort) VALUES
(1, '裁切下料', 'cutting', '意大利植鞣革2.0mm，裁皮刀，钢尺', '注意刀刃角度保持45度，沿钢尺边缘垂直下刀，一次性裁断避免毛边', '根据纸样裁下料片，注意皮料的纹理方向，大面和卡位要合理布局节省皮料', 0),
(1, '打斩', 'other', '菱斩4mm，橡胶垫，锤子', '斩齿要垂直于皮面，锤子力度均匀，相邻斩孔要对齐', '沿缝合线打斩，间距保持均匀，转弯处注意斩的角度', 1),
(1, '缝制', 'sewing', '麻线0.6mm，手缝针两只，蜂蜡', '双针马鞍缝法，每针拉线力度一致，起针收针要回缝加固', '采用双针缝法，从一端开始缝制，注意线迹整齐美观', 2),
(1, '封边上色', 'edge', '砂纸400-1200目，床面处理剂，边油封边液', '每一遍都要等干透再打磨，耐心是关键，至少重复5-8遍', '先用粗砂纸磨平边缘，床面处理剂填充，逐级打磨到镜面效果，最后上边油', 3),
(1, '五金安装', 'hardware', '黄铜四合扣，安装冲子，锤子', '定位要准确，冲子垂直，力度适中，避免砸坏五金', '标记好四合扣位置，用冲子打孔，安装上下扣，确保扣合牢固', 4);

INSERT INTO t_work_step (work_id, step_name, step_type, materials, tips, description, sort) VALUES
(2, '设计版型', 'other', '卡纸，铅笔，橡皮，尺子', '多做几个纸样试折，确认尺寸和结构无误再下料', '根据需求设计卡包版型，8卡位+1零钱位，注意折叠厚度', 0),
(2, '裁切皮料', 'cutting', '日本枥木皮1.5mm酒红色，裁皮刀，钢尺', '枥木皮纹理细腻，注意顺纹裁切，卡位要裁得方正', '按照纸样裁切外皮、内皮、各卡位皮片', 1),
(2, '制作卡位', 'sewing', '麻线，手缝针，定位夹', '卡位边缘先削薄，缝合后整体更平整', '逐个制作卡位，先粘后缝，确保每个卡位尺寸标准', 2),
(2, '组合缝制', 'sewing', '麻线，手缝针，黄胶', '先临时粘合定位，检查无误再正式缝制', '将各部件组合，先粘后打斩缝制，注意对整齐', 3),
(2, '磨边封边', 'edge', '砂纸，床面处理剂，抛光棒', '封边过程中注意保持边缘湿润，打磨出镜面效果', '所有边缘依次打磨、封边，最终抛光处理', 4);

INSERT INTO t_work_step (work_id, step_name, step_type, materials, tips, description, sort) VALUES
(3, '裁皮带', 'cutting', '意大利植鞣革皮带条38mm宽，裁皮刀', '皮带条要选纹理顺直的部位，两边裁切平行', '根据腰围裁切皮带长度，预留扣头和尾端余量', 0),
(3, '削薄尾部', 'other', '削边器，宽铲', '逐渐过渡削薄，避免削出明显台阶', '皮带尾端2cm处削薄至1.0mm左右，方便穿过皮带扣', 1),
(3, '粗磨', 'edge', '砂纸400目，800目', '顺着边缘来回打磨，不要倾斜', '先用400目粗磨边缘，再用800目细磨，直到边缘平整光滑', 2),
(3, '床面处理', 'edge', '床面处理剂（CMC），木棒', '薄涂多次，不要一次涂太厚', '涂抹床面处理剂，待半干时用圆木棒反复压实边缘', 3),
(3, '细磨', 'edge', '砂纸1200目，2000目', '逐级递增砂纸目数，每遍干透再打磨', '依次用1200目、2000目砂纸细磨，每次打磨后重新上处理剂', 4),
(3, '抛光', 'edge', '抛光蜡，帆布或羊皮', '快速来回摩擦产生热量，帮助蜡融化渗透', '涂少量抛光蜡，用帆布快速摩擦边缘，直到产生镜面光泽', 5);

INSERT INTO t_work_image (work_id, step_id, image_url, type, sort) VALUES
(1, NULL, 'https://picsum.photos/600/450?random=11', 1, 0),
(1, NULL, 'https://picsum.photos/600/450?random=12', 1, 1),
(1, 1, 'https://picsum.photos/600/450?random=101', 2, 0),
(1, 2, 'https://picsum.photos/600/450?random=102', 2, 0),
(1, 3, 'https://picsum.photos/600/450?random=103', 2, 0),
(1, 4, 'https://picsum.photos/600/450?random=104', 2, 0),
(1, 5, 'https://picsum.photos/600/450?random=105', 2, 0),
(2, NULL, 'https://picsum.photos/600/450?random=21', 1, 0),
(2, NULL, 'https://picsum.photos/600/450?random=22', 1, 1),
(2, 1, 'https://picsum.photos/600/450?random=201', 2, 0),
(2, 2, 'https://picsum.photos/600/450?random=202', 2, 0),
(2, 3, 'https://picsum.photos/600/450?random=203', 2, 0),
(2, 4, 'https://picsum.photos/600/450?random=204', 2, 0),
(2, 5, 'https://picsum.photos/600/450?random=205', 2, 0),
(3, NULL, 'https://picsum.photos/600/450?random=31', 1, 0),
(3, 1, 'https://picsum.photos/600/450?random=301', 2, 0),
(3, 2, 'https://picsum.photos/600/450?random=302', 2, 0),
(3, 3, 'https://picsum.photos/600/450?random=303', 2, 0),
(3, 4, 'https://picsum.photos/600/450?random=304', 2, 0),
(3, 5, 'https://picsum.photos/600/450?random=305', 2, 0),
(3, 6, 'https://picsum.photos/600/450?random=306', 2, 0);
