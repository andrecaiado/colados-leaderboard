-- Drop existing constraints if they exist
ALTER TABLE app_user_roles
    DROP CONSTRAINT IF EXISTS fk_appuser_roles_on_app_user;

ALTER TABLE app_user_roles
    DROP CONSTRAINT IF EXISTS fk_app_user_roles_app_user_id;

-- Add the correct constraint with ON DELETE CASCADE
ALTER TABLE app_user_roles
    ADD CONSTRAINT fk_app_user_roles_app_user_id
        FOREIGN KEY (app_user_id)
            REFERENCES app_user(id)
            ON DELETE CASCADE;
