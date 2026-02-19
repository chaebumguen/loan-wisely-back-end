-- USER_AUTH: 로그인 실패 횟수/잠금 상태/비밀번호 변경 시각 컬럼 보강
ALTER TABLE USER_AUTH ADD (
    fail_login_count    NUMBER DEFAULT 0 NOT NULL,
    is_locked           CHAR(1) DEFAULT 'N' NOT NULL,
    password_updated_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

ALTER TABLE USER_AUTH ADD CONSTRAINT CK_USER_AUTH_LOCKED
    CHECK (is_locked IN ('Y','N'));

CREATE INDEX IDX_USER_AUTH_STATUS ON USER_AUTH (status, is_locked);
