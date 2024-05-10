package com.recepEasy.api.receita;

import com.recepEasy.api.chatGPT.ConsultaChatGPT;
import com.recepEasy.api.exceptions.NotFoundException;
import com.recepEasy.api.receita.dto.DadosAlterarReceita;
import com.recepEasy.api.receita.dto.DadosDetalhamentoReceita;
import com.recepEasy.api.usuario.Usuario;
import com.recepEasy.api.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReceitaServiceTest {

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private ReceitaRepository repository;

    @InjectMocks
    private ReceitaService service;

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ConsultaChatGPT consultaChatGPT;
    @Captor
    private ArgumentCaptor<Page> captor;


    Usuario usuario;

    @BeforeEach
    void setUp(){
        usuario = new Usuario();
        MockitoAnnotations.initMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("OK-> Deveria retornar uma receita")
    public void findById(){
        Long id = 1L;
        Receita receita = new Receita();
        when(repository.findById(id)).thenReturn(Optional.of(receita));
        assertThat(receita).isEqualTo(service.findById(id));
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao buscar uma receita que não existe")
    public void findById_receitaNaoEncontrada(){
        Long id = 1L;
        Receita receita = new Receita();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(()-> service.findById(id));
        assertThat(throwable).isInstanceOf(NotFoundException.class).hasMessage("Receita 1 não encontrada");
    }

    @Test
    @DisplayName("OK-> Deveria atualizar a receita quando o usuário logado for quem fez a publicação da receita")
    public void atualizar(){

        DadosAlterarReceita dados = new DadosAlterarReceita(1L, "nova receita", "ingredientes", null, true);
        Long id = 1L;
        Receita receita = new Receita();
        receita.setUsuario(usuario);

        when(repository.findById(id)).thenReturn(Optional.of(receita));

        service.atualizar(dados);
        verify(repository).findById(id);

        assertEquals(receita.getNome(), "nova receita");
        assertEquals(receita.getIngredientes(), "ingredientes");
        assertNull(receita.getModoPreparo());
    }

    @Test
    @DisplayName(" Not OK-> Deveria lançar exceção ao tentar atualizar a receita de outro usuário")
    public void atualizar_usuarioIncorreto(){
        DadosAlterarReceita dados = new DadosAlterarReceita(1L, "nova receita", "ingredientes", null, true);
        Long id = 1L;
        Receita receita = new Receita();
        when(repository.findById(id)).thenReturn(Optional.of(receita));
        Throwable throwable = catchThrowable(()-> service.atualizar(dados));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível editar uma receita de outro usuário");
    }

    @Test
    @DisplayName("OK-> Deveria deletar a receita quando o usuário logado for quem fez a publicação da receita")
    public void deletar(){
        Long id = 1L;
        Receita receita = new Receita();
        receita.setUsuario(usuario);

        when(repository.findById(id)).thenReturn(Optional.of(receita));
        service.deletar(id);
        verify(repository).findById(id);

        assertFalse(receita.getAtivo());
    }

    @Test
    @DisplayName(" Not OK-> Deveria lançar exceção ao tentar deletar a receita de outro usuário")
    public void deletar_usuarioIncorreto(){
        Long id = 1L;
        Receita receita = new Receita();
        receita.setAtivo(true);
        when(repository.findById(id)).thenReturn(Optional.of(receita));

        Throwable throwable = catchThrowable(()-> service.deletar(id));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível editar uma receita de outro usuário");
        assertTrue(receita.getAtivo());
    }

    @Test
    @DisplayName("OK-> Deveria retornar todas as receitas cadastradas, mesmo as privadas quando o usuário for igual ao usuário logado")
    public void pegarReceitaDoUsuario(){
        Long id = 1L;

        when(usuarioService.findById(id)).thenReturn(usuario);

        Pageable page = PageRequest.of(2, 50);
        service.pegarReceitaDoUsuario(id, page);
        verify(repository).findReceitasByUsuario(usuario, page);
       verify(repository,never()).findReceitasByUsuarioPrivadoFalse(usuario, page);

    }

    @Test
    @DisplayName("OK-> Deveria retornar somente as receitas publicas quando o usuário for diferente do logado")
    public void pegarReceitaDoUsuario_usuarioDiferente(){
        Long id = 1L;
        Usuario usuario1 = new Usuario();
        usuario1.setId(id);
        usuario.setId(2L);
        when(usuarioService.findById(id)).thenReturn(usuario1);

        Pageable page = PageRequest.of(2, 50);
        service.pegarReceitaDoUsuario(id, page);
        verify(repository).findReceitasByUsuarioPrivadoFalse(usuario1, page);
        verify(repository, never()).findReceitasByUsuario(usuario, page);
    }


    @Test
    @DisplayName("OK-> Deveria retornar a quantidade de curtidas")
    public void getQuantidadeCurtidas(){
        Receita receita = new Receita();
        receita.setQtdCurtidas(10);
        assertThat(service.getQuantidadeCurtidas(receita)).isEqualTo(10);
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção quando nao tem receita")
    public void getQuantidadeCurtidas_receitaNula(){
        Receita receita = null;
        Throwable throwable = catchThrowable(()-> service.getQuantidadeCurtidas(receita));
        assertThat(throwable).isInstanceOf(NotFoundException.class).hasMessage("Não foi possível localizar a receita");
    }


    @Test
    @DisplayName("Not OK-> Deveria lançar exceção quando tenta ver detalhes de receita privada e o usuário nao for o logado")
    public void validaReceita_receitaPrivada(){
        Receita receita = new Receita();
        receita.setPrivado(true);
        receita.setUsuario(new Usuario());
        usuario.setId(1L);

        Throwable throwable = catchThrowable(()-> service.validaReceita(receita));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível ver detalhes de receitas privadas");

    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção quando tenta ver detalhes de receita inativa")
    public void validaReceita_Inativa(){
        Receita receita = new Receita();
        receita.setAtivo(false);
        usuario.setId(1L);
        receita.setUsuario(usuario);
        Throwable throwable = catchThrowable(()-> service.validaReceita(receita));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível ver detalhes de receitas inativas");

    }


    @Test
    @DisplayName("OK-> Deveria converter texto do chatGPT para receita")
    public void receitaChatGPT(){
        String textoGPT = """
                Nome: Pão de Banana com Ovo
                                
                Ingredientes:
                - 2 bananas maduras
                - 2 ovos
                - 4 fatias de pão
                - Açúcar e canela a gosto (opcional)
                                
                Preparo:
                1. Em uma tigela, amasse as bananas até formar um purê.
                2. Adicione os ovos e misture bem até obter uma massa homogênea.
                3. Se desejar, adicione açúcar e canela a gosto na massa.
                4. Passe a mistura em uma fatia de pão e cubra com outra fatia, formando um sanduíche.
                5. Repita o processo até utilizar todo o recheio.
                6. Aqueça uma frigideira antiaderente em fogo médio.
                7. Coloque os sanduíches na frigideira e deixe dourar por cerca de 2 minutos de cada lado.
                8. Sirva quente, com uma pitada de açúcar e canela por cima, se desejar.
                                
                Sabor: Doce e saboroso.
                """;

        Receita receita = new Receita();
        when(consultaChatGPT.validaComestivel("banana, ovo, pao")).thenReturn("comestivel");
        when(consultaChatGPT.obterReceita("banana, ovo, pao")).thenReturn(textoGPT);
        receita = service.receitaChatGPT("banana, ovo, pao");
        assertThat(receita.getNome()).isEqualTo("Pão de Banana com Ovo");
        assertThat(receita.getIngredientes()).isEqualTo("\n- 2 bananas maduras\n- 2 ovos\n- 4 fatias de pão\n- Açúcar e canela a gosto (opcional)\n\n");
        assertThat(receita.getModoPreparo()).isEqualTo("\n1. Em uma tigela, amasse as bananas até formar um purê." +
                "\n2. Adicione os ovos e misture bem até obter uma massa homogênea." +
                "\n3. Se desejar, adicione açúcar e canela a gosto na massa." +
                "\n4. Passe a mistura em uma fatia de pão e cubra com outra fatia, formando um sanduíche." +
                "\n5. Repita o processo até utilizar todo o recheio." +
                "\n6. Aqueça uma frigideira antiaderente em fogo médio." +
                "\n7. Coloque os sanduíches na frigideira e deixe dourar por cerca de 2 minutos de cada lado." +
                "\n8. Sirva quente, com uma pitada de açúcar e canela por cima, se desejar.\n\n");
        assertThat(receita.getSabor()).isEqualTo("DOCE");
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção ao encontrar objeto nao comestivel")
    public void receitaChatGPT_ingredienteNaoComestivel(){
        when(consultaChatGPT.validaComestivel("teclado, ovo, pao")).thenReturn("nao comestivel");
        Throwable throwable = catchThrowable(()-> service.receitaChatGPT("teclado, ovo, pao"));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Não é possível gerar receita com ingredientes não comestíveis");
    }

    @Test
    @DisplayName("OK -> Deveria retornar o usuário")
    public void getUsuario(){
        Usuario usuario1 = service.getUsuario();
        usuario.setId(1L);
        assertEquals(usuario1, usuario);
    }

    @Test
    @DisplayName("Not OK-> Deveria lançar exceção quando a paginação for menor ou igual a 0")
    public void receitasMaisCurtidas(){
        int pageSize = -3;
        Throwable throwable = catchThrowable(()-> service.receitasMaisCurtidas(pageSize, null));
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Quantidade de receitas mais curtidas deve ser maior que 0");
    }




}