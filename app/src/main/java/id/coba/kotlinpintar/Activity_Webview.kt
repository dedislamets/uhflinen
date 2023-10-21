package id.coba.kotlinpintar

import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import id.coba.kotlinpintar.Rest.ApiClient

class Activity_Webview : AppCompatActivity() {
    private lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val url = getIntent().getStringExtra("url")


        webView = findViewById(R.id.webView)
        webView.settings.setJavaScriptEnabled(true)
//        webView.addJavascriptInterface(JavascriptInterface(),"androidObj")
        webView.setWebChromeClient( WebChromeClient())

        if(url != null && url != ""){
            webView.loadUrl(ApiClient.BASE_WEB +  url)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {

                view?.loadUrl(url)

//                view?.addJavascriptInterface(JavascriptInterface(),"Android")
                return true
            }
        }

        getSupportActionBar()?.setTitle(getIntent().getStringExtra("title"))
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
    }

    private inner class JavascriptInterface
    {
        @android.webkit.JavascriptInterface
        fun showToast(text: String)
        {
            if(text.equals("ok")){
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        startActivity(Intent(this, ActivityNotification::class.java))
        this.finish()
    }
}