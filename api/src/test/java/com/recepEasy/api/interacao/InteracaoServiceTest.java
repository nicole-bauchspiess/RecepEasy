package com.recepEasy.api.interacao;

import com.recepEasy.api.exceptions.NotFoundException;
import com.recepEasy.api.interacao.dto.DadosCadastroInteracao;
import com.recepEasy.api.receita.Receita;
import com.recepEasy.api.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InteracaoServiceTest {

    @InjectMocks
    private InteracaoService service;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private InteracaoRepository repository;

    Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        usuario = new Usuario();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao tentar salvar interação quando a receita for privada")
    void salvarInteracao_receitaPrivada() {
        Receita receita = new Receita();
        Interacao interacao = new Interacao();
        interacao.setUsuario(usuario);
        receita.setPrivado(true);
        DadosCadastroInteracao dadosCadastroInteracao = new DadosCadastroInteracao("comentario", true);

        Throwable throwable = catchThrowable(() -> service.salvarInteracao(receita, interacao, dadosCadastroInteracao));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível interagir com receitas privadas");
    }
    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao tentar salvar interação quando a receita for inativa")
    void salvarInteracao_receitaInativa() {
        Receita receita = new Receita();
        Interacao interacao = new Interacao();
        interacao.setUsuario(usuario);
        receita.setPrivado(false);
        receita.setAtivo(false);
        DadosCadastroInteracao dadosCadastroInteracao = new DadosCadastroInteracao("comentario", true);

        Throwable throwable = catchThrowable(() -> service.salvarInteracao(receita, interacao, dadosCadastroInteracao));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível interagir com receitas inativas");
    }


    @Test
    @DisplayName("OK-> Deveria salvar a interação")
    void salvarInteracao() {
        Receita receita = new Receita();
        receita.setPrivado(false);
        receita.setAtivo(true);
        Interacao interacao = new Interacao();
        interacao.setCurtida(true);

        DadosCadastroInteracao dadosCadastroInteracao = new DadosCadastroInteracao("comentario", true);
        service.salvarInteracao(receita, interacao, dadosCadastroInteracao);
        verify(repository).save(interacao);
        assertThat(interacao.getUsuario()).isEqualTo(usuario);
        assertThat(interacao.getReceita()).isEqualTo(receita);
    }



    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao tentar curtir uma receita que já está curtida pelo usuário")
    void curtir_receitaJaCurtidaPeloUsuario() {
        Receita receita = new Receita();
        receita.setPrivado(false);
        receita.setQtdCurtidas(10);
        Interacao interacao = new Interacao();
        interacao.setCurtida(true);

        when(repository.verificarUsuarioCurtidaReceita(usuario, receita)).thenReturn(Optional.of(interacao));

        Throwable throwable = catchThrowable(() -> service.curtir(receita));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não é possível curtir a mesma receita diversas vezes");

    }

    @Test
    @DisplayName("OK-> Deveria curtir a receita")
    void curtir() {
        Receita receita = new Receita();
        receita.setPrivado(false);
        receita.setQtdCurtidas(10);


        when(repository.verificarUsuarioCurtidaReceita(usuario, receita)).thenReturn(Optional.empty());
        service.curtir(receita);
        assertThat(receita.getQtdCurtidas()).isEqualTo(11);
    }

    @Test
    @DisplayName("OK-> Deveria remover curtida do usuário ao identificar interação de curtida com a receita")
    void removerCurtida() {
        Receita receita = new Receita();
        receita.setPrivado(false);
        receita.setQtdCurtidas(10);

        Interacao interacao = new Interacao();
        interacao.setCurtida(true);
        when(repository.verificarUsuarioCurtidaReceita(usuario, receita)).thenReturn(Optional.of(interacao));
        service.removerCurtida(receita);
        verify(repository).save(interacao);
        assertThat(receita.getQtdCurtidas()).isEqualTo(9);
        assertFalse(interacao.getCurtida());
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao tentar remover curtida quando não há curtida deste usuário nesta receita")
    void removerCurtida_semInteracoes() {
        Receita receita = new Receita();
        receita.setPrivado(false);
        receita.setQtdCurtidas(10);
        Interacao interacao = new Interacao();

        when(repository.verificarUsuarioCurtidaReceita(usuario, receita)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> service.removerCurtida(receita));
        verify(repository, never()).save(interacao);
        assertThat(throwable).isInstanceOf(NotFoundException.class).hasMessage("Não foi possível localizar curtida deste usuário para esta receita");

    }


}