package com.pandaterry.quota_microservice.unit.presentation.controller;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.quota_microservice.infrastructure.messaging.QuotaUsageProducer;
import com.pandaterry.quota_microservice.presentation.controller.QuotaController;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QuotaControllerTest {

    @Test
    void recordUsage_callsProducer() {
        DummyProducer producer = new DummyProducer();
        QuotaController controller = new QuotaController(null, producer);
        QuotaUsageRecordRequest request = QuotaUsageRecordRequest.builder()
                .orgId(UUID.randomUUID())
                .increment(1L)
                .build();

        ResponseEntity<Void> response = controller.recordUsage(request);

        assertThat(producer.called).isTrue();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    static class DummyProducer implements QuotaUsageProducer {
        boolean called = false;
        @Override
        public void sendQuotaUsage(QuotaUsageRecordRequest request) {
            called = true;
        }
    }
}
