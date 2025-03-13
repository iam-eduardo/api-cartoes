package com.cartoes.api_cartoes.application.service.impl;

import com.cartoes.api_cartoes.application.dto.ClienteDTO;
import com.cartoes.api_cartoes.application.dto.request.ClienteRequest;
import com.cartoes.api_cartoes.application.dto.response.CartaoResponse;
import com.cartoes.api_cartoes.application.dto.response.SolicitacaoResponse;
import com.cartoes.api_cartoes.application.dto.validator.ClienteValidator;
import com.cartoes.api_cartoes.application.factory.CartaoFactory;
import com.cartoes.api_cartoes.application.service.CartaoApplicationService;
import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.repository.ClienteRepository;
import com.cartoes.api_cartoes.domain.service.AvaliacaoCartaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartaoApplicationServiceImpl implements CartaoApplicationService {

    private final AvaliacaoCartaoService avaliacaoCartaoService;
    private final ClienteRepository clienteRepository;
    private final CartaoFactory cartaoFactory;
    private final ClienteValidator clienteValidator;

    @Override
    public SolicitacaoResponse processarSolicitacao(ClienteRequest clienteRequest) {
        log.info("Iniciando processamento de solicitação de cartão");
        long startTime = System.currentTimeMillis();

        // Validar dados do DTO
        clienteValidator.validar(clienteRequest);

        // Converter ClienteDTO para entidade Cliente
        Cliente cliente = converterParaEntidade(clienteRequest.getCliente());

        // Validações de negócio no domínio
        avaliacaoCartaoService.validarCliente(cliente);

        // Registrar cliente na API externa
        UUID idSolicitacao = clienteRepository.registrarCliente(cliente);
        log.info("Cliente registrado com ID: {}", idSolicitacao);

        // Identificar cartões elegíveis usando a estratégia adequada
        List<Cartao> cartoesElegiveis = avaliacaoCartaoService.avaliarCartoesDisponiveis(cliente);
        List<CartaoResponse> cartoesOfertados = cartaoFactory.toCartaoResponseList(cartoesElegiveis);

        // Preparar resposta
        SolicitacaoResponse response = SolicitacaoResponse.builder()
                .numeroSolicitacao(idSolicitacao.toString())
                .dataSolicitacao(LocalDateTime.now())
                .cliente(clienteRequest.getCliente())
                .cartoesOfertados(cartoesOfertados)
                .build();

        long endTime = System.currentTimeMillis();
        log.info("Processamento finalizado em {} ms", endTime - startTime);

        return response;
    }

    private Cliente converterParaEntidade(ClienteDTO dto) {
        return Cliente.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .idade(dto.getIdade())
                .dataNascimento(dto.getDataNascimento())
                .uf(dto.getUf())
                .rendaMensal(dto.getRendaMensal())
                .email(dto.getEmail())
                .telefoneWhatsapp(dto.getTelefoneWhatsapp())
                .build();
    }
}