package com.recepEasy.api.interacao;

import com.recepEasy.api.interacao.dto.DadosCadastroInteracao;
import com.recepEasy.api.interacao.dto.DadosListagemInteracao;
import com.recepEasy.api.receita.Receita;
import com.recepEasy.api.receita.ReceitaService;
import com.recepEasy.api.receita.dto.DadosDetalhamentoReceita;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/receitas")
@SecurityRequirement(name = "bearer-key")
public class InteracaoController {

    @Autowired
    private ReceitaService receitaService;

    @Autowired
    private InteracaoService service;

    @PostMapping("/{id_receita}")
    @Operation(summary = "Insere uma interação na receita")
    public ResponseEntity inserir(@PathVariable Long id_receita, @RequestBody @Valid DadosCadastroInteracao dados){
        Receita receita = receitaService.findById(id_receita);
        Interacao interacao = new Interacao(dados);

        service.salvarInteracao(receita, interacao, dados);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(interacao).toUri();

        return ResponseEntity.created(uri).body(new DadosListagemInteracao(interacao));
    }



}
