package com.recepEasy.api.usuario.dto;

import com.recepEasy.api.endereco.DadosEndereco;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DadosAlterarUsuario(
        @NotNull
        Long id,

        String nome,
        String email,
        String telefone,
        LocalDate nascimento,
        DadosEndereco endereco,
        String login

) {
}
