package com.cartoes.api_cartoes.application.service;

import com.cartoes.api_cartoes.application.dto.request.ClienteRequest;
import com.cartoes.api_cartoes.application.dto.response.SolicitacaoResponse;

public interface CartaoApplicationService {
    /**
     * Processa a solicitação de cartão de crédito
     *
     * @param clienteRequest Dados do cliente solicitante
     * @return Resposta com os cartões ofertados ou vazio se não houver cartões disponíveis
     */
    SolicitacaoResponse processarSolicitacao(ClienteRequest clienteRequest);
}
