package com.cartoes.api_cartoes.presentation.controller;

import com.cartoes.api_cartoes.application.dto.request.ClienteRequest;
import com.cartoes.api_cartoes.application.dto.response.SolicitacaoResponse;
import com.cartoes.api_cartoes.application.service.CartaoApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
@Tag(name = "Cartão Controller", description = "API para gerenciamento de solicitações de cartões de crédito")
public class CartaoController {

    private final CartaoApplicationService cartaoApplicationService;

    @PostMapping
    @Operation(summary = "Solicitar cartão de crédito", description = "Recebe dados do cliente e retorna cartões disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação processada com sucesso e pelo menos um cartão aprovado",
                    content = @Content(schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "204", description = "Solicitação processada com sucesso, mas nenhum cartão aprovado"),
            @ApiResponse(responseCode = "400", description = "Dados do cliente inválidos"),
            @ApiResponse(responseCode = "422", description = "Solicitação não atende aos critérios de negócio"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<SolicitacaoResponse> solicitarCartao(@Valid @RequestBody ClienteRequest clienteRequest) {
        log.info("Recebida solicitação de cartão");

        SolicitacaoResponse response = cartaoApplicationService.processarSolicitacao(clienteRequest);

        if (response.getCartoesOfertados() == null || response.getCartoesOfertados().isEmpty()) {
            log.info("Nenhum cartão aprovado para o cliente");
            return ResponseEntity.noContent().build();
        }

        log.info("Solicitação processada com sucesso. Cartões aprovados: {}", response.getCartoesOfertados().size());
        return ResponseEntity.ok(response);
    }
}