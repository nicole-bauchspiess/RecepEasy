create table receitas(

    id serial not null,
    ativo boolean not null,
    ingredientes text not null,
    modo_preparo text not null,
    nome varchar(100) not null,
    sabor varchar(10) not null,
    qtd_curtidas int not null,
    privado boolean not null,
    usuario_id serial,
    primary key(id),
    foreign key (usuario_id) references usuarios(id)
);