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
        private String alias;
        private String username;
        private String password;
        private String host;
        private int port;
        private DatabaseType dbType;
        private DatabaseEngineType engineType;
        private String description;

}