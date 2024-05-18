# Trabalho de Sistemas Distribuidos
## Computação Paralela e Distribuída

< Introdução >

## Arquitetura do Servidor


## Client

A classe Client é o ponto de entrada principal do client side. Ela gerencia a lógica de comunicação com o servidor e as transições de estado da aplicação Cliente. 
### Funcionalidades
Autenticação e Registo

1. Menu de Autenticação: 
 - O cliente apresenta um menu inicial onde o usuário pode escolher entre fazer login, registrar um novo usuário, continuar um jogo existente, ou sair da aplicação.
 - Login: O usuário insere seu nome de usuário e senha para autenticar.
- Registo: O usuário pode registrar um novo nome de usuário e senha.
2. Menu Principal:
 - Após a autenticação, o usuário é apresentado com um menu principal onde pode escolher entrar em um lobby simples ou ranqueado, ou sair da aplicação.
 

### Exemplo de Execução
Ao iniciar a aplicação, o usuário verá o menu de autenticação com opções para login, registro, continuar um jogo ou sair. Dependendo da escolha, a aplicação irá solicitar as informações necessárias e comunicar-se com o servidor para processar a ação. Após a autenticação, o usuário pode escolher entre diferentes lobbies de jogos no menu principal.
