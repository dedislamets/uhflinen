@file:Suppress("DEPRECATION")

package id.coba.kotlinpintar

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.R.attr.name
import android.database.Cursor
import android.util.Log
import id.coba.kotlinpintar.InputDbHelper.BASE_URL
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.os.AsyncTask.execute
import android.R.attr.name
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.apache.http.HttpClientConnection
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


class NetworkConnection(private val context: Context, private val url: String): LiveData<Boolean>() {
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private  lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        cekAPI(url)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->{
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->{
                lollipopNetworkRequest()
            }
            else -> {
                context.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

//    override fun onInactive() {
//        super.onInactive()
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
//            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
//        }else{
//            context.unregisterReceiver(networkReceiver)
//        }
//    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private  fun lollipopNetworkRequest(){
        var requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)

        connectivityManager.registerNetworkCallback(
            requestBuilder.build(),
            connectivityManagerCallback()
        )
    }

    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
//                    postValue(true)
                    cekAPI(url)
                }
            }

            return networkCallback
        }else{
            throw IllegalAccessError("Error")
        }
    }

    private val networkReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            cekAPI(url)
        }
    }

    private fun updateConnection(){
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }

    private fun cekAPI(baseurl : String){
        val stringRequest = object : StringRequest(
            Method.GET, baseurl + "koneksi",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    if (obj.getBoolean("status")) {
                        postValue(true)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    postValue(false)
                }
            },
            Response.ErrorListener {
                postValue(false)
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    private fun isURLReachable() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        val mHelper = InputDbHelper(context)
        if (netInfo != null && netInfo.isConnected) {
            try {
                var cursor : Cursor =  mHelper.getSetting()
                var api : String = ""
                if (cursor.moveToFirst()) {
                    api = cursor.getString(cursor.getColumnIndex(BASE_URL))

                }


                val url = URL("http://192.168.100.9:8080/linen/api/koneksi")   // Insert Url
                val urlc = url.openConnection() as HttpURLConnection

                urlc.setConnectTimeout(10 * 1000)          // 10 s.
                urlc.connect()
                if (urlc.getResponseCode() === 200) {
                    Log.wtf("Connection", "Success !")
                    postValue(true)
                } else {
                    postValue(false)
                }
            } catch (e1: MalformedURLException) {
                postValue(false)
            } catch (e: IOException) {
                postValue(false)
            }

        }
        postValue(false)
    }
}