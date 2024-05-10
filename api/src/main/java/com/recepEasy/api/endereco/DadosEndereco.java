package com.recepEasy.api.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosEndereco(
        String logradouro,
        @NotBlank
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
        String cep,
        String localidade,
        String bairro,
        String uf,
        String complemento,
        @NotBlank
        String numero)
    {
}
