package com.recepEasy.api.usuario.dto;

import com.recepEasy.api.usuario.Usuario;

import java.time.LocalDate;

public record DadosExibirUsuario(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalDate nascimento
) {
    public DadosExibirUsuario(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(),
                usuario.getNascimento());
    }

}
