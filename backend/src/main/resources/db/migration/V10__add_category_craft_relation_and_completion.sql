-- ==========================================
-- V10: 品类-工艺合理关联 + 作品完成度标尺
-- 1. 建立 t_category_craft_relation 关联表，定义品类常用工艺
-- 2. 为 t_work 添加完成度字段 (completion_level, completion_note)
-- ==========================================

-- 品类-工艺关联表：定义每个品类常见/适用的工艺组合，减少无意义搭配
CREATE TABLE IF NOT EXISTS t_category_craft_relation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id BIGINT NOT NULL COMMENT '品类ID（关联t_category.id where type=category）',
  craft_type_id BIGINT NOT NULL COMMENT '工艺类型ID（关联t_category.id where type=craft）',
  relation_type VARCHAR(20) DEFAULT 'common' COMMENT '关联类型：common-常见工艺, optional-可选工艺, excluded-不推荐工艺',
  sort INT DEFAULT 0 COMMENT '排序权重，数字越小越靠前',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_category_craft (category_id, craft_type_id),
  INDEX idx_category_id (category_id),
  INDEX idx_craft_type_id (craft_type_id),
  INDEX idx_relation_type (relation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='品类与工艺的合理关联表，用于减少无意义组合';

-- 作品完成度字段：分段记录从粗坯到完工的工艺完成状态
ALTER TABLE t_work
ADD COLUMN completion_level INT DEFAULT 5
COMMENT '工艺完成度等级：1-粗坯(20%), 2-雏形(40%), 3-半成(60%), 4-精修(80%), 5-完工(100%)'
AFTER work_status;

ALTER TABLE t_work
ADD COLUMN completion_note VARCHAR(500)
COMMENT '完成度说明文字，描述当前阶段的工艺状态'
AFTER completion_level;

ALTER TABLE t_work ADD INDEX idx_completion_level (completion_level);

-- ==========================================
-- 预设品类-工艺关联数据（基于皮具制作常识）
-- 说明：
-- 1) 护照夹、钱包、卡包、手拿包、单肩包、托特包：常见缝制+封边
-- 2) 皮带：常见封边+裁切
-- 3) 皮雕工艺：主要适用于有足够雕刻平面的品类（钱包/手拿包/单肩包/托特包/笔记本封皮），
--    不推荐用于小物件（钥匙包/卡包/皮带/护照夹）因为面积太小
-- 4) 编织工艺：适合包袋类、皮带、钥匙包等，不适合笔记本封皮/护照夹
-- ==========================================

-- 先获取品类和工艺的ID（基于schema.sql中的预设数据）
-- 品类ID: 1-钱包,2-卡包,3-皮带,4-手拿包,5-单肩包,6-托特包,7-钥匙包,8-护照夹,9-笔记本封皮,10-其他
-- 工艺ID: 1-新手教程,2-基础缝制,3-皮料裁切,4-边缘处理,5-封边上色,6-五金安装,7-起鼓塑形,8-皮雕工艺,9-编织工艺,10-进阶工艺

-- === 钱包(ID=1) 常见工艺：缝制、裁切、封边、五金、塑形；可选：皮雕/编织 ===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(1, 2, 'common', 1),
(1, 3, 'common', 2),
(1, 4, 'common', 3),
(1, 5, 'common', 4),
(1, 6, 'common', 5),
(1, 7, 'optional', 6),
(1, 8, 'optional', 7),
(1, 9, 'optional', 8),
(1, 10, 'optional', 9);

-- === 卡包(ID=2) 常见工艺：缝制、裁切、封边、五金；不推荐：皮雕（面积小）===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(2, 2, 'common', 1),
(2, 3, 'common', 2),
(2, 4, 'common', 3),
(2, 5, 'common', 4),
(2, 6, 'common', 5),
(2, 8, 'excluded', 99),
(2, 10, 'optional', 6);

