package com.cartoes.api_cartoes.domain.strategy.impl;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StrategyClienteSPJovemAdultoTest {

    private StrategyClienteSPJovemAdulto strategy;

    @Mock
    private StrategyPadrao strategyPadrao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategy = new StrategyClienteSPJovemAdulto(strategyPadrao);

        ReflectionTestUtils.setField(strategy, "idadeJovemAdultoMin", 25);
        ReflectionTestUtils.setField(strategy, "idadeJovemAdultoMax", 30);
        ReflectionTestUtils.setField(strategy, "ufSaoPaulo", "SP");
    }

    @ParameterizedTest
    @CsvSource({
            "SP, 25, true",   // Limite inferior (inclusivo)
            "SP, 27, true",   // Dentro da faixa
            "SP, 29, true",   // Próximo ao limite superior
            "SP, 30, false",  // Exatamente no limite superior (não incluso)
            "SP, 24, false",  // Abaixo do limite inferior
            "SP, 31, false",  // Acima do limite superior
            "RJ, 27, false"   // UF diferente de SP
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
    @DisplayName("Deve delegar a avaliação de cartões para StrategyPadrao")
    void deveDelegarAvaliacaoParaStrategyPadrao() {
        // Dado
        Cliente cliente = Cliente.builder()
                .uf("SP")
                .idade(27)
                .rendaMensal(new BigDecimal("4000.00"))
                .build();

        List<Cartao> cartoesEsperados = Arrays.asList(
                Cartao.builder().tipoCartao(TipoCartao.CARTAO_SEM_ANUIDADE).build(),
                Cartao.builder().tipoCartao(TipoCartao.CARTAO_DE_PARCEIROS).build()
        );

        when(strategyPadrao.avaliarCartoes(any(Cliente.class))).thenReturn(cartoesEsperados);

        // Quando
        List<Cartao> cartoes = strategy.avaliarCartoes(cliente);

        // Então
        assertSame(cartoesEsperados, cartoes);
        verify(strategyPadrao).avaliarCartoes(cliente);
    }

    @Test
    @DisplayName("Deve verificar o caso limite de 25 anos em SP")
    void deveSelecionarEstrategiaParaClienteCom25AnosEmSP() {
        // Dado
        Cliente cliente = Cliente.builder()
                .uf("SP")
                .idade(25)
                .rendaMensal(new BigDecimal("4000.00"))
                .build();

        // Quando
        boolean resultado = strategy.seAplica(cliente);

        // Então
        assertTrue(resultado);
    }
}