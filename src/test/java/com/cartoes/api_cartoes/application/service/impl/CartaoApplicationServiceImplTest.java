package com.cartoes.api_cartoes.application.service.impl;

import com.cartoes.api_cartoes.application.dto.ClienteDTO;
import com.cartoes.api_cartoes.application.dto.request.ClienteRequest;
import com.cartoes.api_cartoes.application.dto.response.CartaoResponse;
import com.cartoes.api_cartoes.application.dto.response.SolicitacaoResponse;
import com.cartoes.api_cartoes.application.dto.validator.ClienteValidator;
import com.cartoes.api_cartoes.application.factory.CartaoFactory;
import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.enums.CartaoStatus;
import com.cartoes.api_cartoes.domain.enums.TipoCartao;
import com.cartoes.api_cartoes.domain.exception.BusinessException;
import com.cartoes.api_cartoes.domain.repository.ClienteRepository;
import com.cartoes.api_cartoes.domain.service.AvaliacaoCartaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartaoApplicationServiceImplTest {

    @Mock
    private AvaliacaoCartaoService avaliacaoCartaoService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CartaoFactory cartaoFactory;

    @Mock
    private ClienteValidator clienteValidator;

    @InjectMocks
    private CartaoApplicationServiceImpl cartaoApplicationService;

    private ClienteDTO clienteDTO;
    private ClienteRequest clienteRequest;
    private Cliente cliente;
    private UUID idSolicitacao;
    private List<Cartao> cartoesDominio;
    private List<CartaoResponse> cartoesResponse;

    @BeforeEach
    void setUp() {
        // Configurar dados de teste
        clienteDTO = criarClienteDTO();
        clienteRequest = new ClienteRequest();
        clienteRequest.setCliente(clienteDTO);

        cliente = criarCliente();

        idSolicitacao = UUID.randomUUID();

        cartoesDominio = criarCartoesDominio();
        cartoesResponse = criarCartoesResponse();
    }

    @Test
    @DisplayName("Deve processar solicitação com sucesso e retornar cartões")
    void deveProcessarSolicitacaoComSucessoERetornarCartoes() {
        // Configurar mocks
        doNothing().when(clienteValidator).validar(any(ClienteRequest.class));
        doNothing().when(avaliacaoCartaoService).validarCliente(any(Cliente.class));
        when(clienteRepository.registrarCliente(any(Cliente.class))).thenReturn(idSolicitacao);
        when(avaliacaoCartaoService.avaliarCartoesDisponiveis(any(Cliente.class))).thenReturn(cartoesDominio);
        when(cartaoFactory.toCartaoResponseList(cartoesDominio)).thenReturn(cartoesResponse);

        // Executar
        SolicitacaoResponse response = cartaoApplicationService.processarSolicitacao(clienteRequest);

        // Verificar
        assertNotNull(response);
        assertEquals(idSolicitacao.toString(), response.getNumeroSolicitacao());
        assertNotNull(response.getDataSolicitacao());
        assertEquals(clienteDTO, response.getCliente());
        assertEquals(cartoesResponse, response.getCartoesOfertados());

        // Verificar chamadas aos mocks
        verify(clienteValidator).validar(clienteRequest);
        verify(avaliacaoCartaoService).validarCliente(any(Cliente.class));
        verify(clienteRepository).registrarCliente(any(Cliente.class));
        verify(avaliacaoCartaoService).avaliarCartoesDisponiveis(any(Cliente.class));
        verify(cartaoFactory).toCartaoResponseList(cartoesDominio);
    }

    @Test
    @DisplayName("Deve propagar exceção de validação do cliente")
    void devePropagaExcecaoDeValidacaoDoCliente() {
        // Configurar mocks
        doThrow(new BusinessException("Dados do cliente inválidos"))
                .when(clienteValidator).validar(any(ClienteRequest.class));

        // Executar/Verificar
        BusinessException exception = assertThrows(BusinessException.class, () -> cartaoApplicationService.processarSolicitacao(clienteRequest));

        assertEquals("Dados do cliente inválidos", exception.getMessage());

        // Verificar que não chamou os outros serviços
        verify(avaliacaoCartaoService, never()).validarCliente(any(Cliente.class));
        verify(clienteRepository, never()).registrarCliente(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve processar solicitação e retornar lista vazia quando não houver cartões disponíveis")
    void deveProcessarSolicitacaoERetornarListaVaziaQuandoNaoHouverCartoes() {
        // Configurar mocks
        doNothing().when(clienteValidator).validar(any(ClienteRequest.class));
        doNothing().when(avaliacaoCartaoService).validarCliente(any(Cliente.class));
        when(clienteRepository.registrarCliente(any(Cliente.class))).thenReturn(idSolicitacao);
        when(avaliacaoCartaoService.avaliarCartoesDisponiveis(any(Cliente.class))).thenReturn(Collections.emptyList());
        when(cartaoFactory.toCartaoResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Executar
        SolicitacaoResponse response = cartaoApplicationService.processarSolicitacao(clienteRequest);

        // Verificar
        assertNotNull(response);
        assertTrue(response.getCartoesOfertados().isEmpty());
    }

    private ClienteDTO criarClienteDTO() {
        return ClienteDTO.builder()
                .nome("Cliente Teste")
                .cpf("123.456.789-10")
                .idade(25)
                .dataNascimento(LocalDate.of(2000, 1, 1))
                .uf("SP")
                .rendaMensal(new BigDecimal("4000.00"))
                .email("cliente@teste.com")
                .telefoneWhatsapp("11999992020")
                .build();
    }

    private Cliente criarCliente() {
        return Cliente.builder()
                .nome("Cliente Teste")
                .cpf("123.456.789-10")
                .idade(25)
                .dataNascimento(LocalDate.of(2000, 1, 1))
                .uf("SP")
                .rendaMensal(new BigDecimal("4000.00"))
                .email("cliente@teste.com")
                .telefoneWhatsapp("11999992020")
                .build();
    }

    private List<Cartao> criarCartoesDominio() {
        return Arrays.asList(
                Cartao.builder()
                        .tipoCartao(TipoCartao.CARTAO_SEM_ANUIDADE)
                        .valorAnuidadeMensal(BigDecimal.ZERO)
                        .valorLimiteDisponivel(new BigDecimal("1000.00"))
                        .status(CartaoStatus.APROVADO)
                        .build(),

                Cartao.builder()
                        .tipoCartao(TipoCartao.CARTAO_DE_PARCEIROS)
                        .valorAnuidadeMensal(new BigDecimal("20.00"))
                        .valorLimiteDisponivel(new BigDecimal("3000.00"))
                        .status(CartaoStatus.APROVADO)
                        .build()
        );
    }

    private List<CartaoResponse> criarCartoesResponse() {
        return Arrays.asList(
                CartaoResponse.builder()
                        .tipoCartao(TipoCartao.CARTAO_SEM_ANUIDADE)
                        .valorAnuidadeMensal(BigDecimal.ZERO)
                        .valorLimiteDisponivel(new BigDecimal("1000.00"))
                        .status(CartaoStatus.APROVADO)
                        .build(),

                CartaoResponse.builder()
                        .tipoCartao(TipoCartao.CARTAO_DE_PARCEIROS)
                        .valorAnuidadeMensal(new BigDecimal("20.00"))
                        .valorLimiteDisponivel(new BigDecimal("3000.00"))
                        .status(CartaoStatus.APROVADO)
                        .build()
        );
    }
}