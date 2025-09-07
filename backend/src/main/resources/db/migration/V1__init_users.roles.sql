-- Enable case-insensitive text so EMAIL uniqueness is case-insensitive
CREATE EXTENSION IF NOT EXISTS citext;

-- ROLES
CREATE TABLE roles (
                       id          BIGSERIAL PRIMARY KEY,
                       name        VARCHAR(50) NOT NULL UNIQUE
);

-- USERS
CREATE TABLE users (
                       id                  BIGSERIAL PRIMARY KEY,
                       email               CITEXT NOT NULL UNIQUE,
                       password_hash       VARCHAR(100) NOT NULL,     -- bcrypt fits in 60 chars; leave headroom
                       full_name           VARCHAR(120),
                       is_email_verified   BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Trigger to auto-update updated_at
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_set_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- USER_ROLES (many-to-many)
CREATE TABLE user_roles (
                            user_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id   BIGINT NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
                            PRIMARY KEY (user_id, role_id)
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_user_roles_user ON user_roles (user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles (role_id);

-- Seed roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_OWNER') ON CONFLICT DO NOTHING;  -- e.g., shop owner
INSERT INTO roles (name) VALUES ('ROLE_USER')  ON CONFLICT DO NOTHING;
