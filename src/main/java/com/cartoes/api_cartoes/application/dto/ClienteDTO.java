package com.cartoes.api_cartoes.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String nome;
    private String cpf;
    private Integer idade;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("data_nascimento")
    private LocalDate dataNascimento;

    private String uf;

    @JsonProperty("renda_mensal")
    private BigDecimal rendaMensal;

    private String email;

    @JsonProperty("telefone_whatsapp")
    private String telefoneWhatsapp;
}