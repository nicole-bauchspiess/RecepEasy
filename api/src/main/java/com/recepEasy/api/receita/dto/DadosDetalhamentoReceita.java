package com.recepEasy.api.receita.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.recepEasy.api.receita.Receita;

import java.time.LocalDateTime;

public record DadosDetalhamentoReceita(
        Long id,
        Long usuario_id,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime horaPublicacao,
        String nome,
        String ingredientes,
        String modoPreparo,
        String sabor
) {
    public DadosDetalhamentoReceita(Receita r){
        this(r.getId(), r.getUsuario().getId(), r.getHoraPublicacao(), r.getNome(), r.getIngredientes(), r.getModoPreparo(), r.getSabor());
    }
}
