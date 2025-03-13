package com.cartoes.api_cartoes.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String codigo;
    private String mensagem;

    @JsonProperty("detalhe_erro")
    private ErrorDetail detalheErro;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String app;

        @JsonProperty("tipo_erro")
        private String tipoErro;

        @JsonProperty("mensagem_interna")
        private String mensagemInterna;
    }
}
