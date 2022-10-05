# my service
## 프로젝트 설명
### 개요
`회원 가입`, `로그인`, `내정보 조회` 등 사용자 정보를 관리/조회하는 서비스를 개발합니다. 

해당 프로젝트를 바탕으로 회원 정보를 필요로로하는 다른 서비스로 기능을 확장할 수 있습니다.

### 제공 기능
|기능|설명|
|---|---|
|회원가입|신규 회원 가입을 할 수 있습니다.<br>회원 가입은 사전에 `전화번호 인증`을 통과해야 가능합니다.|
|로그인|아래 항목을 이용해서 로그인 할 수 있습니다.<br>- 로그인 아이디<br>- 이메일<br>- 핸드폰 번호<br>로그인을 하면 토큰이 발행됩니다.<br>토큰은 10분간 유효하며, 토큰이 필요한 API에 이 값을 사용할 수 있습니다.|
|내 정보 조회|로그인 한 사용자는 자신의 정보를 조회할 수 있습니다.|
|비밀번호 초기화|비밀번호를 분실한 경우, 로그인 없이 비밀번호를 초기화할 수 있습니다.<br>비밀번호 초기화는 사전에 `전화번호 인증`을 통과해야 가능합니다.<br>비밀번호가 초기화되면, 기존 로그인 사용자를 로그아웃하기 위한 푸시메시지가 전송됩니다.|
|전화번호 인증|전화번호를 이용한 본인인증 기능을 지원합니다.<br>전화번호 인증을 요청하면 4자리의 인증번호가 문자메시지로 전송되며, 30분동안 유효합니다.|

## 개발 환경 설정 및 실행
### 프로그램 실행
프로그램 실행
1. 도커 컴포즈 실행: mysql, redis 컨터이너를 실행합니다. 
   ```
   docker-compose up -d
   ```
2. 프로그램 빌드
   ```
   // 테스트 포함 빌드
   ./gradlew clean build
   
   // 테스트 제외 빌드
   ./gradlew clean build -x test 
   ```
3. 프로그램 실행
   ```
   java -jar build/generated/source/kapt/main/libs/myservice-0.0.1.jar
   ```
4. 프로그램 종료: 도커 컴포즈를 종료합니다. 
   ```
   docker-compose down
   ```

프로그램 실행 확인
- 요청
   ```
   curl http://localhost:8080/actuator/health 
   ```
- 응답
   ```
   {"status":"UP"}
   ```

### 기능 별 API 실행
API 스펙
- ['api-spec.html'](/apitest/api-spec.html)에서 확인 가능합니다. 

API 실행
- 'intelliJ IDEA'에서 실행: [`gui_api_test.http`](/apitest/gui_api_test.http) 파일을 이용해서 실행합니다.
- 'command line'에서 실행: [`apitest/cli_api_test.http`](/apitest/cli_api_test.md) 파일을 이용해서 실행합니다. curl을 이용한 테스트를 합니다.

코드 진입점

|기능|API|코드 진입점|
|---|---|---|
|전화번호 인증 요청|[POST] /api/v1/users/authentication/request|UseAuthenticationController#requestAuthentication|
|전화번호 인증 검증|[POST] /api/v1/users/authentication/check|UseAuthenticationController#checkAuthentication|
|회원가입 요청|[POST] /api/v1/users|UserController#createUser|
|로그인|[POST] /api/v1/login<br>- [타입] USERNAME: 로그인 아이디<br>- [타입] PHONE_NUMBER: 전화번호<br>- [타입] EMAIL: 이메일|인증 필터: CustomAuthenticationFilter<br>인가 필터: CustomAuthorizationFilter<br>사용자 요청별 처리: UserLoginService#loadUserByUsername|
|내 정보 조회|[GET] /api/v1/users/my|UserController#findMyInfo|
|비밀번호 초기화|[POST] /api/v1/users/reset-password|UserController#resetPassword|

### DB 접속 정보
접속 정보
- host: localhost
- port: 8080
- db: myservice
- username: svc-user
- password: svc-password

DB 초기화 스크립트: [init.sql](/src/main/resources/db/init.sql)


### 사용 기술
|기능|라이브러리|기타|
|---|---|---|
|언어|코틀린 1.6.21||
|프레임워크|스프링 2.7.4||
|DB|Mysql||
|Cache|Redis|인증 정보를 저장할 때 사용|
|스프링 라이브러리|spring-boot-starter-web<br>spring-boot-starter-data-jpa<br>spring-boot-starter-data-redis<br>spring-boot-starter-security<br>spring-boot-starter-actuator<br>spring-boot-starter-validation||
|jwt token|com.auth0:java-jwt||
|querydsl|com.querydsl:querydsl-jpa|여러 Entity를 조회하는 query가 필요할 때 사용|
|testcontainers|org.testcontainers:testcontainers<br>org.testcontainers:junit-jupiter<br>org.testcontainers:mysql|테스트 시, 컨테이너 기반으로 DB, Cache 동작|
|api spec|org.springframework.restdocs:spring-restdocs-mockmvc|테스트 기반 API Spec. 작성|

