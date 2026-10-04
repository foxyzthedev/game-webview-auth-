import { initializeApp } from "https://www.gstatic.com/firebasejs/12.1.0/firebase-app.js";
import {
  getAuth,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  updateProfile,
  onAuthStateChanged,
  signOut
} from "https://www.gstatic.com/firebasejs/12.1.0/firebase-auth.js";
import { getDatabase, ref, get, set } from "https://www.gstatic.com/firebasejs/12.1.0/firebase-database.js";
import { firebaseConfig } from "./firebase-config.js";

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getDatabase(app);
const $ = (selector) => document.querySelector(selector);

const params = new URLSearchParams(window.location.search);
const returnTo = params.get("returnTo");
const isValidReturnUrl = (value) => {
  try {
    const url = new URL(value);
    return ["http:", "https:"].includes(url.protocol);
  } catch {
    return false;
  }
};

let registerMode = false;

function togglePassword(input, button) {
  button.addEventListener("click", () => {
    input.type = input.type === "password" ? "text" : "password";
    button.setAttribute("aria-pressed", input.type === "text");
  });
}

togglePassword($("#password"), $("#show"));
togglePassword($("#confirmPassword"), $("#showConfirm"));

function setMessage(message = "", type = "error") {
  const msg = $("#msg");
  msg.textContent = message;
  msg.className = `msg ${type}`;
}

function setMode(register) {
  registerMode = register;
  $("#modeTag").textContent = register ? "CREATE ACCOUNT" : "AUTHENTICATION";
  $("#title").textContent = register ? "Crie sua conta" : "Bem-vindo de volta";
  $("#subtitle").textContent = register
    ? "Cadastre-se para entrar no game."
    : "Entre com sua conta para continuar.";
  $("#nameField").classList.toggle("hidden", !register);
  $("#confirmField").classList.toggle("hidden", !register);
  $("#nameInput").required = register;
  $("#confirmPassword").required = register;
  $("#enterText").textContent = register ? "CRIAR CONTA" : "ENTRAR";
  $("#switchMode").textContent = register ? "Já tenho uma conta" : "Ainda não tenho conta";
  setMessage();
}

$("#switchMode").addEventListener("click", () => setMode(!registerMode));

$("#form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const button = $("#enter");
  const email = $("#email").value.trim();
  const password = $("#password").value;
  button.disabled = true;
  setMessage(registerMode ? "Criando sua conta..." : "Verificando acesso...", "loading");

  try {
    let user;
    if (registerMode) {
      const name = $("#nameInput").value.trim();
      if (password !== $("#confirmPassword").value) {
        throw { code: "auth/passwords-dont-match" };
      }
      const credential = await createUserWithEmailAndPassword(auth, email, password);
      user = credential.user;
      await updateProfile(user, { displayName: name });
      await set(ref(db, `profiles/${user.uid}`), {
        email: user.email || email,
        displayName: name,
        createdAt: Date.now()
      });
    } else {
      const credential = await signInWithEmailAndPassword(auth, email, password);
      user = credential.user;
      const profile = ref(db, `profiles/${user.uid}`);
      const snapshot = await get(profile);
      if (!snapshot.exists()) {
        await set(profile, {
          email: user.email || email,
          displayName: user.displayName || "",
          createdAt: Date.now()
        });
      }
    }
    setMessage(registerMode ? "Conta criada. Redirecionando..." : "Login realizado.", "success");
    if (isValidReturnUrl(returnTo)) {
      window.setTimeout(() => { window.location.assign(returnTo); }, 500);
    }
  } catch (error) {
    const errors = {
      "auth/email-already-in-use": "Este e-mail já possui uma conta.",
      "auth/invalid-credential": "E-mail ou senha incorretos.",
      "auth/invalid-email": "Digite um e-mail válido.",
      "auth/weak-password": "A senha precisa ter pelo menos 6 caracteres.",
      "auth/passwords-dont-match": "As senhas não são iguais.",
      "auth/too-many-requests": "Muitas tentativas. Aguarde um pouco.",
      "auth/network-request-failed": "Falha de conexão. Verifique sua internet."
    };
    setMessage(errors[error.code] || "Não foi possível concluir a operação.");
  } finally {
    button.disabled = false;
  }
});

$("#logout").addEventListener("click", () => signOut(auth));

onAuthStateChanged(auth, async (user) => {
  if (!user) {
    $("#login").classList.remove("hidden");
    $("#user").classList.add("hidden");
    return;
  }
  $("#login").classList.add("hidden");
  $("#user").classList.remove("hidden");
  const snapshot = await get(ref(db, `profiles/${user.uid}`));
  const data = snapshot.val() || {};
  const name = data.displayName || user.displayName || (user.email || "Usuário").split("@")[0];
  $("#hello").textContent = `Olá, ${name}`;
  $("#name").textContent = name;
  $("#mail").textContent = user.email || "";
  $("#avatar").textContent = name[0].toUpperCase();
  if (isValidReturnUrl(returnTo)) {
    const link = $("#gameLink");
    link.href = returnTo;
    link.classList.remove("hidden");
  }
});