-- === 皮带(ID=3) 常见工艺：封边、裁切、边缘处理；不推荐：皮雕/编织 ===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(3, 3, 'common', 1),
(3, 4, 'common', 2),
(3, 5, 'common', 3),
(3, 6, 'optional', 4),
(3, 8, 'excluded', 99),
(3, 9, 'excluded', 98);

-- === 手拿包(ID=4) 常见工艺：缝制、裁切、封边、五金、塑形；可选：皮雕/编织 ===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(4, 2, 'common', 1),
(4, 3, 'common', 2),
(4, 4, 'common', 3),
(4, 5, 'common', 4),
(4, 6, 'common', 5),
(4, 7, 'common', 6),
(4, 8, 'optional', 7),
(4, 9, 'optional', 8),
(4, 10, 'optional', 9);

-- === 单肩包(ID=5) 常见工艺：缝制、裁切、封边、五金、塑形；可选：皮雕/编织 ===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(5, 2, 'common', 1),
(5, 3, 'common', 2),
(5, 4, 'common', 3),
(5, 5, 'common', 4),
(5, 6, 'common', 5),
(5, 7, 'common', 6),
(5, 8, 'optional', 7),
(5, 9, 'optional', 8),
(5, 10, 'optional', 9);

-- === 托特包(ID=6) 常见工艺：缝制、裁切、封边、五金；可选：塑形/皮雕/编织 ===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(6, 2, 'common', 1),
(6, 3, 'common', 2),
(6, 4, 'common', 3),
(6, 5, 'common', 4),
(6, 6, 'common', 5),
(6, 7, 'optional', 6),
(6, 8, 'optional', 7),
(6, 9, 'optional', 8),
(6, 10, 'optional', 9);

-- === 钥匙包(ID=7) 常见工艺：缝制、裁切、封边、五金；不推荐：皮雕（面积小）===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(7, 2, 'common', 1),
(7, 3, 'common', 2),
(7, 4, 'common', 3),
(7, 5, 'common', 4),
(7, 6, 'common', 5),
(7, 9, 'optional', 6),
(7, 8, 'excluded', 99);

-- === 护照夹(ID=8) 常见工艺：缝制、封边、裁切、五金；不推荐：皮雕（面积小+结构紧凑）===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(8, 2, 'common', 1),
(8, 5, 'common', 2),
(8, 3, 'common', 3),
(8, 4, 'common', 4),
(8, 6, 'optional', 5),
(8, 8, 'excluded', 99),
(8, 9, 'excluded', 98);

-- === 笔记本封皮(ID=9) 常见工艺：封边、裁切、塑形、皮雕；可选：五金；不推荐：编织 ===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(9, 3, 'common', 1),
(9, 4, 'common', 2),
(9, 5, 'common', 3),
(9, 7, 'common', 4),
(9, 8, 'common', 5),
(9, 2, 'optional', 6),
(9, 6, 'optional', 7),
(9, 9, 'excluded', 99);

-- === 其他(ID=10) 所有工艺均可选，不限定 ===
INSERT INTO t_category_craft_relation (category_id, craft_type_id, relation_type, sort) VALUES
(10, 1, 'optional', 1),
(10, 2, 'optional', 2),
(10, 3, 'optional', 3),
(10, 4, 'optional', 4),
(10, 5, 'optional', 5),
(10, 6, 'optional', 6),
(10, 7, 'optional', 7),
(10, 8, 'optional', 8),
(10, 9, 'optional', 9),
(10, 10, 'optional', 10);

-- ==========================================
-- 更新示例作品的完成度数据，让展示更有层次
-- ==========================================
UPDATE t_work SET completion_level = 5, completion_note = '所有工序已完成，封边达到镜面效果，缝线工整' WHERE id = 1;
UPDATE t_work SET completion_level = 3, completion_note = '主体结构完成，卡位已组装，待精修封边与细节打磨' WHERE id = 2;
UPDATE t_work SET completion_level = 4, completion_note = '封边工序进行中，已完成粗磨和床面处理，还差最终抛光' WHERE id = 3;
