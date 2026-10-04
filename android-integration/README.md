# Integração Android/WebView Foxyz

Este diretório é um exemplo mínimo para o game abrir a página Foxyz e fechar o WebView quando o login terminar.

## URL de login

```text
https://loginpagefoxyzbackend.vercel.app/?returnTo=foxyz%3A%2F%2Fauth%2Fcallback
```

Após um login ou cadastro bem-sucedido, a página redireciona para:

```text
foxyz://auth/callback#status=success&uid=...&token=...
```

O token é um **ID token real do Firebase**, não um token inventado. O game deve enviá-lo ao seu backend por HTTPS. O backend precisa validar o token com o Firebase Admin SDK antes de liberar o jogador.

## Como usar

1. Copie o `AndroidManifest.xml` para o Manifest do game.
2. Copie/adapte `MainActivity.kt` para a Activity que abre o WebView.
3. Mantenha o intent-filter com `foxyz://auth/callback`.
4. Troque `onFoxyzLoginSuccess` pela chamada ao seu backend.
5. Só chame `finish()` depois que o backend confirmar o token.
6. Troque `package com.foxyz.game` pelo package real do game.

## Observação de segurança

O esquema customizado funciona para teste e integração inicial. Em produção, prefira Android App Links com HTTPS e domínio verificado, pois esquemas customizados podem ser registrados por outro aplicativo. Nunca grave senha no game e nunca confie apenas no `uid` recebido da URL.
