SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE line;
TRUNCATE TABLE station;
TRUNCATE TABLE line_section;

SET REFERENTIAL_INTEGRITY TRUE;

ALTER TABLE station ALTER COLUMN id RESTART WITH 1;
ALTER TABLE line ALTER COLUMN id RESTART WITH 1;
ALTER TABLE line_section ALTER COLUMN id RESTART WITH 1;