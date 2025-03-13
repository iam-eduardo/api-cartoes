package com.cartoes.api_cartoes.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetail {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;

    // Extensões conforme RFC 9457 - campos adicionais como Map
    @JsonProperty("extensions")
    private Map<String, Object> extensions;
}
