# Trabalho de Sistemas Distribuidos
## Computação Paralela e Distribuída

Este trabalho, no ambito da unidade curricular Computação Paralela e Distribuida, tem como objetivo criar um sistem cliente-servidor utilizando sockets TCP em java que permita com que vários utlizadores joguem o jogo "Quem acerta mais perto". Para isso, este sistema permite que os jogadores se autentiquem e, depois, se juntem a um de dois lobbys (Simple Lobby e Rank Lobby), que farão equipas de X jogadores, de formas distintas. Uma vez formada a equipa, uma nova instância do Game é criada, o que permite com que estes X utilizadores joguem o jogo entre eles.

## Makefile

Antes de executar qualquer outra ação, deve executar comando `make`, para compilar os ficheiros. 

Depois, para correr o servidor, deve executar o comando `make run_server`. 

Se em vez disso quiser correr o cliente, deve executar o comando `make run_client`.

## Arquitetura do Servidor

### Server.java:
Esta class representa o servidor. Inicialmente apenas existe uma thread que é responsável por aceitar novas conecções. Sempre que uma nova conecção é estabelecida, uma nova thread é criada, que trata de todas as mensagens provenientes desse socket.

Caso o servidor receba uma das seguintes mensgens através de um dos sockets, executa a respetiva ação.

`HELLO <token>`

Ação: Restaura a sessão do jogador com base no token fornecido. Se o token for válido, o jogador receberá uma mensagem "Session restored.".

`AUTH <username> <password>`

Ação: Autentica o jogador com username e password fornecidos. Se a autenticação for bem-sucedida, o servidor enviará uma mensagem com o token do jogador, que este deve utilizar nas mensagens seguintes, para se identificar.

`REGISTER <username> <password>`

Ação: Regista um novo jogador com username e password fornecidos.

`SIMPLE <token>`

Ação: Adiciona o jogador ao lobby simples, se o jogador não estiver num lobby, nem num jogo.

`RANK <token>`

Ação: Adiciona o jogador ao rank lobby, se o jogador não estiver num lobby, nem num jogo.

`LEAVE_LOBBY <token>`

Ação: Caso o jogador esteja num lobby, remove-o do mesmo.

`POINTS <token>`

Ação: Devolve o número de pontos que o jogador tem atualmente.

`PLAY <token> <guess>`

Ação: Caso o jogador esteja num jogo, define o valor indicado como o seu guess na jogada atual.

Caso o servidor receba uma outra mensagem que não esteja listada a cima, o servidor responderá com `ERROR: Command: Invalid command.`.

### Player.java
Esta class representa um jogador. 

Além disso nesta class é guardado, de forma estática, o conjunto de todos os jogadores que tenham interagido com o servidor recentemente. Caso um jogador esteja inativo por X tempo, é eliminado deste conjunto, não podendo mais utilizar o seu token atual e tendo, por isso, que efetuar novamente login.

Em cada jogador é guardado, o seu username, o seu número de pontos, o seu token atual e o último socket através do qual o jogador comunicou com o servidor. A password apenas é guardada na base de dados.
Embora o socket seja guardado nesta class, o jogador pode enviar mensagens através qualquer socket (desde que devidamente identificadas com o respetivo token). Caso este seja diferente do socket guardado, o mesmo será atualizado.

Todas as mensagens enviadas para o jogador serão enviadas para o último socket guardado.

### SimpleLobby.java
Esta class é a implementação do lobby simples. 
Qualquer jogador pode juntar-se a este lobby. Quando, no lobby, estiverem X jogadores, é iniciado um jogo com os mesmos, independentemente da sua pontuação, e o lobby passa a estar vazio.

### RankLobby.java
Aqui é implementado o lobby por rank. 

Este lobby tenta criar jogos com jogadores cujos pontos sejam similares, mas esta condição vai sendo gradualmente relaxada, de modo  a que os jogadores não tenham que esperar eternamente por um jogo. 

Qualquer jogador pode juntar-se a este lobby. Ao fazê-lo, ser-lhe-á associado um raio de pesquisa de 0 pontos e a cada segundo o seu raio será incrementado em X pontos. 

Além disso, a cada segundo, o RankLobby cria todos os conjuntos possiveis, em que parte do raio de pesquisa de todos os elementos o conjunto esteja contido no raio de pesquisa de todos os outros elementos.

Se algum destes conjuntos contiver X jogadores, é iniciado um jogo com os mesmos, e estes jogadores são retirados do RankLobby.

### Game.java

## Arquitetura do Cliente

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
