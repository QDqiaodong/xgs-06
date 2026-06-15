-- ==========================================
-- 幂等迁移：为 t_work 表添加 material_summary 字段
-- 仅当该列不存在时才执行 ALTER TABLE
-- ==========================================

SET @dbname = DATABASE();
SET @tablename = 't_work';
SET @columnname = 'material_summary';
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
      'ADD COLUMN `', @columnname, '` TEXT ',
      'COMMENT ''材料用量速览JSON: {mainMaterials, auxMaterials, tools}'' ',
      'AFTER `materials`'
    )
  )
);

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;
