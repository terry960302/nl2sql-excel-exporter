# NL2SQL Studio

NL2SQL Studio는 한국어 자연어 질의를 기반으로 사전에 정의된 데이터베이스 스키마에 맞춰 SQL을 자동 생성·실행하고, 그 결과를 엑셀 파일로 다운로드할 수 있는 설치형 애플리케이션입니다. [랜딩페이지 바로가기](https://nl2sql.pandaterry.com)

## 주요 기능

* **자연어 → SQL 변환**
  한국어 입력을 SQL로 변환하여 복잡한 쿼리 작성 없이도 데이터 추출 가능
* **로컬 실행**
  사용자의 로컬 환경에서 SQL을 실행해 보안 위험 최소화
* **엑셀 자동 생성**
  조인·셀 병합 구조를 유지한 채 읽기 쉬운 Excel 파일로 내보내기
* **다중 DB 지원**
  MySQL, PostgreSQL, Oracle 등 주요 RDBMS 지원
* **경량화된 모델 연동**
  CPU 환경에서도 실행 가능한 경량 LLM 또는 외부 API 연동

## 아키텍처

```text
┌───────────────┐         ┌───────────────┐
│  NL2SQL Studio│ ─────▶ │  SaaS 관리 서버│
│  (Desktop App) │         │   (API & UI)   │
└───────────────┘         └───────────────┘
        │
        │ SQL 실행 및 결과 전송
        ▼
┌────────────────────────┐
│    로컬 DB 커넥터      │
│  (JDBC: MySQL, PG, Oracle)  │
└────────────────────────┘
```

* **NL2SQL Studio**: React/Electron 또는 JavaFX 기반 설치형 프론트엔드
* **로컬 DB 커넥터**: Spring Boot 기반 백엔드 모듈, JDBC 드라이버로 SQL 실행
* **SaaS 관리 서버**: 사용자·스키마 관리, 로그 및 사용량 집계용 REST API 서버

## 저장소 구조

```text
nl2sql-studio/
├── backend/                   # 백엔드 모듈
│   ├── agent-studio/          # 에이전트 스튜디오 프로젝트
│   │   └── agent-studio-server/  # 에이전트 서버 코드
│   ├── docs/                  # API·ERD·아키텍처·프로세스 문서
│   ├── domains/               # 마이크로서비스 도메인별 모듈
│   ├── infrastructure/        # 게이트웨이 등 인프라 코드
│   ├── shared/                # MSA 계약(공통 모듈)
│   ├── settings.gradle        # Gradle 설정
│   ├── gradlew*               # 빌드 스크립트
│   └── README.md              # 백엔드 개요
├── frontend/                  # 프론트엔드 모듈
│   ├── agent_studio_app/      # Flutter 기반 Desktop App
│   └── landing-page/          # React/Vite 랜딩 페이지
└── README.md                  # 프로젝트 루트 개요
```

## 설치 및 실행

### 1. 사전 요구사항

* Java 17 이상
  - Ubuntu 기준 설치 예시: `sudo apt-get install openjdk-17-jdk`
* Node.js 16 이상 (프론트엔드 빌드용)
* 각 DB에 대한 JDBC 드라이버 (MySQL, PostgreSQL, Oracle)

### 2. 백엔드(SQL 실행 모듈) 실행

```bash
cd backend/agent-studio/agent-studio-server
./gradlew bootRun                # 개발용 실행
./gradlew build                  # 빌드: build/libs/*.jar 생성
java -jar build/libs/*.jar       # 로컬 테스트
```

### 3. 프론트엔드(Desktop App) 빌드 및 실행

```bash
cd frontend/agent_studio_app
flutter pub get
flutter run                       # 개발 모드 실행
# 또는
flutter build windows            # Windows 빌드 예시
```

### 4. 랜딩 페이지 실행

```bash
cd frontend/landing-page
npm install
npm run dev                      # 개발 서버 실행
npm run build                    # 프로덕션 빌드
```

### 5. 서버(SaaS 관리) 실행

```bash
cd server
./gradlew bootRun                # 개발용 실행
```

## 환경 변수 설정

* **backend/agent-studio-server**/.env

  ```properties
  DB_URL=jdbc:mysql://localhost:3306/yourdb
  DB_USER=username
  DB_PASS=password
  ```
* **frontend/landing-page**/.env

  ```properties
  VITE_API_URL=https://api.yourdomain.com
  ```
* **server**/.env

  ```properties
  SERVER_PORT=8080
  JWT_SECRET=your-secret-key
  ```

## 사용 예시

1. 앱 실행 후 상단 검색창에 자연어 질의 입력
2. ‘실행’ 클릭 시 로컬 DB에 SQL 실행
3. 결과 테이블 미리보기 및 ‘엑셀 다운로드’ 클릭
4. 병합·형식이 유지된 `.xlsx` 파일 즉시 저장

## 배포

* **Desktop App**: 각 OS용 설치 프로그램 생성 후 배포
* **Server**: Docker 이미지로 패키징 후 Kubernetes 또는 VM에 배포

## 기여 방법

1. 이 저장소를 Fork 합니다.
2. 브랜치를 생성합니다: `git checkout -b feature/your-feature`
3. 변경 사항을 커밋합니다: `git commit -m "Add your feature"`
4. 원격 저장소에 Push 합니다: `git push origin feature/your-feature`
5. PR(Pull Request)을 생성하고 리뷰를 요청합니다.

## 라이선스

MIT License
자세한 내용은 `LICENSE` 파일을 참조하세요.
