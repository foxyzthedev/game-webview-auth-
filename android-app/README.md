# Foxyz Login Android — app novo

Este é um app Android criado do zero. Ele não altera o APK original e não reutiliza o Manifest binário baixado do MediaFire.

## O que ele faz

- Abre `https://loginpagefoxyzbackend.vercel.app/` em um WebView;
- Passa `returnTo=foxyz://auth/callback`;
- Recebe o callback após login/cadastro;
- envia o ID token a um endpoint HTTPS de validação;
- retorna `foxyz_uid` para a Activity chamadora;
- fecha a tela somente depois que o backend responde HTTP 2xx.
- mostra um aviso azul quando o token é recebido/validado;
- mostra um aviso vermelho quando o token não chega ou é recusado;
- esconde o aviso automaticamente após 5 segundos.

## Identidade do app

```text
package: com.foxyz.login
callback: foxyz://auth/callback
```

## Compilar

Abra a pasta `android-app` no Android Studio e execute **Build > Make Project**. O app precisa de Android SDK 35 e JDK 17.

O app já está configurado para usar este endpoint HTTPS:

```kotlin
https://loginpagefoxyzbackend.vercel.app/api/session/verify
```

O endpoint valida o ID token no Firebase antes de responder HTTP 2xx. Se a validação falhar, o app não fecha o WebView.

## Segurança

O token recebido é um ID token Firebase. Antes de liberar dados do jogo, envie-o por HTTPS ao seu backend e valide com Firebase Admin SDK. Não grave senha no app e não confie apenas no `uid`.

Este projeto é um cliente WebView independente. Ele não injeta código no Free Fire, não altera APK de terceiros e não falsifica tokens de Facebook.
