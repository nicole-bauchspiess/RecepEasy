package com.recepEasy.api.receita;

import com.recepEasy.api.exceptions.NotFoundException;
import com.recepEasy.api.receita.dto.DadosAlterarReceita;
import com.recepEasy.api.receita.dto.DadosDetalhamentoReceita;

import com.recepEasy.api.receita.dto.DadosDetalhamentoReceitasCurtidas;
import com.recepEasy.api.usuario.Usuario;
import com.recepEasy.api.security.SecurityFilter;
import com.recepEasy.api.security.TokenService;
import com.recepEasy.api.chatGPT.ConsultaChatGPT;
import com.recepEasy.api.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReceitaService {

    @Autowired
    private ReceitaRepository repository;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SecurityFilter filter;

    @Autowired
    private ConsultaChatGPT consultaChatGPT;


    public void inserir(Receita obj) {
        Usuario usuario = getUsuario();
        obj.setUsuario(usuario);
        repository.save(obj);
    }


    public Receita atualizar(DadosAlterarReceita dados) {
        Receita receita = findById(dados.id());
        validaUsuario(receita);
        receita.atualizar(dados);
        return receita;
    }

    public Receita deletar(Long id) {
        Receita receita = findById(id);
        validaUsuario(receita);
        receita.excluir();
        return receita;
    }

    public Receita findById(Long id) {
        Optional<Receita> receitaOptional = repository.findById(id);
        receitaOptional.orElseThrow(() -> new NotFoundException("Receita " + id + " não encontrada"));
        return receitaOptional.get();
    }

    private void validaUsuario(Receita receita) {
        Usuario usuario = getUsuario();
        if (!usuario.equals(receita.getUsuario())) {
            throw new IllegalArgumentException("Não é possível editar uma receita de outro usuário");
        }
    }

    public Usuario getUsuario() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }



    public Page<DadosDetalhamentoReceita> pegarReceitaDoUsuario(Long id, Pageable paginacao) {
        Usuario usuario = usuarioService.findById(id);
        Usuario usuarioLogado = getUsuario();

        if (usuarioLogado.equals(usuario)) { //se for o mesmo usuário: retorna todas as receitas independente se estiver privado ou nao
            return repository.findReceitasByUsuario(usuario, paginacao);
        } else {
            return repository.findReceitasByUsuarioPrivadoFalse(usuario, paginacao);
        }
    }


    public Receita receitaChatGPT(String ingredientes) {
        String isComestivel = consultaChatGPT.validaComestivel(ingredientes);
        if (isComestivel.toLowerCase().contains("nao") || isComestivel.toLowerCase().contains("não")) {
            throw new IllegalArgumentException("Não é possível gerar receita com ingredientes não comestíveis");
        }
        String texto = consultaChatGPT.obterReceita(ingredientes);
        System.out.println(texto);

        int posIngredientes = texto.toLowerCase().indexOf("ingredientes");
        int posPreparo = texto.toLowerCase().indexOf("preparo:");
        int posSabor = texto.toLowerCase().indexOf("sabor");

        String nomeReceita = texto.substring(0, posIngredientes).replace("Nome: ", "").replace("\n", "");
        String ingredientes2 = texto.substring(posIngredientes, posPreparo).replace("Ingredientes:", "");
        String modoPreparo = texto.substring(posPreparo, posSabor).replace("Preparo:", "");

        String sabor = texto.substring(posSabor).replace("Sabor: ", "").toUpperCase().replace(".", "");
        if (sabor.contains(" ")) {
            sabor = sabor.split(" ")[0];
        }

        return new Receita(null, nomeReceita, ingredientes2, modoPreparo, sabor, true, true, LocalDateTime.now(), 0, null);
    }


    public int getQuantidadeCurtidas(Receita receita) {
        if (receita == null) {
            throw new NotFoundException("Não foi possível localizar a receita");
        }
        return receita.getQtdCurtidas();
    }


    public Page<DadosDetalhamentoReceitasCurtidas> receitasMaisCurtidasSpec(BuscaReceitaForm busca) {
        Integer page = 0;
        Integer size = 10;
        if (busca.getTop() != null && busca.getTop() > 0) {
            size = busca.getTop();
        }
        if (busca.getPage() != null && busca.getPage() >= 0) {
            page = busca.getPage();
        }
        Pageable paginacao = PageRequest.of(page, size);
        return repository.findAll(busca.toSpec(), paginacao).map(DadosDetalhamentoReceitasCurtidas::new);
    }


    public void validaReceita(Receita receita) {
        Usuario usuarioLogado = getUsuario();
        Usuario user = receita.getUsuario();
        if (!user.equals(usuarioLogado)) {
            if (receita.getPrivado()) {
                throw new IllegalArgumentException("Não é possível ver detalhes de receitas privadas");
            }
        }
        if (!receita.getAtivo()) {
            throw new IllegalArgumentException("Não é possível ver detalhes de receitas inativas");
        }
    }

    public List<Receita> inativarReceitas(Usuario usuario) {
        return repository.inativarReceitas(usuario);
    }

}
