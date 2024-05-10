package com.recepEasy.api.usuario.dto;

import com.recepEasy.api.endereco.Endereco;
import com.recepEasy.api.usuario.Usuario;

import java.time.LocalDate;

public record DadosTodosUsuarios(
    Long id,
    String nome,
    String email,
    String login
) {
    public DadosTodosUsuarios(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(),usuario.getLogin());
    }
}
