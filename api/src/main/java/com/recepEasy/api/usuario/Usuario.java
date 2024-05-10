package com.recepEasy.api.usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.recepEasy.api.endereco.Endereco;
import com.recepEasy.api.usuario.dto.DadosAlterarUsuario;
import com.recepEasy.api.usuario.dto.DadosCadastroUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate nascimento;
    private String senha;
    private String login;

    @JsonIgnore
    private boolean ativo;

    @OneToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    public Usuario(DadosCadastroUsuario dados){
        this.ativo = true;
        this.nome = dados.nome();
        this.cpf= dados.cpf();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.nascimento = dados.nascimento();
        this.senha = dados.senha();
        this.login = dados.login();
        this.endereco = new Endereco(dados.endereco());
    }

    public void atualizar(DadosAlterarUsuario usuario){
        if(usuario.nome()!=null) {
            this.nome = usuario.nome();
        }
        if(usuario.email()!=null) {
            this.email = usuario.email();
        }
        if(usuario.telefone()!=null) {
            this.telefone = usuario.telefone();
        }
        if(usuario.nascimento()!=null) {
            this.nascimento = usuario.nascimento();
        }
        if(usuario.endereco() !=null) {
            this.endereco.atualizarEndereco(usuario.endereco());
        }
        if(usuario.login() !=null) {
            this.login = usuario.login();
        }

    }

    public void excluir() {
        this.ativo= false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
         return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean getAtivo(){
        return this.ativo;
    }

}
