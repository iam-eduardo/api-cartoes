package com.cartoes.api_cartoes.domain.entity;


import com.cartoes.api_cartoes.domain.enums.CartaoStatus;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa um cartão de crédito no domínio de negócio.
 * Esta entidade contém todos os dados necessários sobre um cartão
 * e seu status após avaliação.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cartao {
    private TipoCartao tipoCartao;
    private BigDecimal valorAnuidadeMensal;
    private BigDecimal valorLimiteDisponivel;
    private CartaoStatus status;
}
