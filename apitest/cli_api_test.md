### [전화번호 인증] 타입 정보
- CREATE_USER: 회원 가입
- RESET_PASSWORD: 비밀번호 초기화

### [회원가입] 인증번호 요청
```bash
curl --location --request POST 'http://localhost:8080/api/v1/users/authentication/request' \
--header 'Content-Type: application/json' \
--data-raw '{
  "type": "CREATE_USER",
  "phoneNumber": "01055556666"
}'
```

### [회원가입] 인증번호 검사 -> 응답받은 authenticationNumber 값을 변경합니다
```bash
curl --location --request POST 'http://localhost:8080/api/v1/users/authentication/check' \
--header 'Content-Type: application/json' \
--data-raw '{
  "type": "CREATE_USER",
  "phoneNumber": "01055556666",
  "authenticationNumber": "{authenticationNumber}"
}'
```

### [회원가입] 요청 -> 응답받은 authenticationNumber 값을 변경합니다
```bash
curl --location --request POST 'http://localhost:8080/api/v1/users' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "myuser@myservice.com",
  "phoneNumber": "01055556666",
  "username": "myuser",
  "password": "Secret123!@#",
  "nickname": "mynickname",
  "name": "myuser",
  "authenticationNumber": "{authenticationNumber}"
}'
```

### [로그인] Query Parameter 정보
- loginType: 로그인 타입(USERNAME: 로그인 아이디, PHONE_NUMBER: 전화번호, EMAIL: 이메일)
- loginId: 로그인 타입 별 로그인 아이디
- password: 비밀번호

### [로그인] 사용자 아이디
```bash
curl --location --request POST 'http://localhost:8080/api/v1/login?loginType=USERNAME&loginId=myuser&password=Secret123!@%23'
```

### [로그인] 핸드폰 번호
```bash
curl --location --request POST 'http://localhost:8080/api/v1/login?loginType=PHONE_NUMBER&loginId=01055556666&password=Secret123!@%23'
```

### [로그인] 이메일
```bash
curl --location --request POST 'http://localhost:8080/api/v1/login?loginType=EMAIL&loginId=myuser@myservice.com&password=Secret123!@%23'
```

### [내 정보 조회] 요청 -> 응답받은 accessToken 값을 변경합니다
```bash
curl --location --request GET 'http://localhost:8080/api/v1/users/my' \
--header 'Authorization: bearer {accessToken}'
```

### [비밀번호 초기화] 인증 요청
```bash
curl --location --request POST 'http://localhost:8080/api/v1/users/authentication/request' \
--header 'Content-Type: application/json' \
--data-raw '{
  "type": "RESET_PASSWORD",
  "phoneNumber": "01055556666"
}'
```

### [비밀번호 초기화] 요청 -> 응답받은 authenticationNumber 값을 변경합니다
```bash
curl --location --request POST 'http://localhost:8080/api/v1/users/reset-password' \
--header 'Content-Type: application/json' \
--data-raw '{
  "phoneNumber": "01055556666",
  "newPassword": "NewSecret123!@#",
  "authenticationNumber": "{authenticationNumber}"
}'
```

### [비밀번호 초기화] 초기화된 번호로 로그인 - 핸드폰 번호
```bash
curl --location --request POST 'http://localhost:8080/api/v1/login?loginType=PHONE_NUMBER&loginId=01055556666&password=NewSecret123!@%23'
```