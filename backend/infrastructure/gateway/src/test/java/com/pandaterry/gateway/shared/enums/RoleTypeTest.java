package com.pandaterry.gateway.shared.enums;

import com.pandaterry.msa_contracts.enums.auth.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTypeTest {

    @Test
    @DisplayName("AGENT 문자열은 RoleType.AGENT 로 매핑되어야 한다")
    void from_shouldReturnAgent() {
        RoleType role = RoleType.from("AGENT");
        assertThat(role).isEqualTo(RoleType.AGENT);
    }
}
