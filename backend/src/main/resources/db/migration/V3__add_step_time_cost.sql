ALTER TABLE t_work_step ADD COLUMN time_cost INT DEFAULT 0 COMMENT '步骤耗时(分钟)' AFTER step_type;

UPDATE t_work_step SET time_cost = 30 WHERE id = 1;
UPDATE t_work_step SET time_cost = 20 WHERE id = 2;
UPDATE t_work_step SET time_cost = 60 WHERE id = 3;
UPDATE t_work_step SET time_cost = 90 WHERE id = 4;
UPDATE t_work_step SET time_cost = 15 WHERE id = 5;

UPDATE t_work_step SET time_cost = 25 WHERE id = 6;
UPDATE t_work_step SET time_cost = 20 WHERE id = 7;
UPDATE t_work_step SET time_cost = 45 WHERE id = 8;
UPDATE t_work_step SET time_cost = 50 WHERE id = 9;
UPDATE t_work_step SET time_cost = 60 WHERE id = 10;

UPDATE t_work_step SET time_cost = 10 WHERE id = 11;
UPDATE t_work_step SET time_cost = 15 WHERE id = 12;
UPDATE t_work_step SET time_cost = 20 WHERE id = 13;
UPDATE t_work_step SET time_cost = 30 WHERE id = 14;
UPDATE t_work_step SET time_cost = 40 WHERE id = 15;
UPDATE t_work_step SET time_cost = 30 WHERE id = 16;
