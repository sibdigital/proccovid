create schema if not exists hist
;
ALTER TABLE subs.reg_verification_signature_file
    ADD COLUMN IF NOT EXISTS sys_period tstzrange NOT NULL DEFAULT tstzrange(current_timestamp, null)
;
CREATE TABLE hist.hist_reg_verification_signature_file (LIKE subs.reg_verification_signature_file)
;
DROP TRIGGER IF EXISTS trg_add_version ON subs.reg_verification_signature_file
;
CREATE TRIGGER trg_add_version
    BEFORE INSERT OR UPDATE OR DELETE ON subs.reg_verification_signature_file
    FOR EACH ROW EXECUTE PROCEDURE versioning(
        'sys_period', 'hist.hist_reg_verification_signature_file', true
    );

alter table public.cls_organization add column is_expiremental boolean default false
;