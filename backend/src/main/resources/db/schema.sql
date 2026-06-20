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
  enabled TINYINT DEFAULT 1 COMMENT '1-启用, 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_type (type),
  INDEX idx_sort (sort),
  UNIQUE KEY uk_type_name (type, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_work (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  cover VARCHAR(500),
  content TEXT,
  materials TEXT,
  material_summary TEXT COMMENT '材料用量速览JSON: {mainMaterials, auxMaterials, tools}',
  material_brief VARCHAR(200) COMMENT '简短材料摘要文本，供列表展示',
  craft_steps TEXT,
  category_id BIGINT,
  craft_type_id BIGINT,
  difficulty VARCHAR(20) COMMENT '版型难度: beginner-入门, intermediate-进阶, advanced-复杂',
  view_count INT DEFAULT 0,
  favorite_count INT DEFAULT 0,
  status TINYINT DEFAULT 1 COMMENT '1-发布, 0-下架',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_user_id (user_id),
  INDEX idx_category_id (category_id),
  INDEX idx_craft_type_id (craft_type_id),
  INDEX idx_difficulty (difficulty),
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
  time_cost INT DEFAULT 0 COMMENT '步骤耗时(分钟)',
  materials VARCHAR(500) COMMENT '本步骤材料',
  tips VARCHAR(1000) COMMENT '关键注意点',
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

CREATE TABLE IF NOT EXISTS t_retrospective (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL COMMENT '关联作品ID',
  user_id BIGINT NOT NULL COMMENT '作者用户ID',
  rework_points TEXT COMMENT '返工点：需要返工的环节与问题',
  loss_reasons TEXT COMMENT '损耗原因：皮料/五金等损耗成因分析',
  improvements TEXT COMMENT '下次改进方向：针对性改进建议',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  UNIQUE KEY uk_work_id (work_id),
  INDEX idx_user_id (user_id),
  INDEX idx_update_time (update_time)
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

INSERT INTO t_work (user_id, title, cover, content, materials, material_summary, material_brief, craft_steps, category_id, craft_type_id, difficulty, view_count, favorite_count, status) VALUES
(1, '手工植鞣革短夹制作教程', 'https://picsum.photos/400/300?random=1', 
 '分享一款简约风格的植鞣革短夹制作全过程，适合新手入门学习。', 
 '意大利植鞣革2.0mm，麻线，黄铜四合扣',
 '{"mainMaterials":[{"name":"意大利植鞣革","spec":"2.0mm","quantity":"1.5平方英尺"}],"auxMaterials":[{"name":"麻线","spec":"0.6mm","quantity":"3米"},{"name":"黄铜四合扣","spec":"12mm","quantity":"2套"},{"name":"黄胶","spec":"","quantity":"少量"}],"tools":[{"name":"裁皮刀","spec":"","quantity":"1把"},{"name":"菱斩","spec":"4mm","quantity":"1把"},{"name":"手缝针","spec":"","quantity":"2支"},{"name":"锤子","spec":"","quantity":"1把"},{"name":"橡胶垫","spec":"","quantity":"1块"}]}',
 '主材：意大利植鞣革2.0mm；辅材：麻线0.6mm、黄铜四合扣12mm、黄胶；工具：裁皮刀、菱斩4mm、手缝针、锤子、橡胶垫',
 '1. 裁下料片\n2. 打斩\n3. 缝制\n4. 封边\n5. 安装五金',
 1, 1, 'intermediate', 128, 45, 1),
(1, '复古风卡包制作分享', 'https://picsum.photos/400/300?random=2',
 '做了一个复古风格的多卡位卡包，可以放8张卡，还有一个零钱位。',
 '日本枥木皮1.5mm，酒红色，铜质气眼',
 '{"mainMaterials":[{"name":"日本枥木皮","spec":"1.5mm 酒红色","quantity":"1平方英尺"}],"auxMaterials":[{"name":"麻线","spec":"0.45mm","quantity":"2米"},{"name":"铜质气眼","spec":"5mm","quantity":"8个"},{"name":"拉链","spec":"10cm","quantity":"1条"}],"tools":[{"name":"裁皮刀","spec":"","quantity":"1把"},{"name":"菱斩","spec":"3mm","quantity":"1把"},{"name":"手缝针","spec":"","quantity":"2支"},{"name":"打孔冲","spec":"5mm","quantity":"1支"}]}',
 '主材：日本枥木皮1.5mm 酒红色；辅材：麻线0.45mm、铜质气眼5mm、拉链10cm；工具：裁皮刀、菱斩3mm、手缝针、打孔冲5mm',
 '1. 设计版型\n2. 裁切皮料\n3. 制作卡位\n4. 组合缝制\n5. 磨边封边',
 2, 3, 'intermediate', 256, 89, 1),
(1, '手工皮带的封边技巧', 'https://picsum.photos/400/300?random=3',
 '很多朋友问封边怎么才能做到镜面效果，这里分享一下我的经验。',
 '意大利植鞣革皮带条，床面处理剂，砂纸400-2000目',
 '{"mainMaterials":[{"name":"意大利植鞣革皮带条","spec":"38mm宽","quantity":"120cm"}],"auxMaterials":[{"name":"床面处理剂(CMC)","spec":"","quantity":"少量"},{"name":"边油","spec":"深棕色","quantity":"少量"},{"name":"抛光蜡","spec":"","quantity":"少量"}],"tools":[{"name":"裁皮刀","spec":"","quantity":"1把"},{"name":"砂纸","spec":"400/800/1200/2000目","quantity":"各1张"},{"name":"圆木棒","spec":"","quantity":"1根"},{"name":"帆布","spec":"","quantity":"1块"}]}',
 '主材：意大利植鞣革皮带条38mm宽；辅材：床面处理剂(CMC)、边油深棕色、抛光蜡；工具：裁皮刀、砂纸400/800/1200/2000目、圆木棒、帆布',
 '1. 裁皮带\n2. 削薄尾部\n3. 粗磨\n4. 床面处理\n5. 细磨\n6. 抛光',
 3, 5, 'intermediate', 512, 156, 1);

INSERT INTO t_work_step (work_id, step_name, step_type, time_cost, materials, tips, description, sort) VALUES
(1, '裁切下料', 'cutting', 30, '意大利植鞣革2.0mm，裁皮刀，钢尺', '注意刀刃角度保持45度，沿钢尺边缘垂直下刀，一次性裁断避免毛边', '根据纸样裁下料片，注意皮料的纹理方向，大面和卡位要合理布局节省皮料', 0),
(1, '打斩', 'other', 20, '菱斩4mm，橡胶垫，锤子', '斩齿要垂直于皮面，锤子力度均匀，相邻斩孔要对齐', '沿缝合线打斩，间距保持均匀，转弯处注意斩的角度', 1),
(1, '缝制', 'sewing', 60, '麻线0.6mm，手缝针两只，蜂蜡', '双针马鞍缝法，每针拉线力度一致，起针收针要回缝加固', '采用双针缝法，从一端开始缝制，注意线迹整齐美观', 2),
(1, '封边上色', 'edge', 90, '砂纸400-1200目，床面处理剂，边油封边液', '每一遍都要等干透再打磨，耐心是关键，至少重复5-8遍', '先用粗砂纸磨平边缘，床面处理剂填充，逐级打磨到镜面效果，最后上边油', 3),
(1, '五金安装', 'hardware', 15, '黄铜四合扣，安装冲子，锤子', '定位要准确，冲子垂直，力度适中，避免砸坏五金', '标记好四合扣位置，用冲子打孔，安装上下扣，确保扣合牢固', 4);

INSERT INTO t_work_step (work_id, step_name, step_type, time_cost, materials, tips, description, sort) VALUES
(2, '设计版型', 'other', 25, '卡纸，铅笔，橡皮，尺子', '多做几个纸样试折，确认尺寸和结构无误再下料', '根据需求设计卡包版型，8卡位+1零钱位，注意折叠厚度', 0),
(2, '裁切皮料', 'cutting', 20, '日本枥木皮1.5mm酒红色，裁皮刀，钢尺', '枥木皮纹理细腻，注意顺纹裁切，卡位要裁得方正', '按照纸样裁切外皮、内皮、各卡位皮片', 1),
(2, '制作卡位', 'sewing', 45, '麻线，手缝针，定位夹', '卡位边缘先削薄，缝合后整体更平整', '逐个制作卡位，先粘后缝，确保每个卡位尺寸标准', 2),
(2, '组合缝制', 'sewing', 50, '麻线，手缝针，黄胶', '先临时粘合定位，检查无误再正式缝制', '将各部件组合，先粘后打斩缝制，注意对整齐', 3),
(2, '磨边封边', 'edge', 60, '砂纸，床面处理剂，抛光棒', '封边过程中注意保持边缘湿润，打磨出镜面效果', '所有边缘依次打磨、封边，最终抛光处理', 4);

INSERT INTO t_work_step (work_id, step_name, step_type, time_cost, materials, tips, description, sort) VALUES
(3, '裁皮带', 'cutting', 10, '意大利植鞣革皮带条38mm宽，裁皮刀', '皮带条要选纹理顺直的部位，两边裁切平行', '根据腰围裁切皮带长度，预留扣头和尾端余量', 0),
(3, '削薄尾部', 'other', 15, '削边器，宽铲', '逐渐过渡削薄，避免削出明显台阶', '皮带尾端2cm处削薄至1.0mm左右，方便穿过皮带扣', 1),
(3, '粗磨', 'edge', 20, '砂纸400目，800目', '顺着边缘来回打磨，不要倾斜', '先用400目粗磨边缘，再用800目细磨，直到边缘平整光滑', 2),
(3, '床面处理', 'edge', 30, '床面处理剂（CMC），木棒', '薄涂多次，不要一次涂太厚', '涂抹床面处理剂，待半干时用圆木棒反复压实边缘', 3),
(3, '细磨', 'edge', 40, '砂纸1200目，2000目', '逐级递增砂纸目数，每遍干透再打磨', '依次用1200目、2000目砂纸细磨，每次打磨后重新上处理剂', 4),
(3, '抛光', 'edge', 30, '抛光蜡，帆布或羊皮', '快速来回摩擦产生热量，帮助蜡融化渗透', '涂少量抛光蜡，用帆布快速摩擦边缘，直到产生镜面光泽', 5);

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

INSERT IGNORE INTO t_retrospective (work_id, user_id, rework_points, loss_reasons, improvements, create_time, update_time) VALUES
(1, 1,
 '缝制起针处线迹略松需要返工回缝加固；封边第二遍边油涂得过厚，边缘有轻微流淌需重新打磨',
 '下料时余量预留不足，主材损耗约8%；打斩错位两个孔位，补缝遮盖耗费麻线0.3米',
 '起针收针统一回缝三针加固；封边边油少量多次薄涂，每遍干透再上；下料时每边多预留2mm余量',
 NOW(), NOW()),
(3, 1,
 '粗磨直接跳到细磨过渡太快，边缘出现细微划痕需返工重磨一遍',
 '砂纸消耗偏快，400目砂纸两张即磨损报废；抛光蜡用量偏多造成浪费',
 '粗磨与细磨之间增加800目过渡打磨；改用更耐用的碳化硅砂纸；抛光蜡薄涂即可无需堆蜡',
 NOW(), NOW());
