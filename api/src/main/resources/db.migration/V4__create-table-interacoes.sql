create table interacoes(

    id serial not null,
    comentario varchar(255),
    curtida boolean,
    receita_id serial not null,
    usuario_id serial not null,
    primary key(id),
    foreign key (usuario_id) references usuarios(id),
    foreign key (receita_id) references receitas(id)
);