package com.cartoes.api_cartoes.domain.service.impl;

import com.cartoes.api_cartoes.application.util.ValidadorUtil;
import com.cartoes.api_cartoes.domain.entity.Cartao;
import com.cartoes.api_cartoes.domain.entity.Cliente;
import com.cartoes.api_cartoes.domain.exception.BusinessException;
import com.cartoes.api_cartoes.domain.service.AvaliacaoCartaoService;
import com.cartoes.api_cartoes.domain.strategy.StrategyAvaliacaoCartao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliacaoCartaoServiceImpl implements AvaliacaoCartaoService {

    private final List<StrategyAvaliacaoCartao> estrategias;

    @Value("${aplicacao.cliente.idade-minima}")
    private int idadeMinima;

    @Override
    public List<Cartao> avaliarCartoesDisponiveis(Cliente cliente) {
        log.debug("Determinando cartões elegíveis para cliente: {}", cliente.getCpf());

        // Encontrar a primeira estratégia aplicável
        return estrategias.stream()
                .filter(estrategia -> estrategia.seAplica(cliente))
                .findFirst()
                .map(estrategia -> estrategia.avaliarCartoes(cliente))
                .orElseThrow(() -> new BusinessException("Não foi possível avaliar os cartões elegíveis"));
    }

    @Override
    public void validarCliente(Cliente cliente) {
        log.debug("Validando cliente: {}", cliente.getCpf());

        // Validar idade mínima
        int idadeCalculada = calcularIdade(cliente.getDataNascimento());

        if (idadeCalculada < idadeMinima) {
            log.warn("Cliente com idade inferior a {} anos: {}", idadeMinima, idadeCalculada);
            throw new BusinessException("Cliente deve ter pelo menos " + idadeMinima + " anos");
        }

        // Verificar consistência na idade
        if (cliente.getIdade() != idadeCalculada) {
            log.warn("Inconsistência na idade do cliente. Informada: {}, Calculada: {}", cliente.getIdade(), idadeCalculada);
            throw new BusinessException("Idade informada não corresponde à data de nascimento");
        }
    }

    private int calcularIdade(LocalDate dataNascimento) {
        return ValidadorUtil.calcularIdade(dataNascimento);
    }
}