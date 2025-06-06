package com.pandaterry.msa_contracts.dto.quota.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class QuotaUserDetailDaily {
    private final LocalDate date;
    private final long count;
}
