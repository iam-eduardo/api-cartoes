package com.cartoes.api_cartoes.domain.strategy.impl;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
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

class StrategyClienteSaoPauloTest {

    private StrategyClienteSaoPaulo strategy;

    @BeforeEach
    void setUp() {
        strategy = new StrategyClienteSaoPaulo();
        ReflectionTestUtils.setField(strategy, "idadeJovemAdultoMin", 25);
        ReflectionTestUtils.setField(strategy, "idadeJovemAdultoMax", 30);
        ReflectionTestUtils.setField(strategy, "ufSaoPaulo", "SP");
        ReflectionTestUtils.setField(strategy, "rendaMinimaSemAnuidade", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(strategy, "rendaMinimaCashback", new BigDecimal("5000.00"));
        ReflectionTestUtils.setField(strategy, "limiteSemAnuidade", new BigDecimal("1000.00"));
        ReflectionTestUtils.setField(strategy, "anuidadeSemAnuidade", new BigDecimal("0.00"));
        ReflectionTestUtils.setField(strategy, "limiteCashback", new BigDecimal("5000.00"));
        ReflectionTestUtils.setField(strategy, "anuidadeCashback", new BigDecimal("15.00"));
    }

    @ParameterizedTest
    @CsvSource({
            "SP, 20, true",  // Cliente de SP fora da faixa jovem adulto
            "SP, 31, true",  // Cliente de SP acima da faixa jovem adulto
            "SP, 25, false", // Cliente de SP exatamente no limite inferior jovem adulto
            "SP, 29, false", // Cliente de SP dentro da faixa jovem adulto
            "RJ, 20, false", // Cliente de outro estado
            "RJ, 27, false"  // Cliente de outro estado
    })
    @DisplayName("Deve verificar se a estratégia se aplica baseado na UF e idade")
    void deveVerificarSeEstrategiaSeAplica(String uf, int idade, boolean resultadoEsperado) {
        // Dado
        Cliente cliente = Cliente.builder()
                .uf(uf)
                .idade(idade)
                .build();

        // Quando
        boolean resultado = strategy.seAplica(cliente);

        // Então
        assertEquals(resultadoEsperado, resultado);
    }

    @Test
    @DisplayName("Deve oferecer CARTAO_SEM_ANUIDADE para cliente de SP com renda suficiente")
    void deveOferecerCartaoSemAnuidadeParaClienteDeSPComRendaSuficiente() {
        // Dado
        Cliente cliente = Cliente.builder()
                .uf("SP")
                .idade(20)
                .rendaMensal(new BigDecimal("2000.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(1, cartoes.size());
        Cartao cartao = cartoes.getFirst();
        assertEquals(TipoCartao.CARTAO_SEM_ANUIDADE, cartao.getTipoCartao());
    }

    @Test
    @DisplayName("Deve oferecer CARTAO_SEM_ANUIDADE e CARTAO_COM_CASHBACK para cliente de SP com renda alta")
    void deveOferecerDoisCartoesParaClienteDeSPComRendaAlta() {
        // Dado
        Cliente cliente = Cliente.builder()
                .uf("SP")
                .idade(20)
                .rendaMensal(new BigDecimal("6000.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertEquals(2, cartoes.size());
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_SEM_ANUIDADE));
        assertTrue(cartoes.stream().anyMatch(c -> c.getTipoCartao() == TipoCartao.CARTAO_COM_CASHBACK));
    }

    @Test
    @DisplayName("Não deve oferecer cartões para cliente de SP com renda insuficiente")
    void naoDeveOferecerCartoesParaClienteDeSPComRendaInsuficiente() {
        // Dado
        Cliente cliente = Cliente.builder()
                .uf("SP")
                .idade(20)
                .rendaMensal(new BigDecimal("500.00"))
                .build();

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertTrue(cartoes.isEmpty());
    }
}