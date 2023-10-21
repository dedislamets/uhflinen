package id.coba.kotlinpintar

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.coba.kotlinpintar.InputDbHelper.ID_USER
import id.coba.kotlinpintar.Rest.ApiClient.BASE_WEB
import java.security.Permissions

class Activity_Inspeksi : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var pref : SharedPreferences

    var uploadMessage:ValueCallback<Array<Uri>>? = null
    var mUploadMessage: ValueCallback<Uri>? = null
    private val FILECHOOSER_RESULTCODE = 1
    val REQUEST_SELECT_FILE = 100
    var url_string : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_request)
        pref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        val user_id = pref.getInt(ID_USER, 0)
        url_string = getIntent().getStringExtra("url")

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.setGeolocationEnabled(true)
        webView.addJavascriptInterface(JavascriptInterface(),"androidObj")
        webView.webChromeClient = WebChromeClient()

        if(url_string != null && url_string != ""){
            webView.loadUrl(BASE_WEB + url_string)
        }else {
            webView.loadUrl(BASE_WEB + "pengawasan/penilaian/?user_id=" + user_id )
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                val css = ".az-header{display: none;}"
                val js = "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                webView.evaluateJavascript(js,null)
                super.onPageFinished(view, url)
            }


        }

        webView?.webChromeClient = object:WebChromeClient() {

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                Log.d("alert", message)
                val dialogBuilder = AlertDialog.Builder(applicationContext)

                dialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        result.confirm()
                    }

                val alert = dialogBuilder.create()
                alert.show()

                return true
            }

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor

            fun openFileChooser(uploadMsg : ValueCallback<Uri>, acceptType:String) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
            }

            // For Lollipop 5.0+ Devices
            override fun onShowFileChooser(webView: WebView, filePathCallback:ValueCallback<Array<Uri>>, fileChooserParams:FileChooserParams):Boolean {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if (uploadMessage != null) {
                        uploadMessage?.onReceiveValue(null)
                        uploadMessage = null
                    }
                    uploadMessage = filePathCallback
                    val intent = fileChooserParams.createIntent()
                    try {
                        startActivityForResult(intent, REQUEST_SELECT_FILE)
                    } catch (e:ActivityNotFoundException) {
                        uploadMessage = null
                        Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                        return false
                    }
                    return true
                }else{
                    return false
                }

            }

            //For Android 4.1 only
            fun openFileChooser(uploadMsg:ValueCallback<Uri>, acceptType:String, capture:String) {
                mUploadMessage = uploadMsg
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE)
            }

            fun openFileChooser(uploadMsg:ValueCallback<Uri>) {
                //filePermission()
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
            }
        }

        getSupportActionBar()?.setTitle(getIntent().getStringExtra("title"))
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if(requestCode == REQUEST_SELECT_FILE){
                if(uploadMessage != null){
                    uploadMessage?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,data))
                    uploadMessage = null
                }
            }
        }else if(requestCode == FILECHOOSER_RESULTCODE){
            if(mUploadMessage!=null){
                var result = data?.data
                mUploadMessage?.onReceiveValue(result)
                mUploadMessage = null
            }
        }else{
            Toast.makeText(applicationContext,"Failed to open file uploader, please check app permissions.",Toast.LENGTH_LONG).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
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
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

}