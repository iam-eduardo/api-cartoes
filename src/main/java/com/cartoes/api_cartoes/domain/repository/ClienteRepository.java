package com.cartoes.api_cartoes.domain.repository;

import com.cartoes.api_cartoes.domain.entity.Cliente;

import java.util.UUID;

public interface ClienteRepository {
    /**
     * Registra o cliente em sistema externo
     *
     * @param cliente Dados do cliente a ser registrado
     * @return ID único gerado para o cliente
     */
    UUID registrarCliente(Cliente cliente);
}
