
\connect rating_service_db

GRANT USAGE ON SCHEMA public TO rating_svc_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO rating_svc_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT USAGE, SELECT ON SEQUENCES TO rating_svc_user;

CREATE TABLE ratings (
    id         UUID        PRIMARY KEY,
    judge_id   UUID        NOT NULL,
    dog_id     UUID        NOT NULL,
    rating     INTEGER     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_dog_judge UNIQUE (dog_id, judge_id)
);

CREATE TABLE rating_summaries (
    dog_id     UUID        PRIMARY KEY,
    average    FLOAT       NOT NULL,
    count      INTEGER     NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE ratings OWNER TO postgres;
ALTER TABLE rating_summaries OWNER TO postgres;

GRANT SELECT, INSERT, UPDATE, DELETE ON ratings TO rating_svc_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON rating_summaries TO rating_svc_user;

CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGSERIAL       PRIMARY KEY,
    tbl_name    VARCHAR(100)    NOT NULL,
    operation   VARCHAR(10)     NOT NULL,
    changed_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    old_row     JSONB,
    new_row     JSONB
);

ALTER TABLE audit_log      OWNER TO postgres;

REVOKE ALL ON audit_log FROM rating_svc_user;

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

CREATE TRIGGER audit_ratings
    AFTER INSERT OR UPDATE OR DELETE ON ratings
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_fn();

CREATE TRIGGER audit_rating_summaries
    AFTER INSERT OR UPDATE OR DELETE ON rating_summaries
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_fn();