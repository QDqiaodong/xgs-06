-- ==========================================
-- 作品状态字段：区分练习样件、正式成品、修复件、半成品
-- 让皮具作品语境更准确，列表与详情页同步呈现
-- ==========================================

ALTER TABLE t_work 
ADD COLUMN work_status VARCHAR(20) DEFAULT 'finished' 
COMMENT '作品状态：practice-练习样件, finished-正式成品, repair-修复件, semi_finished-半成品'
AFTER difficulty;

ALTER TABLE t_work ADD INDEX idx_work_status (work_status);

-- ==========================================
-- 更新示例数据的作品状态，让不同作品呈现不同状态
-- ==========================================

UPDATE t_work SET work_status = 'finished' WHERE id = 1;
UPDATE t_work SET work_status = 'practice' WHERE id = 2;
UPDATE t_work SET work_status = 'semi_finished' WHERE id = 3;
