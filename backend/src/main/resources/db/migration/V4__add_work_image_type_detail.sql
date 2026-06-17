ALTER TABLE t_work_image MODIFY COLUMN type TINYINT DEFAULT 1 COMMENT '1-成品图, 2-过程图, 3-细节图';

INSERT INTO t_work_image (work_id, step_id, image_url, type, sort) VALUES
(1, NULL, 'https://picsum.photos/600/450?random=111', 3, 0),
(1, NULL, 'https://picsum.photos/600/450?random=112', 3, 1),
(2, NULL, 'https://picsum.photos/600/450?random=211', 3, 0),
(3, NULL, 'https://picsum.photos/600/450?random=311', 3, 0),
(3, NULL, 'https://picsum.photos/600/450?random=312', 3, 1);
