package com.cartoes.api_cartoes.application.dto.response;

import com.cartoes.api_cartoes.application.dto.ClienteDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResponse {
    @JsonProperty("numero_solicitacao")
    private String numeroSolicitacao;

    @JsonProperty("data_solicitacao")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dataSolicitacao;

    private ClienteDTO cliente;

    @JsonProperty("cartoes_ofertados")
    private List<CartaoResponse> cartoesOfertados;
}