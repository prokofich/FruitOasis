package com.example.orchardoasis.model.webview

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Message
import android.preference.PreferenceManager
import android.view.View
import android.view.Window
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.amplitude.api.AmplitudeClient
import com.example.orchardoasis.model.extensions.saveLastUrl
import com.example.orchardoasis.view.`interface`.InterfaceGame
import org.json.JSONObject

class CreatorWebView(private val interfaceActivity: InterfaceGame, private val context: Context, private val window: Window, private val amplitude: AmplitudeClient?, private val timer:Long) {

    var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserResultCode = 1
    private var customView: View? = null
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    //функция создания WebView
    fun createWebView(): WebView {
        var webView = WebView(context)
        webView = setSettingsForWebView(webView)
        return webView
    }

    // функция установки настроек к созданному WebView
    private fun setSettingsForWebView(webView: WebView): WebView {

        var webSettings = webView.settings

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH) // улучшение производительности
        webSettings.allowContentAccess = true // Разрешает веб-содержимому доступ к локальным ресурсам
        webSettings.allowFileAccessFromFileURLs = true // Разрешает доступ к файлам из файловых URL
        webSettings.javaScriptCanOpenWindowsAutomatically = true // Разрешает JavaScript открывать новые окна без явного разрешения пользователя.
        webSettings.allowUniversalAccessFromFileURLs = true // Разрешает универсальный доступ к файлам из файловых URL.
        webSettings.mediaPlaybackRequiresUserGesture = false  //мультимедийные элементы могут воспроизводиться автоматически
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setEnableSmoothTransition(true)
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.javaScriptEnabled = true // разрешает выполнение JavaScript в WebView
        webSettings.setSupportMultipleWindows(true) // разрешает открытие новых окон в WEbView
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // разрешает загрузку смешанного контента
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL // отображение страницы в соответствии с обычными правилами макета
        webSettings.loadWithOverviewMode = true // загрузка контента в соответствии с размерами экрана
        webSettings.useWideViewPort = true // правильное масштабирование
        webSettings.domStorageEnabled = true // разрешает использование DOM Storage для сохранения данных в локальном хранилище
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // использовать кеш по умолчанию
        webSettings.databaseEnabled = true // разрешает использование базы данных для хранения данных
        webSettings.databasePath = context.getDir("webview_databases", 0).path // устанавливает путь к базе данных для WebView
        webSettings.allowFileAccess = true // разрешает загрузку файлов из локального хранилища
        webSettings.mediaPlaybackRequiresUserGesture = false // разрешение воспроизведения видео/аудио по умолчанию
        webSettings.loadsImagesAutomatically = true // разрешает загрузку изображений по умолчанию

        webView.webViewClient = object : WebViewClient() {

            private var flagOpenFirstUrl = false

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(url!=null){

                    // отправка ивента
                    if(!flagOpenFirstUrl){
                        flagOpenFirstUrl = true
                        val timerOpenFirstUrl = ((System.currentTimeMillis()-timer)/1000).toInt()
                        amplitude?.logEvent("first_url_open_time",JSONObject().put("time",timerOpenFirstUrl))
                    }

                    // отправка ивента
                    amplitude?.logEvent("open_url",JSONObject().put("url",url))

                    sharedPreferences.saveLastUrl(url) //сохранение новой открывшейся ссылки
                }
                return false
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if(url!=null){

                    // отправка ивента
                    if(!flagOpenFirstUrl){
                        flagOpenFirstUrl = true
                        val timerOpenFirstUrl = ((System.currentTimeMillis()-timer)/1000).toInt()
                        amplitude?.logEvent("first_url_open_time",JSONObject().put("time",timerOpenFirstUrl))
                    }

                    // отправка ивента
                    amplitude?.logEvent("open_url",JSONObject().put("url",url))

                    sharedPreferences.saveLastUrl(url) //сохранение новой открывшейся ссылки

                    val Url = request.url.toString()

                    if (Url.startsWith("tg://")) {
                        openAppOrRedirectToPlayStore(context, Url, "org.telegram.messenger")
                        return true
                    } else if (Url.startsWith("viber://")) {
                        openAppOrRedirectToPlayStore(context, Url, "com.viber.voip")
                        return true
                    } else if (Url.startsWith("whatsapp://")) {
                        openAppOrRedirectToPlayStore(context, Url, "com.whatsapp")
                        return true
                    } else if (url.startsWith("https://t.me/")) {
                        // Обработка ссылок на Telegram
                        openTelegram(view?.context, url)
                        return true
                    }

                }
                return false
            }

