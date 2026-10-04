import { initializeApp } from "https://www.gstatic.com/firebasejs/12.1.0/firebase-app.js";
import { getAuth, signInWithEmailAndPassword, onAuthStateChanged, signOut }
from "https://www.gstatic.com/firebasejs/12.1.0/firebase-auth.js";
import { getDatabase, ref, get, set, serverTimestamp }
from "https://www.gstatic.com/firebasejs/12.1.0/firebase-database.js";
import { firebaseConfig } from "./firebase-config.js";

const app=initializeApp(firebaseConfig);
const auth=getAuth(app);
const db=getDatabase(app);

const $=s=>document.querySelector(s);
$("#show").onclick=()=>$("#password").type=$("#password").type==="password"?"text":"password";

$("#form").addEventListener("submit",async e=>{
 e.preventDefault();
 const btn=$("#enter"); const msg=$("#msg");
 btn.disabled=true; msg.textContent="Verificando acesso...";
 try{
   const cred=await signInWithEmailAndPassword(auth,$("#email").value.trim(),$("#password").value);
   const u=cred.user;
   const p=ref(db,"profiles/"+u.uid);
   const snap=await get(p);
   if(!snap.exists()){
     await set(p,{email:u.email||"",displayName:u.displayName||"",createdAt:Date.now()});
   }
   msg.textContent="";
 }catch(err){
   const errors={
    "auth/invalid-credential":"E-mail ou senha incorretos.",
    "auth/invalid-email":"Digite um e-mail válido.",
    "auth/too-many-requests":"Muitas tentativas. Aguarde um pouco.",
    "auth/network-request-failed":"Falha de conexão."
   };
   msg.textContent=errors[err.code]||"Não foi possível entrar.";
 }finally{btn.disabled=false}
});

$("#logout").onclick=()=>signOut(auth);

onAuthStateChanged(auth,async u=>{
 if(!u){$("#login").classList.remove("hidden");$("#user").classList.add("hidden");return}
 $("#login").classList.add("hidden");$("#user").classList.remove("hidden");
 const p=await get(ref(db,"profiles/"+u.uid)); const data=p.val()||{};
 const name=data.displayName||u.displayName||(u.email||"Usuário").split("@")[0];
 $("#hello").textContent="Olá, "+name;$("#name").textContent=name;$("#mail").textContent=u.email||"";
 $("#avatar").textContent=name[0].toUpperCase();
});
