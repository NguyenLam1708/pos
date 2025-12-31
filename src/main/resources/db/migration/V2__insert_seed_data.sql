-- V2__insert_seed_data.sql
INSERT INTO roles (id, name, code)
VALUES
    (gen_random_uuid(), 'Administrator', 'ADMIN'),
    (gen_random_uuid(), 'Regular User', 'USER')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO users (id, email, username, full_name, password, status)
VALUES
    (gen_random_uuid(), 'admin@pos.com', 'admin', 'System Admin',
         '$2a$12$u9f2K3Pq1t6H4Xj7Bq8nGuWf9x0D6lV2P1sK7qF0aY3zR8jT5vUeC',
     'ACTIVE')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (id, user_id, role_id)
SELECT gen_random_uuid(), u.id, r.id
FROM users u
         JOIN roles r ON r.code='ADMIN'
WHERE u.email='admin@pos.com'
    ON CONFLICT (user_id, role_id) DO NOTHING;

