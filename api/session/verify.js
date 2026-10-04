const FIREBASE_WEB_API_KEY = process.env.FIREBASE_WEB_API_KEY || "AIzaSyCC91U9UH8tbCHt-7ZP8oZI2BfrVXR6W90";

export default async function handler(request, response) {
  response.setHeader("Access-Control-Allow-Origin", "*");
  response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
  response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");

  if (request.method === "OPTIONS") return response.status(204).end();
  if (request.method !== "POST") {
    return response.status(405).json({ ok: false, message: "Método não permitido" });
  }

  const authorization = request.headers.authorization || "";
  const idToken = authorization.startsWith("Bearer ")
    ? authorization.slice("Bearer ".length).trim()
    : "";

  if (!idToken) {
    return response.status(401).json({ ok: false, message: "Token ausente" });
  }

  try {
    const firebaseResponse = await fetch(
      `https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=${FIREBASE_WEB_API_KEY}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idToken })
      }
    );

    const payload = await firebaseResponse.json();
    if (!firebaseResponse.ok || !payload.users?.[0]?.localId) {
      return response.status(401).json({ ok: false, message: "Token inválido ou expirado" });
    }

    const user = payload.users[0];
    return response.status(200).json({
      ok: true,
      uid: user.localId,
      email: user.email || null
    });
  } catch {
    return response.status(502).json({ ok: false, message: "Falha ao validar sessão" });
  }
}
