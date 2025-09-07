CREATE TABLE password_reset_tokens (
                                       id              BIGSERIAL PRIMARY KEY,
                                       user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                       token_hash      VARCHAR(128) NOT NULL UNIQUE,        -- SHA-256 hex of the opaque token
                                       issued_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                       expires_at      TIMESTAMPTZ NOT NULL,
                                       used            BOOLEAN NOT NULL DEFAULT FALSE,
                                       used_at         TIMESTAMPTZ,
                                       user_agent      VARCHAR(255),
                                       ip_address      INET
);

CREATE INDEX IF NOT EXISTS idx_prt_user ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_prt_expires ON password_reset_tokens(expires_at);
