-- ==========================================
-- 复盘记录表：为已完成作品沉淀制作经验
-- 记录返工点、损耗原因、下次改进方向，供个人页集中查看
-- 使用 CREATE TABLE IF NOT EXISTS 保证幂等，可在每次启动安全执行
-- ==========================================

CREATE TABLE IF NOT EXISTS t_retrospective (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL COMMENT '关联作品ID',
  user_id BIGINT NOT NULL COMMENT '作者用户ID（冗余便于按用户聚合查询）',
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

-- ==========================================
-- 预置示例复盘数据（INSERT IGNORE 借助 uk_work_id 幂等，重复启动不会重复插入；
-- 用户编辑或删除后不会被覆盖，尊重用户操作）
-- ==========================================
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
