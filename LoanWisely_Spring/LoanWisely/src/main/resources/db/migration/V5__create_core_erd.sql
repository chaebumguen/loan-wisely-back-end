-- 설계서 기준 ERD 핵심 테이블 (정식 마이그레이션)
-- 출처: `5. ERD.pdf`
-- 주의: 컬럼/자료형/제약은 ERD 상세 기준으로 추가 보정 필요
-- 대상 DB: Oracle (NUMBER, VARCHAR2, TIMESTAMP, DATE)

-- =====================================================
-- USER CREDIT (LV1/2/3)
-- =====================================================
CREATE TABLE USER_CREDIT_LV1 (
    user_id        NUMBER    NOT NULL, -- PK, FK -> USER_PROFILE.user_id
    age            NUMBER,
    income_year    NUMBER,
    gender         VARCHAR2(20),
    created_at     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_USER_CREDIT_LV1 PRIMARY KEY (user_id)
);

CREATE TABLE USER_CREDIT_LV2 (
    user_id          NUMBER    NOT NULL, -- PK, FK -> USER_PROFILE.user_id
    employment_type  VARCHAR2(50),
    residence_type   VARCHAR2(50),
    created_at       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_USER_CREDIT_LV2 PRIMARY KEY (user_id)
);

CREATE TABLE USER_CREDIT_LV3 (
    user_id              NUMBER    NOT NULL, -- PK, FK -> USER_PROFILE.user_id
    loan_purpose         VARCHAR2(100),
    total_debt           NUMBER,
    existing_loan_count  NUMBER,
    created_at           TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_USER_CREDIT_LV3 PRIMARY KEY (user_id)
);

-- =====================================================
-- CODE DICTIONARY (메타데이터)
-- =====================================================
CREATE TABLE CODE_DICTIONARY_VERSION (
    version_id      NUMBER       NOT NULL,
    upload_id       NUMBER,       -- FK -> RAW_FILE_UPLOAD.upload_id
    version_label   VARCHAR2(200),
    created_at      TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_CODE_DICT_VERSION PRIMARY KEY (version_id)
);

CREATE TABLE CODE_DICTIONARY (
    dict_id            NUMBER       NOT NULL,
    version_id         NUMBER       NOT NULL, -- FK -> CODE_DICTIONARY_VERSION.version_id
    column_code        VARCHAR2(200),
    column_name        VARCHAR2(200),
    column_desc        VARCHAR2(1000),
    large_category_code_value_id  VARCHAR2(100),
    mideum_category_code_value_id VARCHAR2(100),
    small_category_code_value_id  VARCHAR2(100),
    is_required        CHAR(1),
    data_type          VARCHAR2(50),
    created_at         TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_CODE_DICTIONARY PRIMARY KEY (dict_id)
);

CREATE TABLE CODE_DICTIONARY_DIFF (
    diff_id          NUMBER       NOT NULL,
    pre_version_id   NUMBER       NOT NULL, -- FK -> CODE_DICTIONARY_VERSION.version_id
    post_version_id  NUMBER       NOT NULL, -- FK -> CODE_DICTIONARY_VERSION.version_id
    change_type      VARCHAR2(20),
    column_code      VARCHAR2(200),
    before_json_path VARCHAR2(1000),
    after_json_path  VARCHAR2(1000),
    created_at       TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_CODE_DICTIONARY_DIFF PRIMARY KEY (diff_id)
);

-- =====================================================
-- RECOMMENDATION CORE
-- =====================================================
CREATE TABLE RECO_POLICY (
    policy_id                NUMBER       NOT NULL,
    version                  VARCHAR2(100),
    policy_type_code_value_id VARCHAR2(100),
    policy_key               VARCHAR2(200),
    policy_value             VARCHAR2(2000),
    effective_from           TIMESTAMP,
    effective_to             TIMESTAMP,
    created_at               TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RECO_POLICY PRIMARY KEY (policy_id)
);

CREATE TABLE RECO_REQUEST (
    request_id     NUMBER       NOT NULL,
    user_id        NUMBER       NOT NULL, -- FK -> USER_PROFILE.user_id
    version_id     NUMBER,       -- FK -> CODE_DICTIONARY_VERSION.version_id
    requested_at   TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RECO_REQUEST PRIMARY KEY (request_id)
);

CREATE TABLE RECO_RESULT (
    result_id               NUMBER       NOT NULL,
    request_id              NUMBER       NOT NULL, -- FK -> RECO_REQUEST.request_id
    overall_score           NUMBER,
    policy_version          VARCHAR2(100),
    confidence_level_code_value_id VARCHAR2(100),
    explanation_json_path   VARCHAR2(1000),
    created_at              TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RECO_RESULT PRIMARY KEY (result_id)
);

CREATE TABLE RECO_ITEM (
    item_id          NUMBER       NOT NULL,
    result_id        NUMBER       NOT NULL, -- FK -> RECO_RESULT.result_id
    product_id       NUMBER       NOT NULL, -- FK -> LOAN_PRODUCT.product_id
    matching_score   NUMBER,
    estimated_rate   NUMBER,
    estimated_limit  NUMBER,
    stability_score  NUMBER,
    reason_json_path VARCHAR2(1000),
    rank             NUMBER,
    created_at       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RECO_ITEM PRIMARY KEY (item_id)
);

