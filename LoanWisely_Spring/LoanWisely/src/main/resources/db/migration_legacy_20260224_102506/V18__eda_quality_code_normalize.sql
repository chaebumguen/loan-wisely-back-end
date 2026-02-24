-- Normalized stage table
CREATE TABLE RAW_FILE_NORMALIZED (
    norm_id      NUMBER       NOT NULL,
    upload_id    NUMBER       NOT NULL,
    row_num      NUMBER       NOT NULL,
    column_name  VARCHAR2(200) NOT NULL,
    column_value VARCHAR2(4000),
    created_at   TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_RAW_FILE_NORMALIZED PRIMARY KEY (norm_id)
);

CREATE SEQUENCE RAW_FILE_NORMALIZED_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE INDEX IDX_RAW_FILE_NORM_UPLOAD ON RAW_FILE_NORMALIZED (upload_id, row_num);

-- EDA tables
CREATE TABLE EDA_RUN (
    eda_run_id   NUMBER       NOT NULL,
    user_id      NUMBER,
    snapshot_id  NUMBER,
    version_id   NUMBER,
    created_at   TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_EDA_RUN PRIMARY KEY (eda_run_id)
);

CREATE SEQUENCE EDA_RUN_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE EDA_METRIC (
    metric_id         NUMBER       NOT NULL,
    eda_run_id        NUMBER       NOT NULL,
    metric_name       VARCHAR2(200),
    metric_type       VARCHAR2(100),
    metric_key        VARCHAR2(200),
    metric_value_path VARCHAR2(1000),
    created_at        TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_EDA_METRIC PRIMARY KEY (metric_id)
);

CREATE SEQUENCE EDA_METRIC_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE EDA_STAT_RESULT (
    stat_id      NUMBER       NOT NULL,
    eda_run_id   NUMBER       NOT NULL,
    row_id       NUMBER,
    column_code  VARCHAR2(200),
    mean         NUMBER,
    median       NUMBER,
    std          NUMBER,
    min          NUMBER,
    max          NUMBER,
    q1           NUMBER,
    q3           NUMBER,
    skewness     NUMBER,
    kurtosis     NUMBER,
    missing_rate NUMBER,
    data_type    VARCHAR2(50),
    CONSTRAINT PK_EDA_STAT_RESULT PRIMARY KEY (stat_id)
);

CREATE SEQUENCE EDA_STAT_RESULT_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE EDA_OUTLIER_RESULT (
    outlier_id   NUMBER       NOT NULL,
    eda_run_id   NUMBER       NOT NULL,
    row_id       NUMBER,
    column_code  VARCHAR2(200),
    method_code_value_id VARCHAR2(100),
    flag         CHAR(1),
    reason       VARCHAR2(1000),
    CONSTRAINT PK_EDA_OUTLIER_RESULT PRIMARY KEY (outlier_id)
);

CREATE SEQUENCE EDA_OUTLIER_RESULT_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Quality issue
CREATE TABLE QUALITY_ISSUE (
    issue_id     NUMBER       NOT NULL,
    upload_id    NUMBER       NOT NULL,
    issue_type_code_value_id VARCHAR2(100),
    column_code  VARCHAR2(200),
    detail_json_path VARCHAR2(1000),
    status_code_value_id VARCHAR2(100),
    resolved_by_user_id NUMBER,
    resolved_at TIMESTAMP,
    detected_stage_code_value_id VARCHAR2(100),
    issued_at   TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_QUALITY_ISSUE PRIMARY KEY (issue_id)
);

CREATE SEQUENCE QUALITY_ISSUE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Code set/value
CREATE TABLE CODE_SET (
    code_set_id   NUMBER       NOT NULL,
    code_set_key  VARCHAR2(100) NOT NULL,
    code_set_name VARCHAR2(200),
    description   VARCHAR2(1000),
    is_active     CHAR(1)       DEFAULT 'Y' NOT NULL,
    created_at    TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_CODE_SET PRIMARY KEY (code_set_id)
);

CREATE TABLE CODE_VALUE (
    code_value_id NUMBER       NOT NULL,
    code_set_id   NUMBER       NOT NULL,
    code          VARCHAR2(100) NOT NULL,
    code_name     VARCHAR2(200),
    sort_order    NUMBER,
    is_active     CHAR(1)       DEFAULT 'Y' NOT NULL,
    effective_from TIMESTAMP,
    effective_to  TIMESTAMP,
    created_at    TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_CODE_VALUE PRIMARY KEY (code_value_id)
);

CREATE SEQUENCE CODE_SET_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE CODE_VALUE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Seed minimal code sets/values
INSERT INTO CODE_SET (code_set_id, code_set_key, code_set_name, description, is_active)
VALUES (CODE_SET_SEQ.NEXTVAL, 'RAW_FILE_STATUS', 'Raw file status', 'Raw file pipeline statuses', 'Y');

INSERT INTO CODE_SET (code_set_id, code_set_key, code_set_name, description, is_active)
VALUES (CODE_SET_SEQ.NEXTVAL, 'QUALITY_ISSUE_TYPE', 'Quality issue type', 'Data quality issue types', 'Y');

INSERT INTO CODE_SET (code_set_id, code_set_key, code_set_name, description, is_active)
VALUES (CODE_SET_SEQ.NEXTVAL, 'QUALITY_STATUS', 'Quality status', 'Quality issue status', 'Y');

INSERT INTO CODE_SET (code_set_id, code_set_key, code_set_name, description, is_active)
VALUES (CODE_SET_SEQ.NEXTVAL, 'DETECTED_STAGE', 'Detected stage', 'Detection stage', 'Y');

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'UPLOADED', 'Uploaded', 1, 'Y'
  FROM CODE_SET WHERE code_set_key = 'RAW_FILE_STATUS';

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'VALIDATED', 'Validated', 2, 'Y'
  FROM CODE_SET WHERE code_set_key = 'RAW_FILE_STATUS';

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'FAILED', 'Failed', 3, 'Y'
  FROM CODE_SET WHERE code_set_key = 'RAW_FILE_STATUS';

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'INGESTED', 'Ingested', 4, 'Y'
  FROM CODE_SET WHERE code_set_key = 'RAW_FILE_STATUS';

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'NORMALIZED', 'Normalized', 5, 'Y'
  FROM CODE_SET WHERE code_set_key = 'RAW_FILE_STATUS';

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'MISSING_RATE', 'Missing rate', 1, 'Y'
  FROM CODE_SET WHERE code_set_key = 'QUALITY_ISSUE_TYPE';

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'OPEN', 'Open', 1, 'Y'
  FROM CODE_SET WHERE code_set_key = 'QUALITY_STATUS';

INSERT INTO CODE_VALUE (code_value_id, code_set_id, code, code_name, sort_order, is_active)
SELECT CODE_VALUE_SEQ.NEXTVAL, code_set_id, 'EDA', 'EDA', 1, 'Y'
  FROM CODE_SET WHERE code_set_key = 'DETECTED_STAGE';
