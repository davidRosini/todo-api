-- Only way to update timestamp on postgres is using a trigger
CREATE OR REPLACE FUNCTION updated_at_timestamp() RETURNS TRIGGER LANGUAGE plpgsql
AS
$$
BEGIN
    IF (NEW != OLD) THEN
        NEW.updated_at = CURRENT_TIMESTAMP;
        RETURN NEW;
    END IF;
    RETURN OLD;
END;
$$;

DROP TRIGGER IF EXISTS t_updated_at on todo;
CREATE OR REPLACE TRIGGER t_updated_at
    BEFORE UPDATE
    ON todo
    FOR EACH ROW
EXECUTE PROCEDURE updated_at_timestamp();

-- GRANTS user access to tables on postgres
GRANT USAGE ON SCHEMA public TO api_todo;
GRANT SELECT, USAGE ON ALL SEQUENCES IN SCHEMA public TO api_todo;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO api_todo;