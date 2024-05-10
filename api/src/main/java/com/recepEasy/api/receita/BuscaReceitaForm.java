package com.recepEasy.api.receita;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.catalina.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Paths.get;

public class BuscaReceitaForm {

    String nome;
    String sabor;
    Integer top;
    Integer page;

    public BuscaReceitaForm(String nome, String sabor, Integer top, Integer page) {
        this.nome = nome;
        this.sabor = sabor;
        this.top = top;
        this.page = page;
    }

    public String getNome() {
        return nome;
    }

    public String getSabor() {
        return sabor;
    }

    public Integer getTop() {
        return top;
    }

    public Integer getPage() {
        return page;
    }

    public Specification<Receita> toSpec(){

        return (root,query, builder)-> {
            List<Predicate> predicados = new ArrayList<>();
          if(nome!= null &&!nome.isBlank()){
              Path<String> campoNome = root.<String>get("nome");
              Predicate predicadoNome = builder.like(builder.lower(campoNome), "%" + nome.toLowerCase() + "%");
              predicados.add(predicadoNome);
          }
            if(sabor!= null &&!sabor.isBlank()){
                Path<String> campoSabor = root.<String>get("sabor");
                Predicate predicadoSabor = builder.like(builder.lower(campoSabor), "%" + sabor.toLowerCase() + "%");
                predicados.add(predicadoSabor);
            }
            if(top!= null &&top > 0){
                Path<Integer> qtdCurtidas = root.<Integer>get("qtdCurtidas");
                query.orderBy(builder.desc(qtdCurtidas));
            }
            Path<Boolean> privadoSpec = root.<Boolean>get("privado");
            Predicate predicadoPrivado = builder.equal(privadoSpec, false);
            predicados.add(predicadoPrivado);
            predicados.add(builder.equal(root.<Boolean>get("ativo"), true));

            return builder.and(predicados.toArray(new Predicate[0]));

        };
    }

}
