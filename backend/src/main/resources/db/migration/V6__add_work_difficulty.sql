ALTER TABLE t_work ADD COLUMN difficulty VARCHAR(20) DEFAULT NULL COMMENT '版型难度: beginner-入门, intermediate-进阶, advanced-复杂' AFTER craft_type_id;

CREATE INDEX idx_difficulty ON t_work(difficulty);

UPDATE t_work SET difficulty = CASE
  WHEN id = 1 THEN 'beginner'
  WHEN id = 2 THEN 'intermediate'
  WHEN id = 3 THEN 'advanced'
END;
