-- LOAN_PRODUCT: 대출상품(기본정보)
-- 정책:
-- - product_id는 시퀀스로 생성
-- - CODE_VALUE 기반 코드 컬럼은 code_value_id 문자열로 보관(v1)

CREATE TABLE LOAN_PRODUCT (
    product_id                   NUMBER           NOT NULL,
    provider_id                  NUMBER           NOT NULL,

    product_name                 VARCHAR2(200)    NOT NULL,

    product_type_code_value_id   VARCHAR2(50)     NOT NULL,
    loan_type_code_value_id      VARCHAR2(50)     NOT NULL,
    repayment_type_code_value_id VARCHAR2(50)     NOT NULL,

    collateral_type_code_value_id VARCHAR2(50),
    rate_type_code_value_id      VARCHAR2(50),

    note                         VARCHAR2(1000),

    add_date                     TIMESTAMP        DEFAULT SYSTIMESTAMP NOT NULL,
    end_date                     DATE,
    updated_at                   TIMESTAMP        DEFAULT SYSTIMESTAMP NOT NULL,

    CONSTRAINT PK_LOAN_PRODUCT PRIMARY KEY (product_id)
);

-- PK 시퀀스
CREATE SEQUENCE LOAN_PRODUCT_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 조회/정렬 성능
CREATE INDEX IDX_LOAN_PRODUCT_PROVIDER
    ON LOAN_PRODUCT (provider_id);

CREATE INDEX IDX_LOAN_PRODUCT_UPDATED
    ON LOAN_PRODUCT (updated_at DESC, add_date DESC);
