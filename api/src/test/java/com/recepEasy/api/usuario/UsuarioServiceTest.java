package com.recepEasy.api.usuario;

import com.recepEasy.api.exceptions.NotFoundException;
import com.recepEasy.api.usuario.dto.DadosAlterarUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private UsuarioService service;
    @Mock
    private UsuarioRepository repository;

    Usuario usuario;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        usuario = new Usuario();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    @DisplayName("OK-> Deveria retornar um usuário ")
    public void findById(){
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(usuario));
        assertThat(usuario).isEqualTo(service.findById(id));
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao buscar um usuário que não existe")
    public void findById_usuarioNaoEncontrado(){
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(()-> service.findById(id));
        assertThat(throwable).isInstanceOf(NotFoundException.class).hasMessage("Usuario 1 não encontrado");
    }

    @Test
    @DisplayName("OK-> Deveria atualizar o usuário quando o usuário logado for o mesmo que está sendo atualizado")
    public void atualizar(){
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(usuario));
        DadosAlterarUsuario dados = new DadosAlterarUsuario(1L, "novo nome", "novo@gmail.com", null, null, null, null);
        service.atualizar(dados);
        verify(repository).findById(id);

        assertEquals(usuario.getNome(), "novo nome");
        assertEquals(usuario.getEmail(), "novo@gmail.com");
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao tentar atualizar outro usuário")
    public void atualizar_usuarioIncorreto(){

        Long id = 1L;
        DadosAlterarUsuario dados = new DadosAlterarUsuario(id, "novo nome", "novo@gmail.com", null, null, null, null);
        Usuario usuario1 = new Usuario();
        usuario1.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(usuario1));
        Throwable throwable = catchThrowable(()-> service.atualizar(dados));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível editar outro usuário");
    }

    @Test
    @DisplayName("OK-> Deveria deletar o usuário")
    public void deletar(){
        Long id = 1L;
        usuario.setAtivo(true);
        when(repository.findById(id)).thenReturn(Optional.of(usuario));
        service.deletar(id);
        verify(repository).findById(id);

        assertFalse(usuario.getAtivo());
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao tentar deletar outro usuário")
    public void deletar_usuarioIncorreto(){
        Long id = 1L;
        Usuario usuario1 = new Usuario();
        usuario1.setAtivo(true);
        usuario1.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(usuario1));

        Throwable throwable = catchThrowable(()-> service.deletar(id));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível editar outro usuário");
        assertTrue(usuario1.getAtivo());
    }

    @Test
    public void validarLogin(){
        String login = "login";
        when(repository.findByLogin(login)).thenReturn(Optional.of(usuario));
        usuario.setAtivo(true);
        service.validarLogin(login);
        verify(repository).findByLogin(login);
    }

    @Test
    public void validarLogin_usuarioInativo(){
        String login = "login";
        when(repository.findByLogin(login)).thenReturn(Optional.of(usuario));
        usuario.setAtivo(false);

        Throwable throwable = catchThrowable(()-> service.validarLogin(login));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível logal com usuário inativo");
    }

}