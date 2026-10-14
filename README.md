🦊 Foxyz Private Project


Sistema de login e criação de contas para jogos e aplicações Web, utilizando Firebase Authentication e Realtime Database.


⚙️ Como funciona?


O usuário acessa a página, faz login com e-mail e senha ou cria uma nova conta. O Firebase Authentication verifica as credenciais e autentica o usuário.


Após a autenticação, a página pode redirecionar o usuário para o jogo por meio do parâmetro returnTo.


O projeto também pode ser integrado a jogos Android usando WebView e um callback personalizado. Nesse caso, o backend deve validar o ID token do Firebase antes de liberar o acesso ao jogo.


🔥 Configuração




Crie um projeto no Firebase Console.


Ative o login por e-mail e senha.


Configure o Realtime Database e suas regras de segurança.


Adicione as configurações do Firebase ao projeto.


Publique na Vercel.




🌐 Projeto online


https://loginpagefoxyzbackend.vercel.app


🛡️ Segurança


A configuração Web do Firebase pode ser pública, mas as regras do banco devem proteger os dados dos usuários. Nunca publique chaves privadas, senhas ou credenciais administrativas.


Desenvolvido por Foxyz.


