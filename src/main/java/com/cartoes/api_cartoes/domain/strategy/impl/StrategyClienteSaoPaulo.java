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
public class StrategyClienteSaoPaulo implements StrategyAvaliacaoCartao {

    @Value("${aplicacao.cliente.sp-jovem-adulto-idade-min:25}")
    private int idadeJovemAdultoMin;

    @Value("${aplicacao.cliente.sp-jovem-adulto-idade-max:30}")
    private int idadeJovemAdultoMax;

    @Value("${aplicacao.estados.sao-paulo:SP}")
    private String ufSaoPaulo;

    @Value("${aplicacao.cartoes.sem-anuidade.renda-minima:3500.00}")
    private BigDecimal rendaMinimaSemAnuidade;

    @Value("${aplicacao.cartoes.cashback.renda-minima:7500.00}")
    private BigDecimal rendaMinimaCashback;

    @Value("${aplicacao.cartoes.sem-anuidade.limite:1000.00}")
    private BigDecimal limiteSemAnuidade;

    @Value("${aplicacao.cartoes.sem-anuidade.anuidade:0.00}")
    private BigDecimal anuidadeSemAnuidade;

    @Value("${aplicacao.cartoes.cashback.limite:5000.00}")
    private BigDecimal limiteCashback;

    @Value("${aplicacao.cartoes.cashback.anuidade:15.00}")
    private BigDecimal anuidadeCashback;

    @Override
    public boolean seAplica(Cliente cliente) {
        if (!ufSaoPaulo.equalsIgnoreCase(cliente.getUf())) {
            return false;
        }
        boolean isJovemAdulto = cliente.getIdade() >= idadeJovemAdultoMin &&
                cliente.getIdade() < idadeJovemAdultoMax;

        return !isJovemAdulto;
    }

    @Override
    public List<Cartao> avaliarCartoes(Cliente cliente) {
        List<Cartao> cartoes = new ArrayList<>();

        if (cliente.getRendaMensal().compareTo(rendaMinimaSemAnuidade) >= 0) {
            cartoes.add(Cartao.builder()
                    .tipoCartao(TipoCartao.CARTAO_SEM_ANUIDADE)
                    .valorAnuidadeMensal(anuidadeSemAnuidade)
                    .valorLimiteDisponivel(limiteSemAnuidade)
                    .status(CartaoStatus.APROVADO)
                    .build());
        }

        if (cliente.getRendaMensal().compareTo(rendaMinimaCashback) >= 0) {
            cartoes.add(Cartao.builder()
                    .tipoCartao(TipoCartao.CARTAO_COM_CASHBACK)
                    .valorAnuidadeMensal(anuidadeCashback)
                    .valorLimiteDisponivel(limiteCashback)
                    .status(CartaoStatus.APROVADO)
                    .build());
        }

        return cartoes;
    }
}