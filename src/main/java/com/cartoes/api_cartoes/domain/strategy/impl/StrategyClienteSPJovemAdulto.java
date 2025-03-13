package com.cartoes.api_cartoes.domain.strategy.impl;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.strategy.StrategyAvaliacaoCartao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StrategyClienteSPJovemAdulto implements StrategyAvaliacaoCartao {

    private final StrategyPadrao strategyPadrao;

    @Value("${aplicacao.cliente.sp-jovem-adulto-idade-min:25}")
    private int idadeJovemAdultoMin;

    @Value("${aplicacao.cliente.sp-jovem-adulto-idade-max:30}")
    private int idadeJovemAdultoMax;

    @Value("${aplicacao.estados.sao-paulo:SP}")
    private String ufSaoPaulo;

    @Override
    public boolean seAplica(Cliente cliente) {
        return ufSaoPaulo.equalsIgnoreCase(cliente.getUf()) &&
                cliente.getIdade() >= idadeJovemAdultoMin &&
                cliente.getIdade() < idadeJovemAdultoMax;
    }

    @Override
    public List<Cartao> avaliarCartoes(Cliente cliente) {
        return strategyPadrao.avaliarCartoes(cliente);
    }
}