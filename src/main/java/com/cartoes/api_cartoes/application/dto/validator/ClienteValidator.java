package com.cartoes.api_cartoes.application.dto.validator;

import com.cartoes.api_cartoes.application.dto.ClienteDTO;
import com.cartoes.api_cartoes.application.dto.request.ClienteRequest;
import com.cartoes.api_cartoes.domain.exception.BusinessException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

@Component
public class ClienteValidator {

    private final Validator validator;

    public ClienteValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void validar(ClienteRequest clienteRequest) {
        if (clienteRequest == null || clienteRequest.getCliente() == null) {
            throw new BusinessException("Dados do cliente não podem ser nulos");
        }

        ClienteDTO dto = clienteRequest.getCliente();

        // Validar campos obrigatórios
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new BusinessException("Nome do cliente é obrigatório");
        }

        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new BusinessException("CPF do cliente é obrigatório");
        }

        if (dto.getIdade() == null) {
            throw new BusinessException("Idade do cliente é obrigatória");
        }

        if (dto.getDataNascimento() == null) {
            throw new BusinessException("Data de nascimento é obrigatória");
        }

        if (dto.getUf() == null || dto.getUf().isBlank()) {
            throw new BusinessException("UF do cliente é obrigatória");
        }

        if (dto.getRendaMensal() == null) {
            throw new BusinessException("Renda mensal do cliente é obrigatória");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BusinessException("Email do cliente é obrigatório");
        }

        if (dto.getTelefoneWhatsapp() == null || dto.getTelefoneWhatsapp().isBlank()) {
            throw new BusinessException("Telefone/WhatsApp do cliente é obrigatório");
        }
    }
}