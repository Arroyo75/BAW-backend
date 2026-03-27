-- Schema already created by Hibernate on startup
-- Grant table/sequence permissions to app user

\connect dog_service_db

GRANT USAGE, CREATE ON SCHEMA public TO dog_svc_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO dog_svc_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT USAGE, SELECT ON SEQUENCES TO dog_svc_user;

CREATE TABLE dogs (
    id          UUID          PRIMARY KEY,
    owner_id    UUID          NOT NULL,
    nickname    VARCHAR(255)  NOT NULL,
    breed       VARCHAR(255)  NOT NULL,
    age         INTEGER       NOT NULL,
    image       VARCHAR(255),
    description VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL,
    updated_at  TIMESTAMPTZ   NOT NULL
);

CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGSERIAL       PRIMARY KEY,
    tbl_name    VARCHAR(100)    NOT NULL,
    operation   VARCHAR(10)     NOT NULL,
    changed_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    old_row     JSONB,
    new_row     JSONB
);

REVOKE ALL ON audit_log FROM dog_svc_user;

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

CREATE TRIGGER audit_dogs
    AFTER INSERT OR UPDATE OR DELETE ON dogs
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_fn();

ALTER TABLE dogs OWNER TO postgres;

GRANT SELECT, INSERT, UPDATE, DELETE ON dogs TO dog_svc_user;