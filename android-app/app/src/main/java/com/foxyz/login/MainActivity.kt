package com.foxyz.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private val sessionVerifyUrl = "https://loginpagefoxyzbackend.vercel.app/api/session/verify"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setContentView(webView)
        configureWebView()
        loadFoxyzLogin()
    }

    private fun configureWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.setSupportZoom(false)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = interceptCallback(request.url)

            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                interceptCallback(Uri.parse(url))
        }
    }

    private fun loadFoxyzLogin() {
        val url = Uri.Builder()
            .scheme("https")
            .authority("loginpagefoxyzbackend.vercel.app")
            .appendQueryParameter("returnTo", "foxyz://auth/callback")
            .build()
        webView.loadUrl(url.toString())
    }

    private fun interceptCallback(uri: Uri): Boolean {
        if (uri.scheme != "foxyz" || uri.host != "auth" || uri.path != "/callback") {
            return false
        }

        val values = parseFragment(uri.fragment.orEmpty())
        if (values["status"] == "success" && !values["token"].isNullOrBlank()) {
            verifySessionAndClose(values["uid"].orEmpty(), values["token"].orEmpty())
        }
        return true
    }

    private fun verifySessionAndClose(uid: String, token: String) {
        Thread {
            val valid = runCatching {
                val connection = URL(sessionVerifyUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000
                connection.responseCode in 200..299
            }.getOrDefault(false)

            runOnUiThread {
                if (valid) {
                    val result = Intent().apply {
                        putExtra("foxyz_uid", uid)
                    }
                    setResult(RESULT_OK, result)
                    finish()
                } else {
                    webView.loadData(
                        "<h3>Não foi possível validar a sessão</h3><p>Tente entrar novamente.</p>",
                        "text/html",
                        "UTF-8"
                    )
                }
            }
        }.start()
    }

    private fun parseFragment(fragment: String): Map<String, String> =
        fragment.split('&')
            .mapNotNull { item ->
                val pair = item.split('=', limit = 2)
                if (pair.size == 2) pair[0] to Uri.decode(pair[1]) else null
            }
            .toMap()

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
