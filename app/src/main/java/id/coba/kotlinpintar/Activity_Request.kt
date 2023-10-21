package id.coba.kotlinpintar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.coba.kotlinpintar.Rest.ApiClient.BASE_WEB

class Activity_Request : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var pref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_request)

        val no_request = getIntent().getStringExtra("no_request")
        pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        webView = findViewById(R.id.webView) as WebView
        webView.settings.setJavaScriptEnabled(true)
        webView.addJavascriptInterface(JavascriptInterface(),"androidObj")
        webView.setWebChromeClient( WebChromeClient())

        if(no_request != null && no_request != ""){
            webView.loadUrl(BASE_WEB + "listrequest/edit/" + no_request)
        }else {
            webView.loadUrl(BASE_WEB + "listrequest/create?nama=" + pref.getString(InputDbHelper.NAMA_USER, "Nama User"))
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

        getSupportActionBar()?.setTitle("REQUEST LINEN")
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
//            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()

//            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        startActivity(Intent(this, ListRequestActivity::class.java))
        this.finish()
    }

}