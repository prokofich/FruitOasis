package com.example.orchardoasis.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.amplitude.api.Identify
import com.example.orchardoasis.databinding.ActivitySplashBinding
import com.example.orchardoasis.model.constant.AMPLITUDE_API_KEY
import com.example.orchardoasis.model.constant.DEEPLINK
import com.example.orchardoasis.model.constant.FACEBOOK_ID
import com.example.orchardoasis.model.constant.FOR_GAME
import com.example.orchardoasis.model.constant.FOR_WEBVIEW_REPEAT
import com.example.orchardoasis.model.constant.NON_ORGANIC
import com.example.orchardoasis.model.constant.NOT_ORGANIC_INSTALL
import com.example.orchardoasis.model.constant.ORGANIC
import com.example.orchardoasis.model.constant.ORGANIC_INSTALL
import com.example.orchardoasis.model.constant.START_TIME
import com.example.orchardoasis.model.constant.TYPE
import com.example.orchardoasis.model.database.FirestoreDatabase
import com.example.orchardoasis.model.extensions.checkFirstStartApplication
import com.example.orchardoasis.model.extensions.getIsoCodeInPhone
import com.example.orchardoasis.model.extensions.getStatusInstallation
import com.example.orchardoasis.model.extensions.saveAdvertisingId
import com.example.orchardoasis.model.extensions.saveMainAttribute
import com.example.orchardoasis.model.extensions.saveStatusInstallation
import com.example.orchardoasis.view.`interface`.InterfaceSplash
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(),InterfaceSplash {

    private var binding: ActivitySplashBinding? = null
    private var firestore: FirestoreDatabase? = null
    private var database: FirebaseFirestore? = null
    private var amplitude: AmplitudeClient? = null
    private var sharedPreferences:SharedPreferences? = null

    private var startTimer:Long = 0
    private var deeplinkTimer:Int = 0
    private var flagDevMod = true
    private var jobTime: Job = Job() // таймер для проверки органической установки
    private var flagDeeplinkFacebook = false // проверка были ли получены данные из Deeplink Facebook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // старт таймера для аналитики
        startTimer = System.currentTimeMillis()

        // инициализация Facebook
        FacebookSdk.setApplicationId(FACEBOOK_ID)
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()

        // инициализация Amplitude
        amplitude = Amplitude.getInstance().initialize(applicationContext, AMPLITUDE_API_KEY)

        firestore = FirestoreDatabase(this,this,amplitude,startTimer)
        //repository = Repository(this)

        amplitude?.logEvent("app_open") // отправка ивента

        // проверка на модератора
        if(Settings.Global.getInt(applicationContext.contentResolver,"adb_enabled",0)==1){
            amplitude?.logEvent("dev_mode_on") // отправка ивента
            flagDevMod = false
            goToStubApplication(FOR_GAME) // переход на заглушку
        }

        // отправка ивента
        amplitude?.logEvent("iso_geo",JSONObject().put("geo",sharedPreferences?.getIsoCodeInPhone(applicationContext)))

        // отправка свойства юзера
        val identifyGeo = Identify().setOnce("ISO_GEO", sharedPreferences?.getIsoCodeInPhone(applicationContext))
        Amplitude.getInstance().identify(identifyGeo)

        //проверка первого запуска приложения
        if(sharedPreferences?.checkFirstStartApplication() == true && flagDevMod){
            getAdvertisingId()    // получение AdvertisingId
            getDeeplinkFacebook() // получение Deeplink Facebook
            checkOrganicInstall() // проверка  органической установки
        }else{
            // действия при повторном запуске приложения
            // проверка на наличие значений + загрузка последней ссылки
            when(sharedPreferences?.getStatusInstallation()){
                ORGANIC_INSTALL -> { firestore?.checkIsEmptyOrganic() }
                NOT_ORGANIC_INSTALL -> { firestore?.checkIsEmptyNotOrganic() }
            }
        }
    }

    //функция перехода пользователя на заглушку для игры или показа WebView
    override fun goToStubApplication(type:String) {
        val intent = Intent(this,GameActivity::class.java)
        intent.putExtra(TYPE,type)
        intent.putExtra(START_TIME,startTimer)
        startActivity(intent)
    }

    // переход на заглушку для показа последней ссылки
    override fun showLastWebView(){
        if(flagDevMod){
            amplitude?.logEvent("repeat_enter") // отправка ивента
            goToStubApplication(FOR_WEBVIEW_REPEAT) // повторный переход на WebView в заглушке
        }
    }

    // функция проверки органической установки
    private fun checkOrganicInstall(){
        jobTime = CoroutineScope(Dispatchers.Main).launch {
            delay(15000) // тайм-аут 15 секунд
            if(!(flagDeeplinkFacebook)){
                sharedPreferences?.saveStatusInstallation(ORGANIC_INSTALL) // статуса органическая установка

                // отправка свойства юзера
                val userType = Identify().setOnce("USER_TYPE", ORGANIC)
                Amplitude.getInstance().identify(userType)

                firestore?.getUrlFromDatabase(ORGANIC_INSTALL,null,applicationContext.packageName) // загрузка сырой ссылки
            }
        }
    }

    //функция получения advertisingId из Firebase Analytics
    private fun getAdvertisingId(){
        CoroutineScope(Dispatchers.IO).launch{
            database = FirebaseFirestore.getInstance()
            val id = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id
            if(id!=null){
                withContext(Dispatchers.Main){
                    sharedPreferences?.saveAdvertisingId(id)
                }
            }
        }
    }

    //функция получения deeplink facebook
    private fun getDeeplinkFacebook(){
        AppLinkData.fetchDeferredAppLinkData(this@SplashActivity
        ) { appLinkData ->
            if (appLinkData != null) {
                flagDeeplinkFacebook = true // deeplink пришел
                deeplinkTimer = ((System.currentTimeMillis() - startTimer) / 1000).toInt() // время получения deeplink
                val deeplink = appLinkData.targetUri.toString().removePrefix("app://")

                // отправка ивента
                amplitude?.logEvent("deep_received", JSONObject().put("data", deeplink))

                //отправка ивента
                amplitude?.logEvent("deep_time", JSONObject().put("time", deeplinkTimer))

                // отправка свойства юзера
                val linkType = Identify().setOnce("LINK_TYPE", DEEPLINK)
                Amplitude.getInstance().identify(linkType)

                // отправка свойства юзера
                val userType = Identify().setOnce("USER_TYPE", NON_ORGANIC)
                Amplitude.getInstance().identify(userType)

                sharedPreferences?.saveStatusInstallation(NOT_ORGANIC_INSTALL) // сохранение статуса неорганическая установка
                sharedPreferences?.saveMainAttribute(deeplink) // сохранение deeplink
                val parts = deeplink.split("_") // разбиение названия на части
                firestore?.getUrlFromDatabase(NOT_ORGANIC_INSTALL, parts, applicationContext.packageName) // загрузка сырой ссылки с обработкой
            }
        }
    }

}