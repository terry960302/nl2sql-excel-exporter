package com.pandaterry.msa_contracts.dto.schema.request;

import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import io.micronaut.serde.annotation.Serdeable;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Serdeable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegisterSchemaRequest{
        private UUID datasourceId;
        private String name;
        private List<TableSchema> schemas;
        private String rawSchema;
}
