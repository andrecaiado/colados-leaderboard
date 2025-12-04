ALTER TABLE app_user
    ADD CONSTRAINT uc_appuser_email UNIQUE (email);

ALTER TABLE app_user
    ADD CONSTRAINT uc_appuser_username UNIQUE (username);