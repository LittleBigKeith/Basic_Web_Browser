package com.example.webbrowser

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlText = findViewById<EditText>(R.id.URL)
        val button = findViewById<Button>(R.id.button)
        val webView = findViewById<WebView>(R.id.webView)



        button.setOnClickListener {
            if (isNetworkAvailable(this)) {
                try {
                    // Get the URL from editText to be executed
                    val webSite = urlText.text.toString()
                    webView.webViewClient = WebViewClient()
                    webView.loadUrl(webSite)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else {
                val toast = Toast.makeText(this, "You're offline! Please check your internet connection", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

    }

    //method for checking connectivity
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // SDK version ?= 28/M
            val hasActiveNetwork = connectivityManager.activeNetwork?: return false
            val hasConnectivity = connectivityManager.getNetworkCapabilities(hasActiveNetwork)?: return false
            return hasConnectivity.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    hasConnectivity.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    hasConnectivity.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    hasConnectivity.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            // SDK version < 28/M
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}

private class MyWebViewClient : WebViewClient() {
    // method for overriding URL loading in default browser
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return false
    }
}