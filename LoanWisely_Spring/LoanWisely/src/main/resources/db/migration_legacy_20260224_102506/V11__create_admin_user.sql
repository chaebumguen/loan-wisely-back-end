-- ADMIN_USER
CREATE TABLE ADMIN_USER (
    admin_id      NUMBER       NOT NULL,
    username      VARCHAR2(100) NOT NULL,
    password_hash VARCHAR2(200) NOT NULL,
    status        VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,
    created_at    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_ADMIN_USER PRIMARY KEY (admin_id),
    CONSTRAINT UK_ADMIN_USER_USERNAME UNIQUE (username)
);

CREATE SEQUENCE ADMIN_USER_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- ADMIN_USER_ROLE
CREATE TABLE ADMIN_USER_ROLE (
    role_id   NUMBER       NOT NULL,
    admin_id  NUMBER       NOT NULL,
    role_name VARCHAR2(100) NOT NULL,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_ADMIN_USER_ROLE PRIMARY KEY (role_id)
);

CREATE SEQUENCE ADMIN_USER_ROLE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE INDEX IDX_ADMIN_USER_ROLE_ADMIN ON ADMIN_USER_ROLE (admin_id);

-- Default admin user (username: admin, password: admin1234)
-- NOTE: Uses "plain:" prefix for initial bootstrap. Change to BCrypt hash after setup.
INSERT INTO ADMIN_USER (admin_id, username, password_hash, status, created_at)
VALUES (ADMIN_USER_SEQ.NEXTVAL, 'admin', 'plain:admin1234', 'ACTIVE', SYSTIMESTAMP);

-- Roles for default admin
INSERT INTO ADMIN_USER_ROLE (role_id, admin_id, role_name, created_at)
VALUES (ADMIN_USER_ROLE_SEQ.NEXTVAL, (SELECT admin_id FROM ADMIN_USER WHERE username='admin'), 'SUPER_ADMIN', SYSTIMESTAMP);

INSERT INTO ADMIN_USER_ROLE (role_id, admin_id, role_name, created_at)
VALUES (ADMIN_USER_ROLE_SEQ.NEXTVAL, (SELECT admin_id FROM ADMIN_USER WHERE username='admin'), 'POLICY_WRITER', SYSTIMESTAMP);

INSERT INTO ADMIN_USER_ROLE (role_id, admin_id, role_name, created_at)
VALUES (ADMIN_USER_ROLE_SEQ.NEXTVAL, (SELECT admin_id FROM ADMIN_USER WHERE username='admin'), 'POLICY_APPROVER', SYSTIMESTAMP);
