package com.srl.infinitegrowth

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.util.Log
import android.webkit.ConsoleMessage
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.OnBackPressedCallback
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.webkit.WebSettings

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 移除标题栏
        supportActionBar?.hide()
        
        // 使用新的 WindowInsetsController API
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.statusBars())
            isAppearanceLightStatusBars = false // 状态栏文字颜色，true为深色，false为浅色
        }
        
        setContentView(R.layout.activity_main)

        // 设置返回键处理
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        webView = findViewById(R.id.webView)
        
        // 开启调试
        WebView.setWebContentsDebuggingEnabled(true)
        
        // 配置 WebView 设置
        webView.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            domStorageEnabled = true
            
            // 添加以下配置
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true  // 允许跨域访问
            
            // 如果需要，可以添加以下配置
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            
            // 设置缓存模式
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        
        // 设置 WebViewClient
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("WebView", "页面加载完成: $url")
                // 检查页面内容
                view?.evaluateJavascript(
                    "(function() { return document.documentElement.outerHTML; })();"
                ) { html ->
                    Log.d("WebView", "页面内容: $html")
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                Log.e("WebView", """
                    加载错误:
                    URL: ${request?.url}
                    错误码: ${error?.errorCode}
                    描述: ${error?.description}
                """.trimIndent())
            }
        }

        // 设置 WebChromeClient 用于调试
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.apply {
                    when (messageLevel()) {
                        ConsoleMessage.MessageLevel.ERROR -> {
                            Log.e("WebView Console", "${message()} at ${sourceId()}:${lineNumber()}")
                        }
                        ConsoleMessage.MessageLevel.WARNING -> {
                            Log.w("WebView Console", "${message()} at ${sourceId()}:${lineNumber()}")
                        }
                        else -> {
                            Log.d("WebView Console", "${message()} at ${sourceId()}:${lineNumber()}")
                        }
                    }
                }
                return true
            }
        }
        
        try {
            // 检查 assets 中的文件
            val files = assets.list("")
            files?.forEach { 
                Log.d("Assets", "Found file: $it")
            }
            
            // 检查 dist 目录
            val distFiles = assets.list("dist")
            distFiles?.forEach {
                Log.d("Assets", "dist/$it")
            }
            
            // 检查 dist/assets 目录
            val assetFiles = assets.list("dist/assets")
            assetFiles?.forEach {
                Log.d("Assets", "dist/assets/$it")
            }
            
            webView.loadUrl("file:///android_asset/dist/index.html")
        } catch (e: Exception) {
            Log.e("WebView", "加载页面错误", e)
            e.printStackTrace()
        }
    }
} 