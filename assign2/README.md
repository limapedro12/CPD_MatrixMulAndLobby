# Trabalho de Sistemas Distribuídos
## Computação Paralela e Distribuída

Este trabalho, no âmbito da unidade curricular Computação Paralela e Distribuída, tem como objetivo criar um sistema cliente-servidor utilizando sockets TCP em Java que permita com que vários utilizadores joguem o jogo "Quem acerta mais perto". Para isso, este sistema permite que os jogadores se autentiquem e, depois, se juntem a um de dois lobbies (Simple Lobby e Rank Lobby), que farão equipas com o número de jogadores da especificado anteriormente, de formas distintas. Uma vez formada a equipa, uma nova instância do Game é criada, o que permite com que estes utilizadores joguem o jogo entre eles.

## Compilação e Execução

Antes de executar qualquer outra ação, deve executar comando `make`, para compilar os ficheiros. 

Depois, para correr o servidor, deve executar o comando `make run_server`.

É possivel também iniciar o servidor custumizando a porta e o número de jogadores necessários para iniciar um jogo, com `make run_server PORT=<PORT> NUM_PLAYERS=<NUM_PLAYERS>`.

Se em vez disso quiser correr o cliente, deve executar o comando `make run_client`.

É possivel também correr o cliente customizando o host e a porta à qual se deseja conectar, com `make run_client HOST=<HOST> PORT=<PORT>`.

## Arquitetura do Servidor

### Server.java:

Esta classe representa o servidor. Inicialmente apenas existe uma thread que é responsável por aceitar novas conecções. Sempre que uma nova conecção é estabelecida, uma nova thread é criada, que trata de todas as mensagens provenientes desse socket.

Caso o servidor receba uma das seguintes mensagens através de um dos sockets, executa a respetiva ação.

`AUTH <username> <password>`

Ação: Autentica o jogador com username e password fornecidos. Se a autenticação for bem-sucedida, o servidor enviará uma mensagem com o token do jogador, que este deve utilizar nas mensagens seguintes, para se identificar. Caso o servidor detete que o jogador se encontrava num lobby ou num jogo, envia essa informação ao cliente para que este consiga restaurar o estado correspondente.

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

Esta classe representa um jogador. 

Além disso, nesta class é guardado, de forma estática, o conjunto de todos os jogadores que tenham interagido com o servidor recentemente. 

Em cada jogador é guardado, o seu username, o seu número de pontos, o seu token atual e o último socket através do qual o jogador comunicou com o servidor. A password apenas é guardada na base de dados.
Embora o socket seja guardado nesta class, o jogador pode enviar mensagens através qualquer socket (desde que devidamente identificadas com o respetivo token). Caso este seja diferente do socket guardado, o mesmo será atualizado.

Todas as mensagens enviadas para o jogador serão enviadas para o último socket guardado.

### SimpleLobby.java

Esta classe é a implementação do lobby simples. 
Qualquer jogador pode juntar-se a este lobby. Quando, no lobby, estiverem o número de jogadores necessário, é iniciado um jogo com os mesmos, independentemente da sua pontuação, e o lobby passa a estar vazio.

### RankLobby.java

Aqui é implementado o lobby por rank. 

Este lobby tenta criar jogos com jogadores cujos pontos sejam similares, mas esta condição vai sendo gradualmente relaxada, de modo que os jogadores não tenham que esperar eternamente por um jogo. 

Qualquer jogador pode juntar-se a este lobby. Ao fazê-lo, ser-lhe-á associado um raio de pesquisa de 0 pontos e a cada segundo o seu raio será incrementado em 5 pontos. 

Além disso, a cada segundo, o RankLobby cria todos os conjuntos em que parte do raio de pesquisa esteja contido em todos os outros elementos destes conjuntos.

Se algum destes conjuntos contiver o número de jogadores necessário, é iniciado um jogo com os mesmos, e estes jogadores são retirados do RankLobby.

Este processo é executado por uma thread dedicada apenas ao Rank Lobby.

Para criar os conjuntos, o programa ordena todos os valores de entrada e saida (sendo, por exemplo, um jogador com pontuação de 2000 pontos e um raio de 300, o seu valor de entrada é 2300 e o valor de saida 1700). De seguida iteramos pela lista ordenada, e sempre que um valor de entrada aparece, esse jogador é adicionado à lista de intervalos abertos, sempre que um valor de saida aparece, esse jogador é removido da lista de intervalos abertos e uma cópia deste conjunto é adicionado à lista de conjuntos cujos raios de pesquisa estejam contidos em todos os outros intervalos do grupo.

### Game.java

## Arquitetura do Cliente

A classe Client é o ponto de entrada principal do client side. Ela gerencia a lógica de comunicação com o servidor e as transições de estado da aplicação Cliente.

### Funcionalidades

#### Autenticação e Registo

1. Menu de Autenticação: 
 - O cliente apresenta um menu inicial onde o usuário pode escolher entre fazer login, registrar um novo usuário, continuar um jogo existente, ou sair da aplicação.
 - Login: O usuário insere seu nome de usuário e senha para autenticar.
 - Registo: O usuário pode registrar um novo nome de usuário e senha.
2. Menu Principal:
 - Após a autenticação, o usuário é apresentado com um menu principal onde pode escolher entrar em um lobby simples ou ranqueado, ou sair da aplicação.

### Exemplo de Execução

Ao iniciar a aplicação, o usuário verá o menu de autenticação com opções para login, registro, continuar um jogo ou sair. Dependendo da escolha, a aplicação irá solicitar as informações necessárias e comunicar-se com o servidor para processar a ação. Após a autenticação, o usuário pode escolher entre diferentes lobbies de jogos no menu principal.
 
## SSL

Todas as comunicações entre o servidor e cliente utilizam o protocolo Secure Sockets Layer (SSL), através da biblioteca SSLSocket, de modo a manter todas as mensagens seguras, principalmente as referentes à autenticação e registo dos jogadores.
