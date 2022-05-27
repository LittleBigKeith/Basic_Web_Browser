
package com.example.webbrowser

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.media.Image
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_WebBrowser)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlText = findViewById<EditText>(R.id.URL)
        val buttonGo = findViewById<Button>(R.id.button_go)
        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val back = findViewById<ImageButton>(R.id.back)
        val forward = findViewById<ImageButton>(R.id.forward)
        val reload = findViewById<ImageButton>(R.id.reload)
        val webView = findViewById<WebView>(R.id.webView)

        buttonGo.setOnClickListener {
            if (isNetworkAvailable()) {
                try {
                    // Get the URL from editText to be executed
                    val webSite = urlText.text.toString()
                    setWebClient(webSite)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val toast = Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        val typeStrings = resources.getStringArray(R.array.web_type)
        val typeList = object: ArrayAdapter<String>(this, R.layout.spinner_item, typeStrings) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                if (position % 2 == 0) {
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_50))
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
                return view
            }
        }

        typeList.setDropDownViewResource(R.layout.dropdown_item)
        spinner.adapter = typeList

        button1.setOnClickListener {
            val buttonID = 1
            button1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_red))
            button2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
            button3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
            buttonCoursePage(buttonID, typeList)
        }

        button2.setOnClickListener {
            val buttonID = 2
            button1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
            button2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_red))
            button3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
            buttonCoursePage(buttonID, typeList)
        }

        button3.setOnClickListener {
            val buttonID = 3
            button1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
            button2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500))
            button3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_red))
            buttonCoursePage(buttonID, typeList)
        }

        urlText.setText("")

        back.setOnClickListener() {
            //TODO
        }

        forward.setOnClickListener() {
            //TODO
        }

        reload.setOnClickListener() {
            val current = urlText.text.toString()
            webView.loadUrl(current)
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

    //method for setting up web client
    private fun setWebClient(webSite: String) {
        val urlText = findViewById<EditText>(R.id.URL)
        val webView = findViewById<WebView>(R.id.webView)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val typeList = resources.getStringArray(R.array.web_type)
        if (spinner.selectedItem != typeList[1] || webSite == getString(R.string.error_404)) {
            webView.getSettings().setJavaScriptEnabled(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            webView.webViewClient = object : WebViewClient() {
                // method for overriding URL loading in default browser
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?
                ): Boolean {
                    return false
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)

                    urlText.setText(url)
                    CookieManager.getInstance().flush()
                }
            }


            webView.settings.loadWithOverviewMode = true // loads the WebView completely zoomed out
            webView.settings.useWideViewPort = true
            webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
            webView.webChromeClient = WebChromeClient()
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            webView.loadUrl(webSite)
        } else {
            webView.getContext().startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(webSite))
            )
        }
        // Hide soft keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun buttonCoursePage(buttonID: Int, typeList: ArrayAdapter<String>) {
        val spinner = findViewById<Spinner>(R.id.spinner)
        var pageID = R.string.error_404
        if (isNetworkAvailable()) {
            try {
                when (spinner.selectedItem) {
                    typeList.getItem(0) -> pageID = resources.getIdentifier("website_$buttonID", "string", this.packageName)
                    typeList.getItem(1) -> pageID = resources.getIdentifier("forum_$buttonID", "string", this.packageName)
                    typeList.getItem(2) -> pageID = resources.getIdentifier("moodle_$buttonID", "string", this.packageName)
                }
                val page = getString(pageID)
                if (page.isNullOrEmpty())  setWebClient(getString(R.string.error_404)) else setWebClient(page)
            } catch (e: Exception) {
                setWebClient(getString(R.string.error_404))
                e.printStackTrace()
            }
        } else {
            val toast = Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}