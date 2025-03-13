package com.cartoes.api_cartoes.domain.service.impl;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import com.cartoes.api_cartoes.domain.exception.BusinessException;
import com.cartoes.api_cartoes.domain.strategy.StrategyAvaliacaoCartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AvaliacaoCartaoServiceImplTest {

    private AvaliacaoCartaoServiceImpl avaliacaoCartaoService;

    @Mock
    private StrategyAvaliacaoCartao strategy1;

    @Mock
    private StrategyAvaliacaoCartao strategy2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        List<StrategyAvaliacaoCartao> estrategias = Arrays.asList(strategy1, strategy2);
        avaliacaoCartaoService = new AvaliacaoCartaoServiceImpl(estrategias);
        ReflectionTestUtils.setField(avaliacaoCartaoService, "idadeMinima", 18);
    }

    @Test
    @DisplayName("Deve escolher a primeira estratégia aplicável")
    void deveEscolherPrimeiraEstrategiaAplicavel() {
        // Dado
        Cliente cliente = criarClienteValido();
        List<Cartao> cartoesEsperados = Collections.singletonList(
                Cartao.builder().tipoCartao(TipoCartao.CARTAO_SEM_ANUIDADE).build()
        );

        when(strategy1.seAplica(cliente)).thenReturn(false);
        when(strategy2.seAplica(cliente)).thenReturn(true);
        when(strategy2.avaliarCartoes(cliente)).thenReturn(cartoesEsperados);

        // Quando
        List<Cartao> cartoes = avaliacaoCartaoService.avaliarCartoesDisponiveis(cliente);

        // Então
        assertSame(cartoesEsperados, cartoes);
        verify(strategy1).seAplica(cliente);
        verify(strategy2).seAplica(cliente);
        verify(strategy2).avaliarCartoes(cliente);
        verify(strategy1, never()).avaliarCartoes(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhuma estratégia se aplica")
    void deveLancarExcecaoQuandoNenhumaEstrategiaSeAplica() {
        // Dado
        Cliente cliente = criarClienteValido();

        when(strategy1.seAplica(cliente)).thenReturn(false);
        when(strategy2.seAplica(cliente)).thenReturn(false);

        // Quando/Então
        BusinessException exception = assertThrows(BusinessException.class, () -> avaliacaoCartaoService.avaliarCartoesDisponiveis(cliente));

        assertEquals("Não foi possível avaliar os cartões elegíveis", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar cliente com sucesso")
    void deveValidarClienteComSucesso() {
        // Dado
        Cliente cliente = criarClienteValido();

        // Quando/Então
        assertDoesNotThrow(() -> avaliacaoCartaoService.validarCliente(cliente));
    }

    @Test
    @DisplayName("Deve lançar exceção para cliente com idade menor que a mínima")
    void deveLancarExcecaoParaClienteComIdadeMenorQueMinima() {
        // Dado
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .cpf("123.456.789-10")
                .idade(17)
                .dataNascimento(LocalDate.now().minusYears(17))
                .build();

        // Quando/Então
        BusinessException exception = assertThrows(BusinessException.class, () -> avaliacaoCartaoService.validarCliente(cliente));

        assertTrue(exception.getMessage().contains("Cliente deve ter pelo menos 18 anos"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando idade informada não corresponde à data de nascimento")
    void deveLancarExcecaoParaIdadeIncompativel() {
        // Dado
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .cpf("123.456.789-10")
                .idade(25)
                .dataNascimento(LocalDate.now().minusYears(30))
                .build();

        // Quando/Então
        BusinessException exception = assertThrows(BusinessException.class, () -> avaliacaoCartaoService.validarCliente(cliente));

        assertTrue(exception.getMessage().contains("Idade informada não corresponde à data de nascimento"));
    }

    private Cliente criarClienteValido() {
        return Cliente.builder()
                .nome("Cliente Teste")
                .cpf("123.456.789-10")
                .idade(25)
                .dataNascimento(LocalDate.now().minusYears(25))
                .uf("SP")
                .rendaMensal(new BigDecimal("4000.00"))
                .email("cliente@teste.com")
                .telefoneWhatsapp("11999992020")
                .build();
    }
}