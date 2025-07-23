package com.pandaterry.msa_contracts.event;

import java.util.UUID;

public record QuotaUsageEvent(UUID orgId, UUID userId, long increment) {
}
