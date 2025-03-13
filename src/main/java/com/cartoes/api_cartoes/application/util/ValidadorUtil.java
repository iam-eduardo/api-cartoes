package com.cartoes.api_cartoes.application.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidadorUtil {

    /**
     * Calcula a idade a partir da data de nascimento
     *
     * @param dataNascimento Data de nascimento
     * @return Idade em anos
     */
    public static int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula");
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    /**
     * Verifica se a idade informada é compatível com a data de nascimento
     *
     * @param idade          Idade informada
     * @param dataNascimento Data de nascimento
     * @return true se a idade for compatível
     */
    public static boolean isIdadeCompativel(int idade, LocalDate dataNascimento) {
        int idadeCalculada = calcularIdade(dataNascimento);
        return idade == idadeCalculada;
    }

    /**
     * Verifica se a idade é maior ou igual à idade mínima
     *
     * @param idade       Idade a ser verificada
     * @param idadeMinima Idade mínima permitida
     * @return true se a idade for maior ou igual à idade mínima
     */
    public static boolean isIdadeMaiorOuIgualA(int idade, int idadeMinima) {
        return idade >= idadeMinima;
    }
}
