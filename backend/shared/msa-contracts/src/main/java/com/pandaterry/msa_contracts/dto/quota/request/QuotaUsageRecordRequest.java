package com.pandaterry.msa_contracts.dto.quota.request;

import com.pandaterry.msa_contracts.event.QuotaUsageEvent;
import io.micronaut.serde.annotation.Serdeable;
import lombok.*;

import java.util.UUID;

@Serdeable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuotaUsageRecordRequest {
    private long increment;
}
