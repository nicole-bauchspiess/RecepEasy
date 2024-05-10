create table usuarios(

    id serial not null,
    nome varchar(255) not null,
    email varchar(255) not null unique,
    telefone varchar(20) not null unique,
    cpf varchar(20) not null unique,
    login varchar(20) not null unique,
    senha varchar(20) not null,
    ativo boolean not null ,
    endereco_id serial not null unique,
    nascimento date not null,
    primary key(id),
    foreign key (endereco_id) references endereco(endereco_id)
);