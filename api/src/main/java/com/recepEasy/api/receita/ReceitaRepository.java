package com.recepEasy.api.receita;

import com.recepEasy.api.receita.dto.DadosDetalhamentoReceita;
import com.recepEasy.api.receita.dto.DadosDetalhamentoReceitasCurtidas;
import com.recepEasy.api.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ReceitaRepository extends JpaRepository<Receita, Long>, PagingAndSortingRepository<Receita, Long>, JpaSpecificationExecutor<Receita>{



    @Query("""
            select r
            from Receita r
            where usuario = :usuario
            and ativo = true
            order by horaPublicacao desc
            """)
    Page<DadosDetalhamentoReceita> findReceitasByUsuario(Usuario usuario,Pageable paginacao);

    @Query("""
            select r
            from Receita r
            where usuario = :usuario
            and privado = false
            and ativo = true
            order by horaPublicacao desc
            """)
    Page<DadosDetalhamentoReceita> findReceitasByUsuarioPrivadoFalse
            (Usuario usuario,Pageable paginacao);



    @Query("""
            select r
            from Receita r
            where r.usuario = :usuario
            """)
    List<Receita> inativarReceitas(Usuario usuario);

//@Query("""
//        select r
//        from Receita r
//        where ativo = true
//        and privado = false
//        """)
//    Page<DadosDetalhamentoReceitasCurtidas> findAllByAtivoTrueAndPrivadoFalse(Specification<Receita> spec,Pageable pageable);

}
