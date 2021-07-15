create schema if not exists hist
;
ALTER TABLE subs.doc_request_subsidy
    ADD COLUMN IF NOT EXISTS sys_period tstzrange NOT NULL DEFAULT tstzrange(current_timestamp, null)
;
CREATE TABLE hist.hist_doc_request_subsidy (LIKE subs.doc_request_subsidy)
;
DROP TRIGGER IF EXISTS trg_add_version ON subs.doc_request_subsidy
;
CREATE TRIGGER trg_add_version
    BEFORE INSERT OR UPDATE OR DELETE ON subs.doc_request_subsidy
    FOR EACH ROW EXECUTE PROCEDURE versioning(
        'sys_period', 'hist.hist_doc_request_subsidy', true
    );