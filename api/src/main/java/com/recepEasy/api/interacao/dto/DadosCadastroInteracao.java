package com.recepEasy.api.interacao.dto;

import jakarta.validation.constraints.NotEmpty;

public record DadosCadastroInteracao(

        String comentario,
        Boolean curtida
) {
}
