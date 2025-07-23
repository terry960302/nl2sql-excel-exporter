package com.pandaterry.presentation.dto.request;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.*;

@Serdeable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class  DatabaseConnectionRequest{
        String id;
        String name;
        String alias;
        String description;
        String host;
        int port;
        String username;
        String password;
        DatabaseType dbType;
        DatabaseEngineType engineType;
}