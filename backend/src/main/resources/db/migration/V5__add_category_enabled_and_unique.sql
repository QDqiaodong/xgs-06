-- ==========================================
-- 幂等迁移：为 t_category 表添加 enabled 字段
-- 仅当该列不存在时才执行 ALTER TABLE
-- ==========================================

SET @dbname = DATABASE();
SET @tablename = 't_category';
SET @columnname = 'enabled';
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
      'ADD COLUMN `', @columnname, '` TINYINT DEFAULT 1 ',
      'COMMENT ''1-启用, 0-禁用'' ',
      'AFTER `sort`'
    )
  )
);

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- ==========================================
-- 幂等迁移：为 t_category 表添加唯一索引 (type, name)
-- 仅当该索引不存在时才执行 CREATE INDEX
-- ==========================================

SET @dbname = DATABASE();
SET @tablename = 't_category';
SET @indexname = 'uk_type_name';
SET @preparedStatement = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
      WHERE table_schema = @dbname
        AND table_name = @tablename
        AND index_name = @indexname
    ),
    'SELECT 1',
    CONCAT(
      'CREATE UNIQUE INDEX `', @indexname, '` ',
      'ON `', @tablename, '` (`type`, `name`)'
    )
  )
);

PREPARE createIndexIfNotExists FROM @preparedStatement;
EXECUTE createIndexIfNotExists;
DEALLOCATE PREPARE createIndexIfNotExists;

-- ==========================================
-- 为已有数据设置 enabled 默认值为 1
-- ==========================================

UPDATE t_category SET enabled = 1 WHERE enabled IS NULL;
