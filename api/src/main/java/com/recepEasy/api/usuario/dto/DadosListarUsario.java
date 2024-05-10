package com.recepEasy.api.usuario.dto;

import com.recepEasy.api.endereco.Endereco;
import com.recepEasy.api.usuario.Usuario;

import java.time.LocalDate;

public record DadosListarUsario(
    Long id,
    String nome,
    String email,
    String telefone,
    LocalDate nascimento,
    Endereco endereco
) {
    public DadosListarUsario(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(),
                usuario.getNascimento(), usuario.getEndereco());
    }
}
