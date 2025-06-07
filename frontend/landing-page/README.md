# NL2SQL 랜딩 페이지

이 프로젝트는 NL2SQL 서비스의 랜딩 페이지를 위한 React 기반 웹 애플리케이션입니다.

## 기술 스택

- React 19
- TypeScript
- Vite
- Styled Components
- Emotion
- Headless UI
- Heroicons

## 시작하기

### 필수 조건

- Node.js (최신 LTS 버전 권장)
- npm 또는 yarn

### 설치

```bash
# 의존성 설치
npm install
# 또는
yarn install
```

### 개발 서버 실행

```bash
npm run dev
# 또는
yarn dev
```

### 프로덕션 빌드

```bash
npm run build
# 또는
yarn build
```

### 빌드된 결과물 미리보기

```bash
npm run preview
# 또는
yarn preview
```

## 프로젝트 구조

```
├── src/                    # 소스 코드
│   ├── assets/            # 이미지, 폰트 등 정적 자원
│   ├── components/        # 컴포넌트
│   │   ├── atoms/        # 기본 UI 컴포넌트 (버튼, 입력 필드 등)
│   │   ├── molecules/    # atoms를 조합한 복합 컴포넌트
│   │   ├── organisms/    # molecules를 조합한 더 큰 단위의 컴포넌트
│   │   └── templates/    # 페이지 레이아웃 템플릿
│   ├── constants/        # 상수 정의
│   ├── lib/             # 라이브러리 설정 및 유틸리티
│   ├── utils/           # 유틸리티 함수
│   ├── App.tsx          # 메인 애플리케이션 컴포넌트
│   ├── main.tsx         # 애플리케이션 진입점
│   └── index.css        # 전역 스타일
├── public/               # 정적 파일
├── dist/                # 빌드 결과물
└── index.html           # 진입점 HTML 파일
```

## 스크립트

- `dev`: 개발 서버 실행
- `build`: 프로덕션용 빌드
- `lint`: ESLint를 사용한 코드 검사
- `preview`: 빌드된 결과물 미리보기

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
