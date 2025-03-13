package com.cartoes.api_cartoes.domain.strategy.impl;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyPadraoTest {

    private StrategyPadrao strategy;

    @BeforeEach
    void setUp() {
        strategy = new StrategyPadrao();

        // Faixas de renda
        ReflectionTestUtils.setField(strategy, "rendaFaixaBaixaMin", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(strategy, "rendaFaixaBaixaMax", new BigDecimal("3000.00"));
        ReflectionTestUtils.setField(strategy, "rendaFaixaMediaMin", new BigDecimal("3000.00"));
        ReflectionTestUtils.setField(strategy, "rendaFaixaMediaMax", new BigDecimal("5000.00"));
        ReflectionTestUtils.setField(strategy, "rendaFaixaAltaMin", new BigDecimal("5000.00"));

        // Valores mínimos de renda para cada cartão
        ReflectionTestUtils.setField(strategy, "rendaMinimaSemAnuidade", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(strategy, "rendaMinimaParceiros", new BigDecimal("3000.00"));
        ReflectionTestUtils.setField(strategy, "rendaMinimaCashback", new BigDecimal("5000.00"));

        // Valores dos cartões
        ReflectionTestUtils.setField(strategy, "limiteSemAnuidade", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(strategy, "anuidadeSemAnuidade", new BigDecimal("0.00"));
        ReflectionTestUtils.setField(strategy, "limiteParceiros", new BigDecimal("3000.00"));
        ReflectionTestUtils.setField(strategy, "anuidadeParceiros", new BigDecimal("20.00"));
        ReflectionTestUtils.setField(strategy, "limiteCashback", new BigDecimal("5000.00"));
        ReflectionTestUtils.setField(strategy, "anuidadeCashback", new BigDecimal("15.00"));
    }

    @Test
    @DisplayName("Estratégia padrão deve sempre se aplicar")
    void deveVerificarSeEstrategiaSempreSeAplica() {
        // Dado
        Cliente cliente = Cliente.builder().build();

        // Quando
        boolean resultado = strategy.seAplica(cliente);

        // Então
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve oferecer apenas CARTAO_SEM_ANUIDADE para cliente com renda na faixa baixa")
    void deveOferecerApenasCartaoSemAnuidadeParaClienteComRendaBaixa() {
        // Dado
        Cliente cliente = Cliente.builder()
                .rendaMensal(new BigDecimal("2000.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(1, cartoes.size());
        assertEquals(TipoCartao.CARTAO_SEM_ANUIDADE, cartoes.getFirst().getTipoCartao());
    }

    @Test
    @DisplayName("Deve oferecer CARTAO_SEM_ANUIDADE e CARTAO_DE_PARCEIROS para cliente com renda na faixa média")
    void deveOferecerDoisCartoesParaClienteComRendaMedia() {
        // Dado
        Cliente cliente = Cliente.builder()
                .rendaMensal(new BigDecimal("4000.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(2, cartoes.size());
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_SEM_ANUIDADE));
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_DE_PARCEIROS));
    }

    @Test
    @DisplayName("Deve oferecer todos os cartões para cliente com renda na faixa alta")
    void deveOferecerTodosCartoesParaClienteComRendaAlta() {
        // Dado
        Cliente cliente = Cliente.builder()
                .rendaMensal(new BigDecimal("6000.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(3, cartoes.size());
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_SEM_ANUIDADE));
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_DE_PARCEIROS));
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_COM_CASHBACK));
    }

    @Test
    @DisplayName("Não deve oferecer cartões para cliente com renda abaixo da faixa baixa")
    void naoDeveOferecerCartoesParaClienteComRendaInsuficiente() {
        // Dado
        Cliente cliente = Cliente.builder()
                .rendaMensal(new BigDecimal("500.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertTrue(cartoes.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar o caso limite inferior da faixa média")
    void deveVerificarCasoLimiteInferiorFaixaMedia() {
        // Dado
        Cliente cliente = Cliente.builder()
                .rendaMensal(new BigDecimal("3000.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(2, cartoes.size());
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_SEM_ANUIDADE));
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_DE_PARCEIROS));
    }

    @Test
    @DisplayName("Deve verificar o caso limite inferior da faixa alta")
    void deveVerificarCasoLimiteInferiorFaixaAlta() {
        // Dado
        Cliente cliente = Cliente.builder()
                .rendaMensal(new BigDecimal("5000.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(3, cartoes.size());
    }
}