## 기능 별 세부 구현 설명
### 전화번호 인증
인증번호 생성 및 저장
- 4자리의 랜덤 숫자를 생성합니다.
- Redis를 이용해서 저장되며, 30분동안 유지됩니다. 
- 캐시 저장 메서드는 도메인 레이어에서 인터페이스를 가지고, 인프라스트럭처에 구현체가 있습니다.

인증번호 검증
- Redis에 있는 값과 요청받은 값이 일치하는지 비교합니다.
- `v1/users/authentication/check` API를 이용해서 클라이언트에서 사전에 일치여부를 비교할 수도 있습니다.
- `회원가입`/`비밀번호 초기화` 요청을 할 때에도, 인증번호를 같이 전달해줘야합니다.

인증번호 만료
- ttl(30분)이 지나면 삭제됩니다.
- ttl 이전에 `회원가입`/`비밀번호 초기화`가 완료되면 삭제됩니다. 

### 로그인
AccessToken 발급
- 로그인을 성공하면 JWT AccessToken이 발급됩니다. 토큰은 사용자 정보를 필요로 하는 API에서 사용됩니다.
- 10분간 사용 가능하며, RefreshToken은 발급 기능은 현재 구현되어 있지 않습니다. 

로그인 방법
- 로그안 아이디, 이메일, 전화번호를 이용해서 로그인 가능합니다. 
- 각 로그인 타입의 구분을 위해서 로그인 API를 호출할 때, '로그인 타입' 값을 파라미터로 전달 받습니다. 서버에서는 각 타입별로 사용자 정보를 읽어와서 인증정보를 생성합니다. 

### 비밀번호 초기화
푸쉬 알람 전달
- 비밀번호 초기화가 완료되면 기존 사용자의 강제로그아웃을 위해서 사용자에게 푸쉬 알람을 전달합니다. 
- 코드에서는 실제 푸쉬가 나가지는 않고, 로그로만 기록하고 있습니다.

### API 응답 모델
공통 API 응답 모델의 양식
- result: 요청의 성공 여부. SUCCESS, FAIL
   - HttpStatus는 200이지만, result가 FAIL일 수 있습니다. 예: 요청 처리를 성공했지만, 내부 로직의 validation을 통과 못했을 경우 
- data: 응답 데이터
- errorCode: 에러 발생 시, 에러 코드
- message: 에러 발생 시, 에러 메시지

### 테스트
유닛테스트와 통합테스트가 구현되어 있습니다.

유닛테스트 
- 도메인 레이어: 비지니스 로직의 동작을 검증합니다. 
- 서비스 레이어: 입력 값 검증과, 도메인 서비스를 제대로 호출하는지 검증합니다. 
- 프레젠테이션 레이어: 사용자 입력 값 검증과, 서비스 레이어 호출, 결과 응답을 검증합니다. 
- 인프라 레이어: 별도의 테스트는 없습니다. 다른 테스트에서 사용될 수 있도록, Dummy구현체가 있습니다. 예: LocalCacheRepository, LocalEventPublisher 등

통합테스트
- `testcontainers`를 이용합니다. 테스트가 동작할 때, MySQL, Redis 컨테이너를 구동하고 사용합니다. 
   - local db보다 시간이 느리다는 단점이 있는데, 상용환경과 최대한 일치하는 환경에서 테스트할 수 있는 장점이 있습니다.

### Repository 분리
Repository를 아래 2개 기준으로 분리했습니다. 
- 단일 Entity를 기준으로 조회하는 Repository
- 여러 Entity가 Join되어 결과를 가져올 수 있는 Repository 

프로젝트에는 UserRepository의 구현체인 DefaultUserRepository가 JpaUserRepository와 JpaUserQueryRepository를 주입 받습니다. 
- JpaUserRepository: JpaRepository를 상속하는 일반적인 Entity 조회 Repository 입니다. 
- JpaUserQueryRepository: queryDsl을 이용해서 여러 Entity를 조회하고, 조회에 필요한 칼럼만 가지는 값 객체를 응답하는 Repository 입니다.  

### Dummy로 구현한 기능
아래 2개 기능은 Dummy로 동작합니다. 
- SMS 전송: SmsNotificationSender
- 사용자 푸쉬 전송: FcmUserNotifier
