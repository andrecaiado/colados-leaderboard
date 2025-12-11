-- Insert the referenced users first
INSERT INTO app_user (id, username, email, auth_provider, is_root) VALUES
    (101, 'testuser1', 'testuser1@example.com', 'EXTERNAL', FALSE),
    (102, 'testuser2', 'testuser2@example.com', 'EXTERNAL', FALSE);

INSERT INTO Player (user_id, character_name, created_at) VALUES
    (101, 'Mario', '2025-10-01T10:00:00Z'),
    (101, 'Yoshi', '2025-08-05T12:00:00Z'),
    (101, 'Mario', '2025-06-05T11:00:00Z'),
    (101, 'Toadette', '2025-04-10T09:00:00Z'),
    (102, 'Toadette', '2025-04-10T09:01:00Z');