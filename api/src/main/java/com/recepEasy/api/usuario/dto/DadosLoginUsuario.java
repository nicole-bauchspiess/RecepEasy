package com.recepEasy.api.usuario.dto;

import lombok.Getter;

@Getter
public class DadosLoginUsuario {

        private String login;
        private String senha;

        public DadosLoginUsuario(String login, String senha){
          if(login.contains("'") || senha.contains("'")){
                  throw new IllegalArgumentException("Credenciais não permitem aspas");
          }
          this.login = login;
          this.senha = senha;
        }
}