            private fun openAppOrRedirectToPlayStore(context: Context, url: String, packageName: String) {
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                if (isAppInstalled(context, appIntent)) {
                    // Приложение установлено, открываем его
                    context.startActivity(appIntent)
                } else {
                    // Приложение не установлено, перенаправляем пользователя в Google Play
                    redirectToPlayStore(context, packageName)
                }
            }

            // функция проверки установлено ли определенное приложение
            private fun isAppInstalled(context: Context, intent: Intent): Boolean {
                val packageManager = context.packageManager
                val resolveInfo = packageManager.resolveActivity(intent, 0)
                return resolveInfo != null
            }

            // функция перехода пользователя на скачивание приложения
            private fun redirectToPlayStore(context: Context, packageName: String) {
                try {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                    )
                } catch (e: ActivityNotFoundException) {
                    // В случае, если Google Play Store не установлен, открываем веб-версию
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                    )
                }
            }

            private fun openTelegram(context: Context?, url: String) {
                // Если Telegram установлен, открываем ссылку в Telegram
                if (isAppInstalled(context!!, Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=nsqmarket")))) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    // Если Telegram не установлен, перенаправляем пользователя в магазин приложений
                    redirectToPlayStore(context, "org.telegram.messenger")
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // обработка открытия пустой страницы
                view?.evaluateJavascript(
                    "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
                ) { html: String ->
                    if (html.trim().isEmpty()) {
                        amplitude?.logEvent("empty_blank")
                    }
                }

            }

        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (fileUploadCallback != null) {
                    fileUploadCallback!!.onReceiveValue(null)
                    fileUploadCallback = null
                }
                fileUploadCallback = filePathCallback
                val intent = fileChooserParams.createIntent()
                try {
                    interfaceActivity.goActivityForResult(intent,fileChooserResultCode,fileUploadCallback)
                } catch (e: Exception) {
                    fileUploadCallback = null
                    return false
                }
                return true
            }

            @SuppressLint("ObsoleteSdkInt")
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val resources = request.resources
                    if (resources.contains("android.webkit.resource.VIDEO_CAPTURE")) {

                        interfaceActivity.perm2(request,resources)

                    } else {

                        request.deny()
                        super.onPermissionRequest(request)

                    }
                }
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                super.onShowCustomView(view, callback)

                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                if (customView != null) {
                    callback.onCustomViewHidden()
                    return
                }
                customView = view
                customView?.let {
                    val decorView = window.decorView as FrameLayout
                    decorView.addView(it, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    interfaceActivity.setHorizontalScreen()
                }
                webView.visibility = View.GONE
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                customView?.let {
                    val decorView = window.decorView as FrameLayout
                    decorView.removeView(it)
                    interfaceActivity.setVerticalScreen() // установка портретного режима экрана
                    customView = null
                }
                webView.visibility = View.VISIBLE
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {

                val newWebView = createWebView()

                val href = view!!.handler.obtainMessage()
                view.requestFocusNodeHref(href)
                val url = href.data.getString("url").toString()

                newWebView.loadUrl(url)

                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()

                interfaceActivity.addNewWebView(newWebView)

                return true

            }

            override fun onCloseWindow(window: WebView?) {
                super.onCloseWindow(window)

                interfaceActivity.showToast("закрыл окно")

            }
        }
        return webView
    }

}