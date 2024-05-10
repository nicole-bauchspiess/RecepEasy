package com.recepEasy.api.receita.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroReceita(
        @NotBlank(message = "Nome da receita não pode ser nulo")
         String nome,

        @NotBlank(message = "A receita deve ter pelo menos dois ingredientes")
         String ingredientes,
        @NotBlank(message = "Modo de preparo nao pode ser nulo")
         String modoPreparo,
         @NotNull(message = "Descrição do sabor não pode ser nulo")
        String sabor,
        @NotNull
        boolean privado

         ) {
}
