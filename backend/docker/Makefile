.PHONY: local dev prod stop clean

infra:
	@echo "인프라 환경(로컬전용)을 시작합니다..."
	@export $$(cat .env.infra | xargs) && docker compose up -d
	@echo "인프라 환경(DB, 모니터링, 카프카)을 성공적으로 시작했습니다."
	@echo "- Databases: PostgreSQL instances"
	@echo "      - auth: http://localhost:5431"
	@echo "      - schema: http://localhost:5433"
	@echo "      - query: http://localhost:5434"
	@echo "      - quota: http://localhost:5435"
	@echo "- Kafka: http://localhost:9092"
	@echo "- Kafka-UI: http://localhost:8090"
	@echo "- Grafana: http://localhost:3000"
	@echo "- Prometheus: http://localhost:9090"
	@echo "- Jaeger: http://localhost:16686"

local:
	@echo "local 환경을 시작합니다..."
	@export $$(cat .env.local | xargs) && docker compose up -d
	@echo "local 환경을 성공적으로 시작했습니다. Gateway: http://localhost:8080"

dev:
	@echo "dev 환경을 시작합니다..."
	@export $$(cat .env.dev | xargs) && docker compose up -d
	@echo "dev 환경을 성공적으로 시작했습니다. Gateway: http://localhost:8080"

prod:
	@echo "prod 환경을 시작합니다..."
	@export $$(cat .env.prod | xargs) && docker compose up -d
	@echo "prod 환경을 성공적으로 시작했습니다. Gateway: http://localhost:8080"

stop:
	@docker compose down

clean:
	@docker compose down -v
	@docker system prune -f

logs:
	@docker compose logs -f

status:
	@docker compose ps
