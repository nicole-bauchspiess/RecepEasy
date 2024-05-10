package com.recepEasy.api.endereco;

import com.google.gson.Gson;
import com.recepEasy.api.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EnderecoService {

    Gson gson = new Gson();

    @Autowired
    private EnderecoRepository enderecoRepository;

    public Endereco buscaEndereco(String cep){
        URI endereco = URI.create("https://viacep.com.br/ws/" + cep + "/json");
        HttpRequest request = HttpRequest.newBuilder().uri(endereco).build();


        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), Endereco.class);
        } catch (Exception  e) {
            throw new RuntimeException("Nao foi possível obter esse cep");
        }
    }

    public void inserir(Endereco endereco){
       enderecoRepository.save(endereco);
    }

    public Endereco consultaEnderecoViaCeps(String cep, String numero, String complemento){
        Endereco endereco = buscaEndereco(cep);
        endereco.setNumero(numero);
        endereco.setComplemento(complemento);

        if(endereco.getLocalidade()==null|| endereco.getLogradouro()==null|| endereco.getBairro() == null){
            throw new NotFoundException("Não foi possível localizar o endereço, insira os dados manualmente");
        }
        return endereco;
    }
}

