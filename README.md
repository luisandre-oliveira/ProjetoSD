# ProjetoSD

### Resumo
Neste projeto pede-se a implementação de um serviço de subscrição e difusão de publicações (posts)
sob a forma de um classe com a lógica de gestão dos canais em Java a ser utilizada por vários threads e
um par cliente-servidor em Java utilizando sockets.
A essência do serviço é permitir aos utilizadores ler e fazer publicações em canais.

### Funcionalidade da Lógica de Negócio
Deverá ser implementada numa classe Java a seguinte funcionalidade:
* T1. Criar um canal.
* T2. Fechar um canal, não sendo possível publicar mais nesse canal, mas ainda permitir consultar as
publicações já efetuadas. Caso haja clientes à espera de ler novas publicações no canal fechado,
estes deverão receber uma mensagem que indique que não haverá mais publicações para ler neste
canal.
* T3. Fazer uma publicação num canal.
* T4. Obter as listas de publicações num conjunto de canais.
* T5. Obter a próxima publicação num canal, ficando o cliente à espera que esta seja publicada.
* T6. Valorização: Subscrever um canal, sendo o cliFuncionalidade Cliente-Servidor

### Usando no servidor a classe desenvolvida anteriormente, deverá suportar a seguinte funcionalidade básica:
* S1. Autenticação de utilizador, dado o seu nome e palavra-passe. Sempre que um utilizador desejar
interagir com o serviço deverá estabelecer uma conexão e ser autenticado pelo servidor.
* S2. Dar aos utilizadores comuns acesso às funcionalidades T3 a T5, assumindo-se que o cliente é o
utilizador previamente identificado.
* S3. Dar a um super-utilizador acesso à funcionalidade T1 e T2 (criar e fechar canais).
* S4. Valorização: Permitir que um cliente execute outras operações enquanto se espera pela conclusão
duma operação, incluindo publicar ou obter publicações de outros canais.
* S5. Valorização: Dar aos utilizadores comuns acesso à funcionalidade T6, suportando a notificação
do cliente por parte do servidor quando for feita uma publicação em algum dos canais subscritos.

### Condições
Deverá ser disponibilizado um cliente que ofereça uma interface com o utilizador que permita suportar
a funcionalidade descrita acima. Este cliente deverá ser escrito em Java usando threads e sockets TCP.
O servidor deverá ser escrito também em Java, usando threads e sockets TCP, mantendo em memória
a informação relevante para suportar as funcionalidades acima descritas, receber conexões e input dos
clientes, bem como fazer chegar a estes a informação pretendida. O protocolo entre cliente e servidor
deverá ser num formato binário, através de código desenvolvido no trabalho, podendo recorrer apenas a
Data[Input|Output]Stream. Em resumo, devem ser usadas apenas as classes da plataforma Java utilizadas
na resolução dos guiões práticos.
Para o serviço não ficar vulnerável a clientes lentos, não deverá ter threads do servidor a escrever
em mais do que um socket, devendo as escritas serem feitas por threads associadas a esse socket.ente notificado sempre que houver uma nova publicação
nesse canal.
