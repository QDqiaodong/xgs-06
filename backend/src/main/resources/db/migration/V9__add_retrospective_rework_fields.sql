-- ==========================================
-- 扩展复盘记录：新增返工原因、发生阶段、处理结果字段
-- 帮助更结构化地沉淀失败经验，便于后续检索和分析
-- 使用 ALTER TABLE IGNORE 保证幂等，字段已存在则跳过
-- ==========================================

ALTER TABLE t_retrospective
ADD COLUMN IF NOT EXISTS rework_reason TEXT COMMENT '返工原因：具体描述为何需要返工' AFTER rework_points,
ADD COLUMN IF NOT EXISTS occurrence_stage VARCHAR(50) COMMENT '发生阶段：如裁切/封边/缝制等工艺阶段' AFTER rework_reason,
ADD COLUMN IF NOT EXISTS handle_result TEXT COMMENT '处理结果：最终如何解决该问题' AFTER occurrence_stage;

-- ==========================================
-- 为已有示例数据补充新字段内容
-- ==========================================

UPDATE t_retrospective
SET
  rework_reason = CASE
    WHEN work_id = 1 THEN '起针收针未按规范回缝加固，边油涂厚导致流淌'
    WHEN work_id = 3 THEN '打磨工序跳级，未按粗-中-细顺序进行'
    ELSE rework_reason
  END,
  occurrence_stage = CASE
    WHEN work_id = 1 THEN '缝制,封边'
    WHEN work_id = 3 THEN '打磨'
    ELSE occurrence_stage
  END,
  handle_result = CASE
    WHEN work_id = 1 THEN '拆除松动线迹重新回缝加固；打磨掉流淌边油后薄涂三遍'
    WHEN work_id = 3 THEN '返工重新按400-800-1200目顺序打磨三遍'
    ELSE handle_result
  END
WHERE id IN (SELECT id FROM (SELECT id FROM t_retrospective WHERE work_id IN (1, 3)) AS tmp);
