ALTER TABLE app_user_roles
DROP CONSTRAINT IF EXISTS fk_appuser_roles_on_app_user,
DROP CONSTRAINT IF EXISTS fk_app_user_roles_app_user_id,
ADD CONSTRAINT fk_app_user_roles_app_user_id
FOREIGN KEY (app_user_id)
REFERENCES app_user(id)
ON DELETE CASCADE;
