CREATE TABLE app_user_roles
(
    app_user_id INTEGER NOT NULL,
    roles       VARCHAR(255)
);

ALTER TABLE app_user
    ADD password VARCHAR(255);

ALTER TABLE app_user_roles
    ADD CONSTRAINT fk_appuser_roles_on_app_user FOREIGN KEY (app_user_id) REFERENCES app_user (id);