package com.foxyz.login

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var statusBanner: TextView
    private val mainHandler = Handler(Looper.getMainLooper())
    private val sessionVerifyUrl = "https://loginpagefoxyzbackend.vercel.app/api/session/verify"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        statusBanner = TextView(this).apply {
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(12), dp(18), dp(12))
            alpha = 0f
            visibility = TextView.GONE
        }

        val root = FrameLayout(this)
        root.addView(webView)
        root.addView(statusBanner, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP
        ).apply {
            setMargins(dp(14), dp(14), dp(14), 0)
        })
        setContentView(root)
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
            showStatus("Token recebido • validando acesso…", false)
            verifySessionAndClose(values["uid"].orEmpty(), values["token"].orEmpty())
        } else {
            showStatus("Token não recebido • login não concluído", true)
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
                    showStatus("Token validado • acesso liberado", false)
                    val result = Intent().apply { putExtra("foxyz_uid", uid) }
                    setResult(RESULT_OK, result)
                    mainHandler.postDelayed({ finish() }, 700)
                } else {
                    showStatus("Token recusado • tente entrar novamente", true)
                    webView.loadData(
                        "<h3>Não foi possível validar a sessão</h3><p>Tente entrar novamente.</p>",
                        "text/html",
                        "UTF-8"
                    )
                }
            }
        }.start()
    }

    private fun showStatus(message: String, error: Boolean) {
        statusBanner.text = message
        statusBanner.background = GradientDrawable().apply {
            cornerRadius = dp(18).toFloat()
            setColor(if (error) Color.rgb(150, 35, 55) else Color.rgb(20, 105, 190))
        }
        statusBanner.visibility = TextView.VISIBLE
        statusBanner.animate().alpha(1f).translationY(0f).setDuration(180).start()
        mainHandler.removeCallbacksAndMessages(null)
        mainHandler.postDelayed({
            statusBanner.animate().alpha(0f).translationY(-dp(12).toFloat()).setDuration(220)
                .withEndAction { statusBanner.visibility = TextView.GONE }
                .start()
        }, 5_000)
    }

    private fun parseFragment(fragment: String): Map<String, String> =
        fragment.split('&').mapNotNull { item ->
            val pair = item.split('=', limit = 2)
            if (pair.size == 2) pair[0] to Uri.decode(pair[1]) else null
        }.toMap()

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        webView.destroy()
        super.onDestroy()
    }
}
