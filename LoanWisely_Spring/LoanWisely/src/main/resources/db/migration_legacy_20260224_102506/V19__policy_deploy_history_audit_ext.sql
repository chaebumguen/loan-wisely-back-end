-- Policy deploy history
CREATE TABLE POLICY_DEPLOY_LOG (
    deploy_id          NUMBER       NOT NULL,
    policy_id          NUMBER       NOT NULL,
    previous_policy_id NUMBER,
    action             VARCHAR2(20) NOT NULL, -- DEPLOY / ROLLBACK
    reason             VARCHAR2(1000),
    actor_id           VARCHAR2(100),
    deployed_at        TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_POLICY_DEPLOY_LOG PRIMARY KEY (deploy_id)
);

CREATE SEQUENCE POLICY_DEPLOY_LOG_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE INDEX IDX_POLICY_DEPLOY_LOG_POLICY
    ON POLICY_DEPLOY_LOG (policy_id, deployed_at DESC);

-- Audit log expansion
ALTER TABLE AUDIT_LOG ADD (
    actor_roles VARCHAR2(400),
    detail_json CLOB
);
