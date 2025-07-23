package com.pandaterry.infrastructure.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import com.pandaterry.presentation.dto.request.DatabaseConnectionRequest;
import com.pandaterry.presentation.dto.response.DatabaseConnectionResponse;

import java.util.List;

public class DtoConverter {
    public static <T> String objectToString(T dto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화하는데 실패했습니다 -> " + e);
        }
    }

    public static <T> T stringToObject(String json, Class<T> type) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화하는데 실패했습니다 -> " + e);
        }
    }

    public static <T> List<T> stringToList(String json, Class<T> C) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<List<T>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화하는데 실패했습니다 -> " + e);
        }
    }
}
