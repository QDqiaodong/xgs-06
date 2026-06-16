-- ==========================================
-- 幂等迁移：为 t_work 表添加 material_brief 字段
-- 简短材料摘要文本，供列表和个人页直接展示
-- ==========================================

SET @dbname = DATABASE();
SET @tablename = 't_work';
SET @columnname = 'material_brief';
SET @preparedStatement = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
      WHERE table_schema = @dbname
        AND table_name = @tablename
        AND column_name = @columnname
    ),
    'SELECT 1',
    CONCAT(
      'ALTER TABLE `', @tablename, '` ',
      'ADD COLUMN `', @columnname, '` VARCHAR(200) ',
      'COMMENT ''简短材料摘要文本，供列表展示'' ',
      'AFTER `material_summary`'
    )
  )
);

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;
