-- Schema already created by Hibernate on startup
-- Grant table/sequence permissions to app user

\connect user_service_db

GRANT USAGE, CREATE ON SCHEMA public TO user_svc_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO user_svc_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT USAGE, SELECT ON SEQUENCES TO user_svc_user;

CREATE TABLE users (
    id            UUID PRIMARY KEY,
    username      VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    first_name    VARCHAR(30)  NOT NULL,
    last_name     VARCHAR(30)  NOT NULL,
    phone_number  VARCHAR(12),
    active        BOOLEAN      NOT NULL DEFAULT true,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL
);

CREATE INDEX username_id ON users (username);
CREATE INDEX email_id    ON users (email);

CREATE TABLE user_roles (
    user_id UUID        NOT NULL REFERENCES users(id),
    role    VARCHAR(255) CHECK (role IN ('ADMIN', 'JUDGE', 'USER'))
);

CREATE TABLE refresh_tokens (
    id         UUID         PRIMARY KEY,
    token      VARCHAR(2048) NOT NULL UNIQUE,
    user_id    UUID          NOT NULL REFERENCES users(id),
    expires_at TIMESTAMPTZ   NOT NULL,
    revoked    BOOLEAN       NOT NULL DEFAULT false,
    family     VARCHAR(36)
);

-- Transfer ownership to postgres so app user cannot drop
ALTER TABLE users          OWNER TO postgres;
ALTER TABLE user_roles     OWNER TO postgres;
ALTER TABLE refresh_tokens OWNER TO postgres;

-- Grant DML only to app user
GRANT SELECT, INSERT, UPDATE, DELETE ON users          TO user_svc_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON user_roles     TO user_svc_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON refresh_tokens TO user_svc_user;

CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGSERIAL       PRIMARY KEY,
    tbl_name    VARCHAR(100)    NOT NULL,
    operation   VARCHAR(10)     NOT NULL,
    changed_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    old_row     JSONB,
    new_row     JSONB
);

ALTER TABLE audit_log      OWNER TO postgres;

REVOKE ALL ON audit_log FROM user_svc_user;

CREATE OR REPLACE FUNCTION audit_trigger_fn()
RETURNS TRIGGER LANGUAGE plpgsql SECURITY DEFINER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
       INSERT INTO audit_log(tbl_name, operation, old_row)
       VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD)::jsonb);
    ELSIF TG_OP = 'INSERT' THEN
          INSERT INTO audit_log(tbl_name, operation, new_row)
          VALUES (TG_TABLE_NAME, TG_OP, row_to_json(NEW)::jsonb);
    ELSE
        INSERT INTO audit_log(tbl_name, operation, old_row, new_row)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD)::jsonb, row_to_json(NEW)::jsonb);
    END IF;
    RETURN NULL;
END;
$$;

CREATE TRIGGER audit_users
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_fn();

CREATE TRIGGER audit_user_roles
    AFTER INSERT OR UPDATE OR DELETE ON user_roles
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_fn();

CREATE TRIGGER audit_refresh_tokens
    AFTER INSERT OR UPDATE OR DELETE ON refresh_tokens
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_fn();
