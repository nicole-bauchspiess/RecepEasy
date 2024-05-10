package com.recepEasy.api.interacao;

import com.recepEasy.api.exceptions.NotFoundException;
import com.recepEasy.api.interacao.dto.DadosCadastroInteracao;
import com.recepEasy.api.interacao.dto.DadosListagemInteracaoReceita;
import com.recepEasy.api.receita.Receita;
import com.recepEasy.api.receita.ReceitaService;
import com.recepEasy.api.security.SecurityFilter;
import com.recepEasy.api.security.TokenService;
import com.recepEasy.api.usuario.Usuario;
import com.recepEasy.api.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InteracaoService {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SecurityFilter filter;

    @Autowired
    private InteracaoRepository repository;

    @Autowired
    private ReceitaService receitaService;


    public void salvarInteracao(Receita receita, Interacao interacao, DadosCadastroInteracao dados) {
        if (receita.getPrivado()) {
            throw new IllegalArgumentException("Não é possível interagir com receitas privadas");
        }
        if (!receita.getAtivo()) {
            throw new IllegalArgumentException("Não é possível interagir com receitas inativas");
        }

        Usuario usuario = getUsuario();
        interacao.setUsuario(usuario);
        interacao.setReceita(receita);

        if (dados.curtida() != null) {
            if (dados.curtida()) {
                curtir(receita);
                inserir(interacao);
            } else {
                removerCurtida(receita);
            }
        } else {
            interacao.setCurtida(false);
            inserir(interacao);
        }
    }

    public void inserir(Interacao interacao) {
        repository.save(interacao);
    }


    public void curtir(Receita receita) {
        Usuario usuario = getUsuario();

        Optional<Interacao> interacaoOptional = repository.verificarUsuarioCurtidaReceita(usuario, receita);
        if (interacaoOptional.isPresent()) {
            throw new IllegalArgumentException("Não é possível curtir a mesma receita diversas vezes");
        }
        receita.setQtdCurtidas(receita.getQtdCurtidas() + 1);
    }

    private Usuario getUsuario() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    //pega o usuário do token, verifica se tem alguma curtida no banco com este usuário e esta receita, se tiver, seta como curtida = false
    public void removerCurtida(Receita receita) {
        Usuario usuario = getUsuario();

        Optional<Interacao> interacaoOptional = repository.verificarUsuarioCurtidaReceita(usuario, receita);

        if (interacaoOptional.isPresent()) {
            Interacao interacao = interacaoOptional.get();
            receita.setQtdCurtidas(receita.getQtdCurtidas() - 1);
            interacao.setCurtida(false);
            inserir(interacao);
        } else {
            throw new NotFoundException("Não foi possível localizar curtida deste usuário para esta receita");
        }
    }

    //ver detalhes da receita: todos os comentarios
    public Page<DadosListagemInteracaoReceita> pegarInteracoesDaReceita(Receita receita, Pageable paginacao) {
        return repository.listaInteracoesDaReceita(receita, paginacao);
    }


}
