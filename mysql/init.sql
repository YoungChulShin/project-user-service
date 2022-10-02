-- DB 생성
CREATE DATABASE `myservice` DEFAULT CHARACTER SET = `utf8mb4` DEFAULT COLLATE `utf8mb4_unicode_ci`;

-- 사용자 생성
CREATE USER 'svc-user'@'%' IDENTIFIED BY 'svc-password';
CREATE USER 'svc-user'@'localhost' IDENTIFIED BY 'svc-password';

GRANT ALL PRIVILEGES ON myservice.* TO 'svc-user'@'%';
GRANT ALL PRIVILEGES ON myservice.* TO 'svc-user'@'localhost';

FLUSH PRIVILEGES;