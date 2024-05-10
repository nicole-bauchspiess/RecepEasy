package com.recepEasy.api.interacao.dto;

import com.recepEasy.api.interacao.Interacao;

public record DadosListagemInteracao (

        Long id,
        Long receita_id,
        Long usuario_id,
        String comentario,
        boolean curtida

){
    public DadosListagemInteracao(Interacao interacao){
        this(interacao.getId(), interacao.getReceita().getId(), interacao.getUsuario().getId(), interacao.getComentario(), interacao.getCurtida());
    }
}
