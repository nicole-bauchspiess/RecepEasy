package com.recepEasy.api.interacao.dto;

import com.recepEasy.api.receita.Receita;
import org.springframework.data.domain.Page;

public class DadosListagem{

    private int qtdCurtida;
    private Page<DadosListagemInteracaoReceita> dados;

    public DadosListagem(Receita receita, Page<DadosListagemInteracaoReceita> dados){
        this.qtdCurtida = receita.getQtdCurtidas();
        this.dados = dados;
    }

    public int getQtdCurtida() {
        return qtdCurtida;
    }

    public Page<DadosListagemInteracaoReceita> getDados() {
        return dados;
    }
}