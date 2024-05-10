package com.recepEasy.api.receita.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroReceitaGPT(
        @NotBlank String ingredientes,
         boolean privado
) {
}
