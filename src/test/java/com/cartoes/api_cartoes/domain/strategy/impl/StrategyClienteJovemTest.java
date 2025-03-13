package com.cartoes.api_cartoes.domain.strategy.impl;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.enums.CartaoStatus;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyClienteJovemTest {

    private StrategyClienteJovem strategy;

    @BeforeEach
    void setUp() {
        strategy = new StrategyClienteJovem();
        ReflectionTestUtils.setField(strategy, "idadeMinima", 18);
        ReflectionTestUtils.setField(strategy, "idadeJovemMaxima", 25);
        ReflectionTestUtils.setField(strategy, "rendaMinimaSemAnuidade", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(strategy, "limiteSemAnuidade", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(strategy, "anuidadeSemAnuidade", new BigDecimal("0.00"));
    }

    @ParameterizedTest
    @CsvSource({
            "18, true",   // Limite inferior - idade mínima
            "20, true",   // Dentro da faixa
            "24, true",   // Próximo ao limite superior
            "25, false",  // Exatamente no limite superior (não se aplica)
            "17, false",  // Abaixo da idade mínima
            "26, false"   // Acima da idade máxima
    })
    @DisplayName("Deve verificar se a estratégia se aplica baseado na idade")
    void deveVerificarSeEstrategiaSeAplica(int idade, boolean resultadoEsperado) {
        // Dado
        Cliente cliente = Cliente.builder()
                .idade(idade)
                .build();

        // Quando
        boolean resultado = strategy.seAplica(cliente);

        // Então
        assertEquals(resultadoEsperado, resultado);
    }

    @Test
    @DisplayName("Deve oferecer CARTAO_SEM_ANUIDADE para cliente com renda suficiente")
    void deveOferecerCartaoSemAnuidadeParaClienteComRendaSuficiente() {
        // Dado
        Cliente cliente = Cliente.builder()
                .idade(20)
                .rendaMensal(new BigDecimal("1500.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(1, cartoes.size());
        Cartao cartao = cartoes.getFirst();
        assertEquals(TipoCartao.CARTAO_SEM_ANUIDADE, cartao.getTipoCartao());
        assertEquals(new BigDecimal("0.00"), cartao.getValorAnuidadeMensal());
        assertEquals(new BigDecimal("1000.00"), cartao.getValorLimiteDisponivel());
        assertEquals(CartaoStatus.APROVADO, cartao.getStatus());
    }

    @Test
    @DisplayName("Não deve oferecer nenhum cartão para cliente com renda insuficiente")
    void naoDeveOferecerCartaoParaClienteComRendaInsuficiente() {
        // Dado
        Cliente cliente = Cliente.builder()
                .idade(20)
                .rendaMensal(new BigDecimal("500.00")) // Abaixo da renda mínima
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertTrue(cartoes.isEmpty());
    }
}