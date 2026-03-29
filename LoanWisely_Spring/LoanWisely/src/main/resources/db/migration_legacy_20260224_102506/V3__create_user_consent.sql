-- USER_CONSENT: 사용자 LV별 동의 이력 (이력 불변 + 활성 플래그)
-- 정책:
-- - 같은 (user_id, consent_level) 조합에 대해 최신 유효 1건만 is_active='Y'
-- - 신규 입력 시 기존 is_active='Y'를 'N'으로 바꾸고 신규 row insert

CREATE TABLE USER_CONSENT (
    consent_id      NUMBER          NOT NULL,
    user_id         NUMBER          NOT NULL,
    consent_level   NUMBER          NOT NULL,  -- 1~3 (LV)
    consent_given   CHAR(1)         NOT NULL,  -- 'Y'/'N'
    is_active       CHAR(1)         DEFAULT 'Y' NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT PK_USER_CONSENT PRIMARY KEY (consent_id),
    CONSTRAINT CK_USER_CONSENT_LEVEL CHECK (consent_level IN (1,2,3)),
    CONSTRAINT CK_USER_CONSENT_GIVEN CHECK (consent_given IN ('Y','N')),
    CONSTRAINT CK_USER_CONSENT_ACTIVE CHECK (is_active IN ('Y','N'))
);

-- PK 시퀀스
CREATE SEQUENCE USER_CONSENT_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 조회 성능: 최신 유효 조회 / 이력 조회
CREATE INDEX IDX_USER_CONSENT_USER_LEVEL_ACTIVE
    ON USER_CONSENT (user_id, consent_level, is_active, created_at DESC);

CREATE INDEX IDX_USER_CONSENT_USER_LEVEL_CREATED
    ON USER_CONSENT (user_id, consent_level, created_at DESC);
