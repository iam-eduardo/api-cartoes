package com.cartoes.api_cartoes.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa um cliente no domínio de negócio.
 * Esta entidade contém todos os dados necessários para analisar
 * a elegibilidade para diferentes tipos de cartões.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private String nome;
    private String cpf;
    private Integer idade;
    private LocalDate dataNascimento;
    private String uf;
    private BigDecimal rendaMensal;
    private String email;
    private String telefoneWhatsapp;
}