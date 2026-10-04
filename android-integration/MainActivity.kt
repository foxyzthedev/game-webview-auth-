package com.foxyz.game

import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = handleCallback(request.url)

            @Deprecated("Deprecated in API 24")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                handleCallback(Uri.parse(url))
        }

        val loginUrl = Uri.Builder()
            .scheme("https")
            .authority("loginpagefoxyzbackend.vercel.app")
            .appendQueryParameter("returnTo", "foxyz://auth/callback")
            .build()
        webView.loadUrl(loginUrl.toString())
    }

    private fun handleCallback(uri: Uri): Boolean {
        if (uri.scheme != "foxyz" || uri.host != "auth" || uri.path != "/callback") {
            return false
        }

        val fragment = uri.fragment ?: return true
        val values = fragment.split('&')
            .mapNotNull { item ->
                val parts = item.split('=', limit = 2)
                if (parts.size == 2) parts[0] to Uri.decode(parts[1]) else null
            }
            .toMap()

        val token = values["token"]
        val uid = values["uid"]
        if (values["status"] == "success" && !token.isNullOrBlank() && !uid.isNullOrBlank()) {
            // IMPORTANTE: envie o token ao seu backend por HTTPS para validar
            // com o Firebase Admin SDK antes de liberar recursos do jogo.
            onFoxyzLoginSuccess(uid, token)
        }
        return true
    }

    private fun onFoxyzLoginSuccess(uid: String, idToken: String) {
        // Substitua este bloco pela chamada ao seu backend/estado do jogo.
        // Só depois da validação no servidor feche a tela:
        setResult(RESULT_OK)
        finish()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
