package com.recepEasy.api.interacao.dto;

import com.recepEasy.api.interacao.Interacao;

public record DadosListagemInteracaoReceita(

        Long usuario_id,
        String comentario

){
    public DadosListagemInteracaoReceita(Interacao interacao){
        this (interacao.getUsuario().getId(), interacao.getComentario());
    }
}
