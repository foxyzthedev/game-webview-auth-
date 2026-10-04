// Exemplo: endpoint HTTPS do seu backend para o game.
// Instale: npm i express firebase-admin
// Configure GOOGLE_APPLICATION_CREDENTIALS fora do repositório.

import express from "express";
import admin from "firebase-admin";

admin.initializeApp({
  credential: admin.credential.applicationDefault()
});

const app = express();
app.use(express.json());

app.post("/session/verify", async (req, res) => {
  const authorization = req.headers.authorization || "";
  const token = authorization.startsWith("Bearer ")
    ? authorization.slice("Bearer ".length)
    : "";

  if (!token) return res.status(401).json({ ok: false, message: "Token ausente" });

  try {
    const decoded = await admin.auth().verifyIdToken(token);
    const profile = await admin.database().ref(`profiles/${decoded.uid}`).get();
    return res.json({ ok: true, uid: decoded.uid, profile: profile.val() || {} });
  } catch {
    return res.status(401).json({ ok: false, message: "Token inválido ou expirado" });
  }
});

app.listen(process.env.PORT || 3000, "0.0.0.0", () => {
  console.log("Token verification API online");
});
