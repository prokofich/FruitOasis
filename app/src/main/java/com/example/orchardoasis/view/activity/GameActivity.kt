package com.example.orchardoasis.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.WindowManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebBackForwardList
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.example.orchardoasis.R
import com.example.orchardoasis.databinding.ActivityGameBinding
import com.example.orchardoasis.model.constant.AMPLITUDE_API_KEY
import com.example.orchardoasis.model.constant.FOR_GAME
import com.example.orchardoasis.model.constant.FOR_WEBVIEW
import com.example.orchardoasis.model.constant.FOR_WEBVIEW_REPEAT
import com.example.orchardoasis.model.constant.GAME
import com.example.orchardoasis.model.constant.ONESIGNAL_APP_ID
import com.example.orchardoasis.model.constant.START_TIME
import com.example.orchardoasis.model.constant.TYPE
import com.example.orchardoasis.model.extensions.getUserId
import com.example.orchardoasis.model.extensions.loadUrlAndShowFirstWebView
import com.example.orchardoasis.model.extensions.loadUrlAndShowOldWebView
import com.example.orchardoasis.model.extensions.saveLastUrl
import com.example.orchardoasis.model.webview.CreatorWebView
import com.example.orchardoasis.view.fragments.MenuFragment
import com.example.orchardoasis.view.`interface`.InterfaceGame
import com.onesignal.OneSignal

class GameActivity : AppCompatActivity(),InterfaceGame {

    private var binding: ActivityGameBinding? = null
    private var webView: WebView? = null
    private var creatorWebView: CreatorWebView? = null
    private var amplitude: AmplitudeClient? = null
    private var requestPermissionLauncher:ActivityResultLauncher<String>? = null

    private var startTimer:Long = 0
    private var webViewArray = mutableListOf<WebView>()

    private var sharedPreferences:SharedPreferences? = null

    private var flpldCllbck: ValueCallback<Array<Uri>>? = null

    private var Request: PermissionRequest? = null
    private var Res:Array<String>? = null

    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Разрешение получено, продолжаем загрузку страницы
                    webView?.reload()
                    Request?.grant(Res)
                }
            }

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        GAME = this
        navController = Navigation.findNavController(this,R.id.id_nav_host)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        startTimer = intent.getLongExtra(START_TIME,0)

        amplitude = Amplitude.getInstance().initialize(applicationContext, AMPLITUDE_API_KEY)
        creatorWebView = CreatorWebView(this,this,window,amplitude,startTimer)

        webView = WebView(this) // далее сюда запишется правильный экземпляр

        // загрузка состояния WebView после смены конфигурации
        if (savedInstanceState != null) {
            webView?.restoreState(savedInstanceState)
        }

        // первый показ WebView
        if(intent.getStringExtra(TYPE) == FOR_WEBVIEW){
            initOneSignal() // инициализация OneSignal
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.id_nav_host) as NavHostFragment
            val menuFragment = navHostFragment.childFragmentManager.fragments[0] as MenuFragment
            menuFragment.hideContent() // скрытие контента
            webView = creatorWebView?.createWebView() // создание первого WebView
            webView?.loadUrlAndShowFirstWebView(this,webViewArray,binding?.idGame) // загрузка ссылки + показ на экране
        }

        // повторный показ WebView
        if(intent.getStringExtra(TYPE) == FOR_WEBVIEW_REPEAT){
            initOneSignal() // инициализация OneSignal
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.id_nav_host) as NavHostFragment
            val menuFragment = navHostFragment.childFragmentManager.fragments[0] as MenuFragment
            menuFragment.hideContent() // скрытие контента
            webView = creatorWebView?.createWebView() // создание старого WebView
            webView?.loadUrlAndShowOldWebView(this,webViewArray,binding?.idGame)   // загрузка ссылки + показ на экране
        }

        // проверка на переход для игры
        if(intent.getStringExtra(TYPE) == FOR_GAME){
            amplitude?.logEvent("open_main") // отправка ивента
        }

    }

    //обработка перехода назад + возможное закрытие
    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(intent.getStringExtra(TYPE) != FOR_GAME){

            val currentWebView = webViewArray.lastOrNull() // текущий WebView

            if (webViewArray.size > 1) {
                if (currentWebView!!.canGoBack()) {

                    savePreviousUrl(currentWebView) // сохранение ссылки при табе назад
                    currentWebView.goBack() // переход по ссылке назад в текущем WebView

                }else{

                    val previousWebView = webViewArray[webViewArray.size-2]
                    saveUrlInPreviousWebView(previousWebView) // сохранение ссылки при табе назад
                    webViewArray.removeLast() // удаляем текущий WebView из списка
                    binding?.idGame?.removeView(currentWebView) // удаляем текущий WebView с экрана*/

                }
            } else {
                if (currentWebView!!.canGoBack()) {

                    savePreviousUrl(currentWebView) // сохранение ссылки при табе назад
                    currentWebView.goBack() // переход по ссылке назад в текущем WebView

                }else{

                    finishAffinity() // закрытие приложения

                }
            }
        }
    }

    override fun goActivityForResult(intent: Intent, code: Int,cb:ValueCallback<Array<Uri>>?) {
        flpldCllbck = cb
        startActivityForResult(intent, code)
    }

    override fun setHorizontalScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun setVerticalScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun showToast(str: String) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show()
    }

    // добавление нового WebView при запросе на создание нового окна
    override fun addNewWebView(webView: WebView) {
        binding?.idGame?.addView(webView)
        webViewArray.add(webView)
    }

    override fun perm2(request: PermissionRequest,res:Array<String>) {
        Request = request
        Res = res
        requestPermissionLauncher?.launch(Manifest.permission.CAMERA)
    }

    //сохранение состояния при изменении конфигурации
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (flpldCllbck != null) {
                val results = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                creatorWebView?.fileUploadCallback!!.onReceiveValue(results)
                creatorWebView?.fileUploadCallback = null
            }
        }
    }

    // функция инициализации OneSignal
    private fun initOneSignal(){
        if(Settings.Global.getInt(applicationContext.contentResolver,"adb_enabled",0)==0){
            OneSignal.setAppId(ONESIGNAL_APP_ID)
            OneSignal.initWithContext(this)
            OneSignal.setExternalUserId(sharedPreferences?.getUserId()!!)
        }
    }

    // функция сохранения ссылки при табе назад
    private fun savePreviousUrl(webView:WebView){
        val history: WebBackForwardList = webView.copyBackForwardList()
        val previousUrl: String = history.getItemAtIndex(history.currentIndex - 1).url
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferences.saveLastUrl(previousUrl)
    }

    // функция сохранения ссылки при табе назад
    private fun saveUrlInPreviousWebView(webView:WebView){
        val history: WebBackForwardList = webView.copyBackForwardList()
        val previousUrl: String = history.getItemAtIndex(history.currentIndex).url
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferences.saveLastUrl(previousUrl)
    }

}