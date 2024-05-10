package com.recepEasy.api.usuario.dto;

import com.recepEasy.api.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record DadosCadastroUsuario(
        @NotBlank(message = "Nome não pode ser nulo")
        @Size(min = 6, message = "Nome deve ter pelo menos 6 caracteres")
        String nome,
        @NotBlank(message = "O CPF não pode ser nulo")
        @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}")
        String cpf,

        @NotBlank(message = "O email não pode ser nulo")
        @Email
        String email,

        @NotBlank(message = "O telefone não pode ser nulo")
        String telefone,

        @NotNull(message = "A data de nascimento não pode ser nula")
        LocalDate nascimento,

        @NotBlank
        String login,

        @NotBlank
        String senha,

        @NotNull
        @Valid
        DadosEndereco endereco
        ) {

}
