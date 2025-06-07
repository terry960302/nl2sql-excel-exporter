package com.pandaterry.msa_contracts.dto.query.response;

import lombok.*;

import java.util.UUID;

public record NaturalLanguageQueryResponse(String filename, String downloadUrl) {
}