CREATE TABLE RECO_ESTIMATION_DETAIL (
    detail_id      NUMBER       NOT NULL,
    snapshot_id    NUMBER,       -- FK -> USER_CREDIT_SNAPSHOT.snapshot_id (선택)
    item_id        NUMBER       NOT NULL, -- FK -> RECO_ITEM.item_id
    factor_code    VARCHAR2(100),
    factor_name    VARCHAR2(200),
    factor_value   VARCHAR2(1000),
    contribution   NUMBER,
    created_at     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RECO_ESTIMATION_DETAIL PRIMARY KEY (detail_id)
);

CREATE TABLE RECO_EXCLUSION_REASON (
    reason_id    NUMBER       NOT NULL,
    result_id    NUMBER       NOT NULL, -- FK -> RECO_RESULT.result_id
    product_id   NUMBER       NOT NULL, -- FK -> LOAN_PRODUCT.product_id
    reason_code  VARCHAR2(100),
    reason_text  VARCHAR2(1000),
    created_at   TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RECO_EXCLUSION_REASON PRIMARY KEY (reason_id)
);

CREATE TABLE RECO_REJECT_LOG (
    reject_id     NUMBER       NOT NULL,
    request_id    NUMBER       NOT NULL, -- FK -> RECO_REQUEST.request_id
    product_id    NUMBER       NOT NULL, -- FK -> LOAN_PRODUCT.product_id
    reject_code   VARCHAR2(100),
    reject_reason VARCHAR2(1000),
    created_at    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RECO_REJECT_LOG PRIMARY KEY (reject_id)
);

CREATE TABLE RECO_EVENT_LOG (
    log_id      NUMBER       NOT NULL,
    masked_user_id VARCHAR2(200),
    product_id  NUMBER,       -- FK -> LOAN_PRODUCT.product_id
    event_type_code_value_id VARCHAR2(100),
    occurred_at TIMESTAMP,
    CONSTRAINT PK_RECO_EVENT_LOG PRIMARY KEY (log_id)
);

-- =====================================================
-- FK 제약 (ERD 확정 후 구체화)
-- =====================================================
-- 주의: 아래 FK는 참조 테이블/컬럼 확정 시 활성화 권장
-- 예시)
-- ALTER TABLE USER_CREDIT_LV1
--   ADD CONSTRAINT FK_UCLV1_USER
--   FOREIGN KEY (user_id) REFERENCES USER_PROFILE(user_id);
--
-- ALTER TABLE RECO_ITEM
--   ADD CONSTRAINT FK_RECO_ITEM_PRODUCT
--   FOREIGN KEY (product_id) REFERENCES LOAN_PRODUCT(product_id);
--
-- ALTER TABLE RECO_REQUEST
--   ADD CONSTRAINT FK_RECO_REQ_USER
--   FOREIGN KEY (user_id) REFERENCES USER_PROFILE(user_id);
--
-- ALTER TABLE RECO_RESULT
--   ADD CONSTRAINT FK_RECO_RESULT_REQ
--   FOREIGN KEY (request_id) REFERENCES RECO_REQUEST(request_id);

-- =====================================================
-- 인덱스 (조회 패턴 기준, 필요 시 조정)
-- =====================================================
CREATE INDEX IDX_UCLV1_CREATED ON USER_CREDIT_LV1 (created_at DESC);
CREATE INDEX IDX_UCLV2_CREATED ON USER_CREDIT_LV2 (created_at DESC);
CREATE INDEX IDX_UCLV3_CREATED ON USER_CREDIT_LV3 (created_at DESC);

CREATE INDEX IDX_RECO_REQUEST_USER ON RECO_REQUEST (user_id, requested_at DESC);
CREATE INDEX IDX_RECO_RESULT_REQ ON RECO_RESULT (request_id, created_at DESC);
CREATE INDEX IDX_RECO_ITEM_RESULT ON RECO_ITEM (result_id, rank);
CREATE INDEX IDX_RECO_EXCLUSION_RESULT ON RECO_EXCLUSION_REASON (result_id, created_at DESC);
CREATE INDEX IDX_RECO_REJECT_REQ ON RECO_REJECT_LOG (request_id, created_at DESC);

CREATE INDEX IDX_CODE_DICT_VERSION ON CODE_DICTIONARY (version_id);
CREATE INDEX IDX_CODE_DICT_DIFF_VER ON CODE_DICTIONARY_DIFF (pre_version_id, post_version_id);

-- =====================================================
-- 시퀀스 (Oracle)
-- =====================================================
CREATE SEQUENCE CODE_DICTIONARY_VERSION_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE CODE_DICTIONARY_SEQ        START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE CODE_DICTIONARY_DIFF_SEQ   START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE SEQUENCE RECO_POLICY_SEQ            START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE RECO_REQUEST_SEQ           START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE RECO_RESULT_SEQ            START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE RECO_ITEM_SEQ              START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE RECO_ESTIMATION_DETAIL_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE RECO_EXCLUSION_REASON_SEQ  START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE RECO_REJECT_LOG_SEQ        START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE RECO_EVENT_LOG_SEQ         START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- =====================================================
-- TODO (한국어 기준)
-- - USER_PROFILE/LOAN_PRODUCT/USER_CREDIT_SNAPSHOT 확정 후 FK 제약 추가
-- - ERD 기준 컬럼명/자료형 정합성 재검증
-- - 인덱스는 실제 조회 패턴에 따라 추가/삭제
-- - PK 생성 방식(시퀀스/트리거/애플리케이션) 최종 결정
-- =====================================================
