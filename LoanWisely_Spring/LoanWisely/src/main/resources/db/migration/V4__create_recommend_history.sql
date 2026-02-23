-- RECOMMEND_HISTORY: 추천 이력(재현/감사/증거 경로 포함)
-- 정책:
-- - DB에는 JSON 자체 저장하지 않음 (경로만 저장)
-- - reproduce_key는 재현 키이므로 UNIQUE
-- - append-only 성격(삭제/갱신 최소화). v1에서는 update 사용하지 않음

CREATE TABLE RECOMMEND_HISTORY (
    recommend_id         NUMBER           NOT NULL,
    user_id              NUMBER           NOT NULL,

    reproduce_key        VARCHAR2(200)    NOT NULL,

    recommend_state      VARCHAR2(30)     NOT NULL,  -- READY / NOT_READY / BLOCKED

    policy_version       VARCHAR2(100),
    meta_version         VARCHAR2(100),

    evidence_file_path   VARCHAR2(1000),
    explain_file_path    VARCHAR2(1000),

    created_at           TIMESTAMP        DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT PK_RECOMMEND_HISTORY PRIMARY KEY (recommend_id),
    CONSTRAINT UQ_RECOMMEND_HISTORY_REPRODUCE UNIQUE (reproduce_key)
);

-- PK 시퀀스
CREATE SEQUENCE RECOMMEND_HISTORY_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 조회 성능
CREATE INDEX IDX_RECOMMEND_HISTORY_USER
    ON RECOMMEND_HISTORY (user_id, created_at DESC);

CREATE INDEX IDX_RECOMMEND_HISTORY_STATE
    ON RECOMMEND_HISTORY (recommend_state, created_at DESC);
