package com.recepEasy.api.receita.dto;

import jakarta.validation.constraints.NotNull;

public record DadosAlterarReceita(
        @NotNull
        Long id,
        String nome,
        String ingredientes,
        String modoPreparo,
        Boolean privado
) {
}
