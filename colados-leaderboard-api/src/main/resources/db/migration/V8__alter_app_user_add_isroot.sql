ALTER TABLE app_user
    ADD is_root BOOLEAN;

ALTER TABLE app_user
    ALTER COLUMN is_root SET NOT NULL;