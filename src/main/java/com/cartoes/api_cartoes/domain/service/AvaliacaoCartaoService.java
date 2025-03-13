package com.cartoes.api_cartoes.domain.service;

import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;

import java.util.List;

public interface AvaliacaoCartaoService {
    /**
     * Avalia quais cartões podem ser oferecidos ao cliente
     *
     * @param cliente Cliente a ser avaliado
     * @return Lista de cartões elegíveis
     */
    List<Cartao> avaliarCartoesDisponiveis(Cliente cliente);

    /**
     * Valida os dados do cliente
     *
     * @param cliente Cliente a ser validado
     * @throws com.cartoes.api_cartoes.domain.exception.BusinessException se os dados forem inválidos
     */
    void validarCliente(Cliente cliente);
}
