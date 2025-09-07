CREATE TABLE refresh_tokens (
                                id              BIGSERIAL PRIMARY KEY,
                                user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                token_hash      VARCHAR(128) NOT NULL UNIQUE,      -- SHA-256 hex of the opaque token
                                issued_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                expires_at      TIMESTAMPTZ NOT NULL,
                                revoked         BOOLEAN NOT NULL DEFAULT FALSE,
                                replaced_by_id  BIGINT NULL REFERENCES refresh_tokens(id) ON DELETE SET NULL,
                                user_agent      VARCHAR(255),                      -- optional diagnostics
                                ip_address      INET                               -- optional diagnostics
);

CREATE INDEX IF NOT EXISTS idx_refresh_user ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_expires ON refresh_tokens(expires_at);
