package com.pandaterry.msa_contracts.dto.schema.response;

import java.util.UUID;

public record RegisterSchemaResponse(UUID id, String name, String rawJson){
}
