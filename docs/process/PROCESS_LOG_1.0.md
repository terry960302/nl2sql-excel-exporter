##  기존(1.0) NL2SQL 처리 순서 (Agent 도입 전 구조)

### 시스템 구성 (기본 개념)

* 모든 컴포넌트는 **SaaS 클라우드 내**에 존재
* 고객은 웹 UI에서 질의
* SaaS 서버에서 **LLM 호출 → SQL 생성 → SQL 실행 → Excel 변환 → 응답**

---

### 처리 흐름

#### 1. **고객이 자연어 질의 전송**

```http
POST /queries
{
  "naturalText": "지난달 가입자 중 VIP 비율 알려줘",
  "datasourceId": "org-abc-db1"
}
```

#### 2. **QueryService가 다음 수행**

* 해당 datasourceId로 **스키마/alias 조회** (SchemaService 참조)
* PromptContext 구성
* 내부 LLM 호출 (ONNX or API) → SQL 생성
* AST 파싱: SELECT-only, LIMIT 등 검증

#### 3. **QueryService가 DB에 직접 SQL 실행**

* SaaS 서버 내에서 JDBC로 고객이 등록한 외부 DB 접속
* SQL 실행 (시간 제한: 5초, LIMIT 1000 등)

>  이때 DB 접속 정보 (`jdbcUrl`, `username`, `password`)는 SchemaService 내 `DATASOURCE` 테이블에 저장됨
> (보안상 우려 가능 지점)

#### 4. **쿼리 결과 → Excel 변환**

* EasyExcel 등으로 `.xlsx` 생성
* 저장소: 로컬 디스크 or S3
* 프리사인드 다운로드 링크 발급

#### 5. **사용자에게 응답 반환**

* Excel 다운로드 링크 포함한 응답
* 상태: `COMPLETED`

#### 6. **Quota 차감 및 요청 로그 저장**

* QuotaService에서 요청 수 차감
* 요청 이력 테이블에 자연어, SQL, 성공/실패, 응답 시간 등 기록

---

##  구조적으로 보면

```plaintext
[User]
  ↓
[QueryService]
  ↓    ↘
LLM   SchemaService
  ↓
DB 접속 (JDBC) ← datasource 정보 저장됨
  ↓
Excel 생성
  ↓
결과 응답 + Quota 차감
```

---

##  주요 문제점

| 문제       | 설명                                          |
|----------| ------------------------------------------- |
| 보안       | SaaS 서버가 고객 DB 접속정보를 저장/보유하고 있음             |
| 운영 부담    | QueryService가 SQL 생성부터 실행, Excel까지 모든 책임을 짐 |
| 확장 한계    | 조직별로 DB가 분리되어 있어 연결/해제 부하 커짐                |
| 커넥션 풀 부하 | SaaS 서버에서 다수의 DB 커넥션 풀 직접 관리해야 함            |
|  네트워크 이슈 | 고객 DB가 VPN 또는 VPC에 있어 SaaS에서 접근 불가능한 경우 존재  |

---

### 그래서 구조 전환의 핵심은:

> 기존 QueryService의 **“SQL 실행 책임”을 제거하고**,
> 로컬 Agent로 위임하면서 **보안·운영·확장성 이슈**를 모두 해소하는 것이었습니다.

---