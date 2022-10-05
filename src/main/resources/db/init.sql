-- DB 생성
CREATE DATABASE IF NOT EXISTS `myservice` DEFAULT CHARACTER SET = `utf8mb4` DEFAULT COLLATE `utf8mb4_unicode_ci`;

-- 사용자 생성
CREATE USER 'svc-user'@'%' IDENTIFIED BY 'svc-password';
CREATE USER 'svc-user'@'localhost' IDENTIFIED BY 'svc-password';

GRANT ALL PRIVILEGES ON myservice.* TO 'svc-user'@'%';
GRANT ALL PRIVILEGES ON myservice.* TO 'svc-user'@'localhost';

FLUSH PRIVILEGES;

-- DB 선택
USE myservice;

-- 권한 정보 테이블 생성
CREATE TABLE IF NOT EXISTS roles
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    name       VARCHAR(20)     NOT NULL COMMENT '권한 이름',
    created_at TIMESTAMP       NOT NULL COMMENT '생성 시간',
    updated_at TIMESTAMP       NULL COMMENT '마지막 업데이트 시간',
    PRIMARY KEY (id),
    UNIQUE idx_role_name (name)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- 권한 정보 초기 데이터 추가
INSERT IGNORE INTO roles (name, created_at, updated_at)
SELECT 'ROLE_ADMIN', now(), now();
INSERT IGNORE INTO roles (name, created_at, updated_at)
SELECT 'ROLE_USER', now(), now();

-- 회원 정보 테이블 생성
CREATE TABLE IF NOT EXISTS users
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    username     VARCHAR(25)     NOT NULL COMMENT '로그인 아이디',
    email        VARCHAR(50)     NOT NULL COMMENT '이메일',
    phone_number VARCHAR(15)     NOT NULL COMMENT '핸드폰 번호',
    password     VARCHAR(100)    NOT NULL COMMENT '비밀번호(암호화)',
    name         VARCHAR(50)     NOT NULL COMMENT '회원 이름',
    nickname     VARCHAR(50)     NOT NULL COMMENT '회원 별명',
    created_at   TIMESTAMP       NOT NULL COMMENT '생성 시간',
    updated_at   TIMESTAMP       NULL COMMENT '마지막 업데이트 시간',
    deleted_at   TIMESTAMP       NULL COMMENT '삭제 시간',
    PRIMARY KEY (id),
    UNIQUE idx_username (username),
    UNIQUE idx_email (email),
    UNIQUE idx_phone_number (phone_number)
    ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- 어드민 사용자 추가 (비밀번호: Secret1323)
INSERT INTO users(username, email, phone_number, password, name, nickname, created_at, updated_at, deleted_at)
SELECT 'admin', 'admin@myservice.com', '01011112222', '$2a$10$IMj2tLz5UHw1mB66/a5TTOSKljS1bHYqLtgxC0qVGIl39iL7SRMsK', 'adminuser',
       'adminuser', now(), now(), null;

-- 테스트 사용자 추가 (비밀번호: Secret1323)
INSERT INTO users(username, email, phone_number, password, name, nickname, created_at, updated_at, deleted_at)
SELECT 'testuser', 'testuser@myservice.com', '01033334444', '$2a$10$IMj2tLz5UHw1mB66/a5TTOSKljS1bHYqLtgxC0qVGIl39iL7SRMsK', 'testuser',
       'testuser', now(), now(), null;

-- 회원의 권한 정보 테이블 생성
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT UNSIGNED NOT NULL COMMENT '회원 ID',
    role_id BIGINT UNSIGNED NOT NULL COMMENT '권한 ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4;

-- 어드민 사용자 권한 추가
INSERT INTO user_roles (user_id, role_id)
SELECT (SELECT id FROM users WHERE username = 'admin'),
       (SELECT id FROM roles WHERE name = 'ROLE_ADMIN');

-- 테스트 사용자 권한 추가
INSERT INTO user_roles (user_id, role_id)
SELECT (SELECT id FROM users WHERE username = 'testuser'),
       (SELECT id FROM roles WHERE name = 'ROLE_USER');