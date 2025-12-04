ALTER TABLE app_user_roles
ADD CONSTRAINT fk_app_user_roles_app_user_id
FOREIGN KEY (app_user_id)
REFERENCES app_user(id)
ON DELETE CASCADE;
