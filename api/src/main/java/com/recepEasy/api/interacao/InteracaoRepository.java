package com.recepEasy.api.interacao;

import com.recepEasy.api.interacao.dto.DadosListagemInteracaoReceita;
import com.recepEasy.api.receita.Receita;
import com.recepEasy.api.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface InteracaoRepository extends JpaRepository<Interacao, Long> {



    @Query("""
            select i
            from Interacao i
            where i.usuario = :usuario
            and i.curtida = true
            and i.receita = :receita
            """)
    Optional<Interacao> verificarUsuarioCurtidaReceita(Usuario usuario, Receita receita);

    @Query("""
            select i
            from Interacao i
            where i.receita = :receita
            """)
    Page<DadosListagemInteracaoReceita> listaInteracoesDaReceita(Receita receita, Pageable pageable);


}
