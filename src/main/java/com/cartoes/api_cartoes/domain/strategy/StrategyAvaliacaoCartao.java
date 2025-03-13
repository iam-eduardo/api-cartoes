package com.cartoes.api_cartoes.domain.strategy;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;

import java.util.List;

public interface StrategyAvaliacaoCartao {
    boolean seAplica(Cliente cliente);

    List<Cartao> avaliarCartoes(Cliente cliente);
}