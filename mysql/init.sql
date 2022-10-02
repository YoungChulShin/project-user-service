-- DB 생성
CREATE DATABASE `myservice` DEFAULT CHARACTER SET = `utf8mb4` DEFAULT COLLATE `utf8mb4_unicode_ci`;

-- 사용자 생성
CREATE USER 'svc-user'@'%' IDENTIFIED BY 'svc-password';
CREATE USER 'svc-user'@'localhost' IDENTIFIED BY 'svc-password';

GRANT ALL PRIVILEGES ON myservice.* TO 'svc-user'@'%';
GRANT ALL PRIVILEGES ON myservice.* TO 'svc-user'@'localhost';

FLUSH PRIVILEGES;

-- DB 선택
USE myservice;

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