package com.cartoes.api_cartoes.application.factory.impl;

import com.cartoes.api_cartoes.application.dto.response.CartaoResponse;
import com.cartoes.api_cartoes.application.factory.CartaoFactory;
import com.cartoes.api_cartoes.domain.entity.Cartao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartaoFactoryImpl implements CartaoFactory {

    @Override
    public CartaoResponse toCartaoResponse(Cartao cartao) {
        return CartaoResponse.builder()
                .tipoCartao(cartao.getTipoCartao())
                .valorAnuidadeMensal(cartao.getValorAnuidadeMensal())
                .valorLimiteDisponivel(cartao.getValorLimiteDisponivel())
                .status(cartao.getStatus())
                .build();
    }

    @Override
    public List<CartaoResponse> toCartaoResponseList(List<Cartao> cartoes) {
        return cartoes.stream()
                .map(this::toCartaoResponse)
                .collect(Collectors.toList());
    }
}