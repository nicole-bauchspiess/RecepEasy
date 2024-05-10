package com.recepEasy.api.usuario;

import com.recepEasy.api.endereco.Endereco;
import com.recepEasy.api.endereco.EnderecoService;
import com.recepEasy.api.exceptions.NotFoundException;
import com.recepEasy.api.receita.Receita;
import com.recepEasy.api.receita.ReceitaService;
import com.recepEasy.api.usuario.dto.DadosAlterarUsuario;
import com.recepEasy.api.usuario.dto.DadosCadastroUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    @Lazy
    private ReceitaService receitaService;

    public Usuario findById(Long id) {
        Optional<Usuario> user = repository.findById(id);
        user.orElseThrow(() -> new NotFoundException("Usuario " + id + " não encontrado"));
        return user.get();
    }

    public Usuario findByLogin(String login) {
        Optional<Usuario> usuarioOptional = repository.findByLogin(login);
        usuarioOptional.orElseThrow(() -> new NotFoundException("Não foi possível localizar o login"));
        return usuarioOptional.get();
    }

    public Usuario inserir(Usuario usuario, DadosCadastroUsuario dadosUsuario) {
        this.criaEndereco(usuario,dadosUsuario);
        usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    public void criaEndereco(Usuario usuario, DadosCadastroUsuario dadosUsuario){
        String cep = dadosUsuario.endereco().cep();
        String numero = dadosUsuario.endereco().numero();
        String complemento = dadosUsuario.endereco().complemento();
        Endereco endereco;
        if(dadosUsuario.endereco().logradouro()==null) {
            endereco = enderecoService.consultaEnderecoViaCeps(cep, numero, complemento);
        }else{
            endereco = new Endereco(dadosUsuario.endereco());
        }
        usuario.setEndereco(endereco);
        enderecoService.inserir(endereco);
    }


    //ao deletar o usuario: inativa todas as receitas
    public Usuario deletar(Long id) {
        Usuario usuarioLogado = getUsuario();
        Usuario usuario = findById(id);
        if (!usuarioLogado.equals(usuario)) {
            throw new IllegalArgumentException("Não é possível excluir outro usuário");
        }
        usuario.excluir();
        List<Receita> receitas = receitaService.inativarReceitas(usuario);
        receitas.forEach(r -> r.setAtivo(false));
        return usuario;
    }

    public Usuario atualizar(DadosAlterarUsuario dados) {
        Usuario usuarioLogado = getUsuario();
        Usuario usuario = findById(dados.id());
        if (!usuarioLogado.equals(usuario)) {
            throw new IllegalArgumentException("Não é possível alterar outro usuário");
        }
        usuario.atualizar(dados);
        return usuario;
    }


    private Usuario getUsuario() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<Usuario> findAll(){
        return repository.findAllOrderById();
    }

    public void validarLogin(String login) {
        Usuario usuario = findByLogin(login);
        if (!usuario.getAtivo()) {
            throw new IllegalArgumentException("Não é possível logar com usuário inativo");
        }
    }
}
