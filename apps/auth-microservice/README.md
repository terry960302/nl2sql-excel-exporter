# Auth Microservice

인증 및 인가를 담당하는 마이크로서비스입니다.

## 기술 스택

- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- JWT (JSON Web Token)
- Gradle
- H2 Database (테스트용)

## 주요 기능

1. **회원가입**

   - 이메일 중복 검사
   - 비밀번호 정책 검증
   - 기본 플랜(BASIC)으로 조직 생성

2. **로그인**

   - JWT 기반 인증
   - Access Token & Refresh Token 발급
   - 토큰 검증

3. **사용자 정보 조회**

   - 사용자 기본 정보
   - 조직 정보
   - 플랜 정보
   - 할당량 정보

4. **토큰 관리**
   - Refresh Token 갱신
   - 토큰 폐기 (로그아웃)

## 프로젝트 구조

```
src/main/java/com/pandaterry/auth_microservice/
├── application/          # 애플리케이션 서비스 계층
│   ├── service/         # 비즈니스 로직
│   └── validator/       # 유효성 검증
├── domain/              # 도메인 계층
│   ├── entity/         # 도메인 엔티티
│   ├── repository/     # 리포지토리 인터페이스
│   └── exception/      # 도메인 예외
├── infrastructure/      # 인프라스트럭처 계층
│   ├── config/         # 설정
│   ├── persistence/    # 영속성 구현
│   └── security/       # 보안 관련 구현
└── presentation/       # 프레젠테이션 계층
    ├── controller/     # REST 컨트롤러
    ├── dto/           # 데이터 전송 객체
    └── advice/        # 예외 처리
```

## API 엔드포인트

### 인증 관련

- `POST /auth/signup` - 회원가입
- `POST /auth/login` - 로그인
- `POST /auth/logout` - 로그아웃
- `POST /auth/token/refresh` - 토큰 갱신

### 사용자 정보

- `GET /auth/me` - 사용자 정보 조회

## 보안

- JWT 기반 인증
- 비밀번호 암호화 (BCrypt)
- 토큰 기반 인가
- CORS 설정
- Rate Limiting (플랜별)

## 데이터베이스

### 주요 엔티티

- User (사용자)
- Organization (조직)
- Plan (플랜)
- RefreshToken (리프레시 토큰)

## 테스트

- JUnit 5
- Mockito
- Spring Security Test
- H2 Database (테스트용)

## 실행 방법

1. 환경 설정

```bash
# application.yml 설정
jwt:
  secret-key: ${JWT_SECRET_KEY:your-secret-key-here}
  access-token:
    expiration-time: 7200000 # 2시간
  refresh-token:
    expiration-time: 2592000000 # 30일
```

2. 빌드 및 실행

```bash
./gradlew build
java -jar build/libs/auth-microservice-0.0.1-SNAPSHOT.jar
```

## 플랜 정보

### BASIC

- 월간 할당량: 100건
- 초당 요청 제한: 1건

### PRO

- 월간 할당량: 500건
- 초당 요청 제한: 3건

## 주의사항

1. JWT Secret Key는 반드시 환경 변수로 설정
2. 프로덕션 환경에서는 적절한 데이터베이스 사용
3. Rate Limiting 설정 확인
4. CORS 설정 확인

## 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.
