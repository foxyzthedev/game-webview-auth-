# Foxyz Private Project

Tela de login com Firebase Authentication (e-mail/senha) + Realtime Database.

## Configuração
1. Firebase Console > Authentication > Sign-in method > Email/Password: ativar.
2. Realtime Database > Rules: cole o conteúdo de `database.rules.json`.
3. Firebase Console > Project settings > Your apps > adicione/abra seu Web App.
4. Copie `apiKey`, `messagingSenderId` e `appId` para `firebase-config.js`.
5. Publique os arquivos no GitHub/Vercel.

A URL do RTDB já está configurada:
https://foxyz-private-project-default-rtdb.firebaseio.com

IMPORTANTE: nunca coloque uma Service Account JSON, private_key ou credencial administrativa no frontend/GitHub.
