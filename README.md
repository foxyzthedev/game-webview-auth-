# Foxyz Private Project

Tela de login e criação de conta com Firebase Authentication (e-mail/senha) + Realtime Database.

## Link publicado

- **Vercel:** https://loginpagefoxyzbackend.vercel.app

## Como abrir pelo game e voltar automaticamente

Abra a página de login passando o endereço do game no parâmetro `returnTo`:

```text
https://loginpagefoxyzbackend.vercel.app/?returnTo=https%3A%2F%2FSEU-GAME.com
```

Depois do login ou cadastro, o usuário é redirecionado para esse endereço. Se o endereço não for informado, a página mostra apenas o painel autenticado. O parâmetro aceita somente URLs `http://` ou `https://`.

Exemplo em JavaScript no game:

```js
const loginUrl = new URL("https://loginpagefoxyzbackend.vercel.app/");
loginUrl.searchParams.set("returnTo", window.location.href);
window.location.href = loginUrl.toString();
```

## Configuração do Firebase

1. Firebase Console > Authentication > Sign-in method > ative **Email/Password**.
2. Realtime Database > Rules: cole o conteúdo de `database.rules.json`.
3. Firebase Console > Project settings > Your apps > confira o Web App.
4. Publique os arquivos no GitHub/Vercel. O deploy da Vercel pode ser automático após o push na branch `main`.

A URL do RTDB já está configurada:

https://foxyz-private-project-default-rtdb.firebaseio.com

> Nunca coloque uma Service Account JSON, `private_key` ou credencial administrativa no frontend/GitHub.
