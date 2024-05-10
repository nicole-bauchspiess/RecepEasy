package com.recepEasy.api.endereco;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "endereco")
@Table(name ="endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endereco_id")
    private Long id;
    private String logradouro;
    private String cep;
    private String bairro;
    private String localidade;
    private String uf;
    private String complemento;
    private String numero;


    public Endereco(DadosEndereco dados){
        this.cep = dados.cep();
        this.numero = dados.numero();
        this.logradouro = dados.logradouro();
        this.bairro = dados.bairro();
        this.localidade = dados.localidade();
        this.uf = dados.uf();
        this.complemento = dados.complemento();
    }

    public void atualizarEndereco(DadosEndereco outroEndereco) {
        if(outroEndereco.logradouro()!= null) {
            this.logradouro = outroEndereco.logradouro();
        }
        if(outroEndereco.cep()!= null) {
            this.cep = outroEndereco.cep();
        }
        if(outroEndereco.bairro()!= null) {
            this.bairro = outroEndereco.bairro();
        }
        if(outroEndereco.localidade()!= null) {
            this.localidade = outroEndereco.localidade();
        }
        if(outroEndereco.uf()!= null) {
            this.uf = outroEndereco.uf();
        }
        if(outroEndereco.complemento()!= null) {
            this.complemento = outroEndereco.complemento();
        }
        if(outroEndereco.numero()!= null) {
            this.numero = outroEndereco.numero();
        }
    }
}
