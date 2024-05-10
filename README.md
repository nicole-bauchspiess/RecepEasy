<h1>
API REST para criação de posts de receitas culinárias por meio do ChatGPT </h1>

<br> 
Com objetivo de gerar receitas culinárias, o usuário pode informar alguns ingredientes e ao consumir a API do chatGPT, é retornado ao usuário os ingredientes, modo de preparo e o sabor da receita. O usuário tem a opção de deixar esta receita privada ou pública e para as públicas, outros usuários conseguem interagir com este post por meio de curtidas e comentários. A fim de facilitar o cadastro de usuários está sendo consumido a api do viaceps para que ao informar o CEP do endereço, este já seja convertido para entidade JPA. 

<br>
Somado a isso, por meio do Spring security foi validado a autenticação e autorização do usuário para poder fazer as requisições na API  e geração do token JWT, visando uma aplicação stateless (não armazena as informações se o usuário está logado ou não) mais segura. Foi feita a documentação através do SpringDoc utilizando o swagger e testes unitários e de integração utilizando o JUnit e Mockito.  

<br>
<br>

## Instalação e execução: 

- Instalação do postgres, versão 14.11: 
https://www.enterprisedb.com/downloads/postgres-postgresql-downloads
- Criação do banco de dados: 'CREATE DATABASE recepEasy';
- Clonar o repositório: https://git.senior.com.br/testes-learning/godev-nicole-bauchspiess.git
- No explorador de arquivo: na pasta que foi gerada ao clonar o repositório é possível selecionar com o botão direito do mouse e clicar sobre "abrir terminal" 
- Limpar o maven:  mvn clean package
- Executar a aplicacao: java "-Dspring.profiles.active=prod" -DDATASOURCE_URL=jdbc:postgresql://localhost/recepEasy -DDATASOURCE_USERNAME=postgres -DDATASOURCE_PASSWORD=sa2008++ -jar .\target\api-0.0.1-SNAPSHOT.jar
- A aplicação estará disponível em http://localhost:8080



<br>

## Documentação SpringDoc:
- Após a execução do projeto rodar a url: 
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/v3/api-docs

<br>

## Tecnologias utilizadas:
- Linguagem: Java 17
- Framework: Spring Boot 3.0.0
- JPA/Hibernate 
- Banco de dados relacional: Postgres 14
- SGBD: PG Admin 4
- Testes da API: Postman
- Gerenciador de dependencias: Maven


## Dependencias Maven:
- Spring Security 
- Sprin Web
- Spring Data JPA
- Devtools 
- Postgres 
- SpringDoc
- Validation
- Lombok
- Flyway
- Java-JWT: https://github.com/auth0/java-jwt


<br>

## Endpoint básicos:

 USUARIOS:
- POST/PUT: localhost:8080/usuarios
- DELETE: localhost:8080/usuarios/{id}

RECEITAS:
- GET/POST/PUT: localhost:8080/receitas
- GET: localhost:8080/receitas/top/{pageSize} -> Pega as receitas mais curtidas em que a quantdade listada é definida na url
- GET: localhost:8080/receitas/usuario/{id} -> Se o usuário passado como parâmetro for o mesmo usuário que o logado: lista as receitas publicas e privadas, se não: lista somente as receitas públicas
- GET: localhost:8080/receitas/{id}/detalhes -> Lista a quantidade de curtidas da receita, bem como os comentários que teve
- DELETE: localhost:8080/receitas/{id}

INTERAÇÕES:
- POST: localhost:8080/receitas/{id} -> inserção de curtidas e comentários, permitido somente para receitas públicas e ativas


LOGIN:
- POST: localhost:8080/login
- Ao enviar uma requisição post com esta url, gera-se um token cujo será usado nas outras requisições para identificar que o usuário está autenticado. 
Este token tem validade de 2 horas a partir de sua geração 





