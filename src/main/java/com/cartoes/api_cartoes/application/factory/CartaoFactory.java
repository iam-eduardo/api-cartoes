package com.cartoes.api_cartoes.application.factory;

import com.cartoes.api_cartoes.application.dto.response.CartaoResponse;
import com.cartoes.api_cartoes.domain.entity.Cartao;

import java.util.List;

public interface CartaoFactory {
    /**
     * Converte uma entidade de domínio Cartao para um DTO CartaoResponse
     */
    CartaoResponse toCartaoResponse(Cartao cartao);

    /**
     * Converte uma lista de entidades de domínio Cartao para uma lista de DTOs CartaoResponse
     */
    List<CartaoResponse> toCartaoResponseList(List<Cartao> cartoes);
}
