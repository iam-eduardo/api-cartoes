package com.cartoes.api_cartoes.infrastructure.config;


import com.cartoes.api_cartoes.domain.strategy.StrategyAvaliacaoCartao;
import com.cartoes.api_cartoes.domain.strategy.impl.StrategyClienteJovem;
import com.cartoes.api_cartoes.domain.strategy.impl.StrategyClienteSPJovemAdulto;
import com.cartoes.api_cartoes.domain.strategy.impl.StrategyClienteSaoPaulo;
import com.cartoes.api_cartoes.domain.strategy.impl.StrategyPadrao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StrategyConfig {

    /**
     * Define a ordem de prioridade das estratégias de avaliação de cartões.
     * A primeira estratégia na lista que se aplicar será usada.
     * A estratégia padrão deve ser sempre a última.
     */
    @Bean
    public List<StrategyAvaliacaoCartao> estrategiasAvaliacaoCartao(
            StrategyClienteJovem estrategiaClienteJovem,
            StrategyClienteSPJovemAdulto estrategiaClienteSPJovemAdulto,
            StrategyClienteSaoPaulo estrategiaClienteSaoPaulo,
            StrategyPadrao estrategiaPadrao) {

        return List.of(
                estrategiaClienteJovem,          // Primeira prioridade: clientes jovens (18-25 anos)
                estrategiaClienteSPJovemAdulto,  // Segunda prioridade: clientes SP entre 25-30 anos
                estrategiaClienteSaoPaulo,       // Terceira prioridade: outros clientes de SP
                estrategiaPadrao                 // Última prioridade: regra padrão para os demais casos
        );
    }
}