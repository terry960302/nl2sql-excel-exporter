# NL2SQL Excel Exporter
> natural language to SQL and generating EXCEL from it.

[랜딩페이지 바로가기](nl2sql.pandaterry.com)

## 서비스별 역할

| 구성 요소 | 주요 역할 | 통신 방식 | 비고 |
| --- | --- | --- | --- |
| 🔹 **API Gateway** | 외부 요청 수신, 라우팅, 인증 필터링, 라벨링, QoS 제어 | REST (WebFlux 기반) | 모든 요청은 이곳을 거침 |
| 🔸 **Auth Service** | JWT 발급, 사용자 인증 | REST | 인증 필터만 수행 후 위임 |
| 🔸 **Schema Service** | 스키마/alias 관리 | REST + Kafka 이벤트 발행 | 사용자별 context 처리 핵심 |
| 🔸 **Query Service** | 자연어 → SQL 처리, Excel 반환 | REST + 내부 LLM 호출 (gRPC/HTTP) | LLM 연산/큐 처리 포함 |
| 🔸 **Quota Service** | 요청 수 제한, 초과 판단 | REST + Redis + Kafka 이벤트 발행 | 요청 시 선 체크 |

## 서비스간 통신

| Source → Target | 방식 | 설명 |
| --- | --- | --- |
| Gateway → Service | REST (WebClient / Feign) | 요청 디스패치 |
| Query → LLM (Python) | HTTP or gRPC | ONNX Inference |
| Query → Quota | REST or Redis Command | quota 체크 |
| Query → Logging | Kafka or Internal Event | 요청 성공/실패 로그 |
| Schema → EventBus | Kafka or Spring Event | alias 변경 이벤트 |
| Quota → EventBus | Kafka or Spring Event | QuotaUsed 이벤트 |
| Prometheus → 각 서비스 | HTTP scrape | `/actuator/prometheus` endpoint |

## 기술스택

| 구성 요소 | 기술 스택 | 이유 |
| --- | --- | --- |
| **API Gateway** | `Spring Cloud Gateway` + `Spring WebFlux` | JWT 인증, rate-limit 필터, 라우팅 처리에 적합 |
| **Auth Service** | `Spring Boot` + `Spring Security` + `Redis` | JWT 발급/검증, 세션 캐싱에 이상적 |
| **Schema Service** | `Spring Boot` + `Spring Data JPA` + `PostgreSQL` + `Kafka` | 스키마/alias 저장 및 변경 이벤트 발행용 |
| **Query Service** | `Spring Boot` + `WebClient` + `ONNX Runtime` (or gRPC to Python), `EasyExcel` | 비동기 처리 + Excel 생성 + LLM 연동까지 전담 |
| **LLM Inference** | `Python FastAPI` + `ONNX Runtime` (or Huggingface Transformers) | NL2SQL 모델 서빙에 가볍고 유연한 선택 |
| **Quota Service** | `Spring Boot` + `Redis` + `Lua Script` + `Kafka` | 빠른 quota 체크 + 이벤트 기반 처리 가능 |
| **Event Bus** | `Apache Kafka` | 느슨한 결합 + 이벤트 기반 기록 및 확장 유연성 |
| **Monitoring** | `Spring Actuator` + `Micrometer` + `Prometheus` + `Grafana` | 표준 모니터링 스택으로 빠른 통합 가능 |
| **Storage (Excel)** | `AWS S3` or `Local Disk` + `Pre-signed URLs` | 파일 다운로드 링크 제공을 위한 안전한 처리 방식 |
| **Build/Deploy** | `Gradle` (Java), `Poetry` (Python), `Docker`, `GitHub Actions`, `k8s` or `Docker Compose` | 서비스별 독립 배포 환경 구축에 용이 |

## 아키텍처 설계도

![Image](https://github.com/user-attachments/assets/ff571366-9db4-4fdd-ac58-f04519429012)
