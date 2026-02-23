-- USER_PROFILE: 사용자 입력/이력 (이력 불변 + 활성 플래그)
-- 정책:
-- - 같은 user_id에 대해 최신 유효 1건만 is_active='Y'
-- - 신규 입력 시 기존 is_active='Y'를 'N'으로 바꾸고 신규 row insert

CREATE TABLE USER_PROFILE (
    profile_id           NUMBER          NOT NULL,
    user_id              NUMBER          NOT NULL,
    input_level          NUMBER          NOT NULL,

    age                  NUMBER,
    income_year          NUMBER,
    gender               VARCHAR2(20),

    employment_type      VARCHAR2(50),
    residence_type       VARCHAR2(50),

    debt_total           NUMBER,
    existing_loan_count  NUMBER,
    loan_purpose         VARCHAR2(100),

    input_state_code     VARCHAR2(50),
    is_active            CHAR(1)         DEFAULT 'Y' NOT NULL,
    created_at           TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT PK_USER_PROFILE PRIMARY KEY (profile_id),
    CONSTRAINT CK_USER_PROFILE_ACTIVE CHECK (is_active IN ('Y','N')),
    CONSTRAINT CK_USER_PROFILE_LEVEL CHECK (input_level IN (1,2,3))
);

-- PK 시퀀스
CREATE SEQUENCE USER_PROFILE_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 조회 성능: 최신 유효 조회/이력 조회 지원
CREATE INDEX IDX_USER_PROFILE_USER_ACTIVE
    ON USER_PROFILE (user_id, is_active, created_at DESC);

CREATE INDEX IDX_USER_PROFILE_USER_CREATED
    ON USER_PROFILE (user_id, created_at DESC);
