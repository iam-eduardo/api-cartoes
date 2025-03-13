package com.cartoes.api_cartoes.domain.strategy.impl;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.enums.CartaoStatus;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import com.cartoes.api_cartoes.domain.strategy.StrategyAvaliacaoCartao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StrategyClienteJovem implements StrategyAvaliacaoCartao {

    @Value("${aplicacao.cliente.idade-minima:18}")
    private int idadeMinima;

    @Value("${aplicacao.cliente.idade-jovem-maxima:25}")
    private int idadeJovemMaxima;

    @Value("${aplicacao.cartoes.sem-anuidade.renda-minima:3500.00}")
    private BigDecimal rendaMinimaSemAnuidade;

    @Value("${aplicacao.cartoes.sem-anuidade.limite:1000.00}")
    private BigDecimal limiteSemAnuidade;

    @Value("${aplicacao.cartoes.sem-anuidade.anuidade:0.00}")
    private BigDecimal anuidadeSemAnuidade;

    @Override
    public boolean seAplica(Cliente cliente) {
        return cliente.getIdade() >= idadeMinima && cliente.getIdade() < idadeJovemMaxima;
    }

    @Override
    public List<Cartao> avaliarCartoes(Cliente cliente) {
        List<Cartao> cartoes = new ArrayList<>();
        // Verificar renda mínima para o cartão sem anuidade
        if (cliente.getRendaMensal().compareTo(rendaMinimaSemAnuidade) >= 0) {
            cartoes.add(Cartao.builder()
                    .tipoCartao(TipoCartao.CARTAO_SEM_ANUIDADE)
                    .valorAnuidadeMensal(anuidadeSemAnuidade)
                    .valorLimiteDisponivel(limiteSemAnuidade)
                    .status(CartaoStatus.APROVADO)
                    .build());
        }
        return cartoes;
    }
}
