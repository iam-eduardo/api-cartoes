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
public class StrategyPadrao implements StrategyAvaliacaoCartao {

    // Faixas de renda
    @Value("${aplicacao.renda.faixa-baixa-min:1000.00}")
    private BigDecimal rendaFaixaBaixaMin;

    @Value("${aplicacao.renda.faixa-baixa-max:3000.00}")
    private BigDecimal rendaFaixaBaixaMax;

    @Value("${aplicacao.renda.faixa-media-min:3000.00}")
    private BigDecimal rendaFaixaMediaMin;

    @Value("${aplicacao.renda.faixa-media-max:5000.00}")
    private BigDecimal rendaFaixaMediaMax;

    @Value("${aplicacao.renda.faixa-alta-min:5000.00}")
    private BigDecimal rendaFaixaAltaMin;

    // Valores mínimos de renda para cada cartão
    @Value("${aplicacao.cartoes.sem-anuidade.renda-minima:3500.00}")
    private BigDecimal rendaMinimaSemAnuidade;

    @Value("${aplicacao.cartoes.parceiros.renda-minima:5500.00}")
    private BigDecimal rendaMinimaParceiros;

    @Value("${aplicacao.cartoes.cashback.renda-minima:7500.00}")
    private BigDecimal rendaMinimaCashback;

    @Value("${aplicacao.cartoes.sem-anuidade.limite:1000.00}")
    private BigDecimal limiteSemAnuidade;

    @Value("${aplicacao.cartoes.sem-anuidade.anuidade:0.00}")
    private BigDecimal anuidadeSemAnuidade;

    @Value("${aplicacao.cartoes.parceiros.limite:3000.00}")
    private BigDecimal limiteParceiros;

    @Value("${aplicacao.cartoes.parceiros.anuidade:20.00}")
    private BigDecimal anuidadeParceiros;

    @Value("${aplicacao.cartoes.cashback.limite:5000.00}")
    private BigDecimal limiteCashback;

    @Value("${aplicacao.cartoes.cashback.anuidade:15.00}")
    private BigDecimal anuidadeCashback;

    @Override
    public boolean seAplica(Cliente cliente) {
        return true; // Estratégia padrão, sempre se aplica se nenhuma outra se aplicar
    }

    @Override
    public List<Cartao> avaliarCartoes(Cliente cliente) {
        List<Cartao> cartoes = new ArrayList<>();
        BigDecimal rendaMensal = cliente.getRendaMensal();

        // Faixa baixa: [1000, 3000)
        if (rendaMensal.compareTo(rendaFaixaBaixaMin) >= 0 &&
                rendaMensal.compareTo(rendaFaixaBaixaMax) < 0) {
            if (rendaMensal.compareTo(rendaMinimaSemAnuidade) >= 0) {
                cartoes.add(createCartaoSemAnuidade());
            }
        }
        // Faixa média: [3000, 5000)
        else if (rendaMensal.compareTo(rendaFaixaMediaMin) >= 0 &&
                rendaMensal.compareTo(rendaFaixaMediaMax) < 0) {
            if (rendaMensal.compareTo(rendaMinimaSemAnuidade) >= 0) {
                cartoes.add(createCartaoSemAnuidade());
            }
            if (rendaMensal.compareTo(rendaMinimaParceiros) >= 0) {
                cartoes.add(createCartaoParceiros());
            }
        }
        // Faixa alta: [5000, ∞)
        else if (rendaMensal.compareTo(rendaFaixaAltaMin) >= 0) {
            if (rendaMensal.compareTo(rendaMinimaSemAnuidade) >= 0) {
                cartoes.add(createCartaoSemAnuidade());
            }
            if (rendaMensal.compareTo(rendaMinimaParceiros) >= 0) {
                cartoes.add(createCartaoParceiros());
            }
            if (rendaMensal.compareTo(rendaMinimaCashback) >= 0) {
                cartoes.add(createCartaoCashback());
            }
        }

        return cartoes;
    }

    private Cartao createCartaoSemAnuidade() {
        return Cartao.builder()
                .tipoCartao(TipoCartao.CARTAO_SEM_ANUIDADE)
                .valorAnuidadeMensal(anuidadeSemAnuidade)
                .valorLimiteDisponivel(limiteSemAnuidade)
                .status(CartaoStatus.APROVADO)
                .build();
    }

    private Cartao createCartaoParceiros() {
        return Cartao.builder()
                .tipoCartao(TipoCartao.CARTAO_DE_PARCEIROS)
                .valorAnuidadeMensal(anuidadeParceiros)
                .valorLimiteDisponivel(limiteParceiros)
                .status(CartaoStatus.APROVADO)
                .build();
    }

    private Cartao createCartaoCashback() {
        return Cartao.builder()
                .tipoCartao(TipoCartao.CARTAO_COM_CASHBACK)
                .valorAnuidadeMensal(anuidadeCashback)
                .valorLimiteDisponivel(limiteCashback)
                .status(CartaoStatus.APROVADO)
                .build();
    }
}