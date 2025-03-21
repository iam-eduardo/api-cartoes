package com.cartoes.api_cartoes.application.dto.response;

import com.cartoes.api_cartoes.domain.enums.CartaoStatus;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartaoResponse {
    @JsonProperty("tipo_cartao")
    private TipoCartao tipoCartao;

    @JsonProperty("valor_anuidade_mensal")
    private BigDecimal valorAnuidadeMensal;

    @JsonProperty("valor_limite_disponivel")
    private BigDecimal valorLimiteDisponivel;

    private CartaoStatus status;
}
