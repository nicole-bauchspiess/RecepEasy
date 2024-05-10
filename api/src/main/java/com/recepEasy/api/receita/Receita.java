package com.recepEasy.api.receita;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.recepEasy.api.interacao.Interacao;
import com.recepEasy.api.receita.dto.DadosAlterarReceita;
import com.recepEasy.api.receita.dto.DadosCadastroReceita;
import com.recepEasy.api.receita.dto.DadosCadastroReceitaGPT;
import com.recepEasy.api.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Entity
@Table(name = "receitas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Column(columnDefinition = "text")
    private String ingredientes;
    @Column(columnDefinition = "text")
    private String modoPreparo;
    private String sabor;
    @JsonIgnore
    private boolean ativo;
    private boolean privado;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime horaPublicacao;
    private int qtdCurtidas;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Receita(DadosCadastroReceita dados) {
        this.ativo = true;
        this.privado = dados.privado();
        this.nome = dados.nome();
        this.ingredientes = dados.ingredientes();
        this.modoPreparo = dados.modoPreparo();
        this.horaPublicacao = LocalDateTime.now();
        this.sabor = dados.sabor();
    }

    public Receita(DadosCadastroReceitaGPT dados) {
        this.ativo = true;
        this.ingredientes = dados.ingredientes();
        this.privado = dados.privado();
    }

    public void tornarPublico() {
        this.privado = false;
    }

    public void excluir() {
        this.ativo = false;
    }

    public void atualizar(DadosAlterarReceita dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.ingredientes() != null) {
            this.ingredientes = dados.ingredientes();
        }
        if (dados.modoPreparo() != null) {
            this.modoPreparo = dados.modoPreparo();
        }
        if (dados.privado() != null) {
            this.privado = dados.privado();
        }

    }

    public boolean getAtivo() {
        return this.ativo;
    }
    public boolean getPrivado(){return this.privado;}


}
