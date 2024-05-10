package com.recepEasy.api.interacao;

import com.recepEasy.api.interacao.dto.DadosCadastroInteracao;
import com.recepEasy.api.receita.Receita;
import com.recepEasy.api.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name ="interacoes")
@NoArgsConstructor
@AllArgsConstructor
public class Interacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "receita_id")
    private Receita receita;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    private String comentario;
    private Boolean curtida;

    public Interacao(DadosCadastroInteracao dados){
        this.comentario = dados.comentario();
        this.curtida = dados.curtida();
    }

    public boolean getCurtida(){
        return this.curtida;
    }

}
