# Foxyz Login Android — app novo

Este é um app Android criado do zero. Ele não altera o APK original e não reutiliza o Manifest binário baixado do MediaFire.

## O que ele faz

- Abre `https://loginpagefoxyzbackend.vercel.app/` em um WebView;
- Passa `returnTo=foxyz://auth/callback`;
- Recebe o callback após login/cadastro;
- envia o ID token a um endpoint HTTPS de validação;
- retorna `foxyz_uid` para a Activity chamadora;
- fecha a tela somente depois que o backend responde HTTP 2xx.

## Identidade do app

```text
package: com.foxyz.login
callback: foxyz://auth/callback
```

## Compilar

Abra a pasta `android-app` no Android Studio e execute **Build > Make Project**. O app precisa de Android SDK 35 e JDK 17.

Antes de compilar, edite `MainActivity.kt` e troque:

```kotlin
https://SEU-BACKEND.example/session/verify
```

pelo endpoint HTTPS que usa o exemplo `verify-token.example.js`. Sem essa configuração, o app mostra uma mensagem e não fecha o WebView de propósito.

## Segurança

O token recebido é um ID token Firebase. Antes de liberar dados do jogo, envie-o por HTTPS ao seu backend e valide com Firebase Admin SDK. Não grave senha no app e não confie apenas no `uid`.

Este projeto é um cliente WebView independente. Ele não injeta código no Free Fire, não altera APK de terceiros e não falsifica tokens de Facebook.
