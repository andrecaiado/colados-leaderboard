ALTER TABLE app_user
    ADD auth_provider VARCHAR(255);

ALTER TABLE app_user
    ALTER COLUMN auth_provider SET NOT NULL;