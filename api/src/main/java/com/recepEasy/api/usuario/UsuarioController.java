package com.recepEasy.api.usuario;

import com.recepEasy.api.endereco.Endereco;
import com.recepEasy.api.endereco.EnderecoService;
import com.recepEasy.api.usuario.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private EnderecoService enderecoService;


    @GetMapping
    @Operation(summary = "lista os usuários")
    public ResponseEntity getUsuarios() {
        List<Usuario> usuarios = service.findAll();
        List<DadosTodosUsuarios> dados = usuarios.stream().map(DadosTodosUsuarios::new).toList();
        return ResponseEntity.ok().body(dados);
    }

    @PostMapping
    @Operation(summary = "Cria um usuário")
    public ResponseEntity insertUsuario (@RequestBody @Valid DadosCadastroUsuario dadosUsuario) {

        Usuario usuario = new Usuario(dadosUsuario);
        service.inserir(usuario, dadosUsuario);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(usuario.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosListarUsario(usuario));
    }


    @PutMapping
    @Transactional
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Atualiza um usuário")
    public ResponseEntity atualizarUsuarios( @RequestBody @Valid DadosAlterarUsuario dados) {
        Usuario obj = service.atualizar(dados);
        return ResponseEntity.ok().body(new DadosListarUsario(obj));
    }


    @DeleteMapping(value = "/{id}")
    @Transactional
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Exclui logicamente um usuário")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Usuario obj = service.deletar(id);
        return ResponseEntity.noContent().build();
    }


}
