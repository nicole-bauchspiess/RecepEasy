create table endereco(

    endereco_id serial not null,
    logradouro varchar(255) not null,
    bairro varchar(100) not null,
    complemento varchar(100) not null,
    localidade varchar(255) not null,
    cep varchar(10) not null,
    uf varchar(2) not null ,
    primary key(endereco_id)
);