package com.cartoes.api_cartoes.application.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorUtilTest {

    @Test
    @DisplayName("Deve calcular a idade corretamente")
    void deveCalcularIdadeCorretamente() {

        LocalDate dataNascimento = LocalDate.now().minusYears(30);

        int idade = ValidadorUtil.calcularIdade(dataNascimento);

        assertEquals(30, idade);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de nascimento for nula")
    void deveLancarExcecaoQuandoDataNascimentoForNula() {
        assertThrows(IllegalArgumentException.class, () -> ValidadorUtil.calcularIdade(null));
    }

    @ParameterizedTest
    @CsvSource({
            "20, true",  // Idade informada = idade calculada
            "21, false", // Idade informada != idade calculada
            "19, false"  // Idade informada != idade calculada
    })
    @DisplayName("Deve verificar se a idade é compatível com a data de nascimento")
    void deveVerificarIdadeCompativel(int idadeInformada, boolean resultado) {
        LocalDate dataNascimento = LocalDate.now().minusYears(20);

        assertEquals(resultado, ValidadorUtil.isIdadeCompativel(idadeInformada, dataNascimento));
    }

    @ParameterizedTest
    @CsvSource({
            "18, 18, true",  // Idade = idade mínima
            "19, 18, true",  // Idade > idade mínima
            "17, 18, false"  // Idade < idade mínima
    })
    @DisplayName("Deve verificar se a idade é maior ou igual à idade mínima")
    void deveVerificarIdadeMaiorOuIgual(int idade, int idadeMinima, boolean resultado) {
        assertEquals(resultado, ValidadorUtil.isIdadeMaiorOuIgualA(idade, idadeMinima));
    }
}