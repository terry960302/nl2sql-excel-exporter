package com.pandaterry.msa_contracts.dto.schema.request;

import java.util.Map;
import java.util.UUID;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasourceUpdateRequest{
        private String name;
        private DatabaseType dbType;
        private DatabaseEngineType engineType;

}