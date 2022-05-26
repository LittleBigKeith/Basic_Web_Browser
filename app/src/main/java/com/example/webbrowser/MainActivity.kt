package com.example.webbrowser

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlText = findViewById<EditText>(R.id.URL)
        val button = findViewById<Button>(R.id.button)
        val webView = findViewById<WebView>(R.id.webView)

        button.setOnClickListener {
            if (isNetworkAvailable()) {
                try {
                    // Get the URL from editText to be executed
                    val webSite = urlText.text.toString()
                    webView.webViewClient = object : WebViewClient() {
                        // method for overriding URL loading in default browser
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            return false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            urlText.setText(url)
                        }
                    }
                    webView.loadUrl(webSite)
                    // Hide soft keyboard
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val toast = Toast.makeText(
                    this,
                    "You're offline! Please check your internet connection",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }

    }

    //method for checking connectivity
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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