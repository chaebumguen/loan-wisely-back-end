ALTER TABLE USER_AUTH ADD (
    fail_login_count NUMBER DEFAULT 0 NOT NULL,
    is_locked        CHAR(1) DEFAULT 'N' NOT NULL,
    password_updated_at TIMESTAMP
);

ALTER TABLE USER_AUTH ADD CONSTRAINT CK_USER_AUTH_LOCKED
    CHECK (is_locked IN ('Y','N'));

UPDATE USER_AUTH
   SET fail_login_count = 0
 WHERE fail_login_count IS NULL;

UPDATE USER_AUTH
   SET is_locked = 'N'
 WHERE is_locked IS NULL;
