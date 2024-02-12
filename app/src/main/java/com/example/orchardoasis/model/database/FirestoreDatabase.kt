package com.example.orchardoasis.model.database

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import com.amplitude.api.AmplitudeClient
import com.example.orchardoasis.model.constant.FOR_GAME
import com.example.orchardoasis.model.constant.FOR_WEBVIEW
import com.example.orchardoasis.model.constant.NOT_ORGANIC_INSTALL
import com.example.orchardoasis.model.constant.ORGANIC_INSTALL
import com.example.orchardoasis.model.extensions.saveLastUrl
import com.example.orchardoasis.model.extensions.saveMainUrl
import com.example.orchardoasis.model.extensions.setValueInRawLink
import com.example.orchardoasis.model.extensions.updateCountStartApplication
import com.example.orchardoasis.view.`interface`.InterfaceSplash
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class FirestoreDatabase(private val interfaceSplash: InterfaceSplash, private val context: Context, private val amplitude: AmplitudeClient?, private val timer:Long) {

    private val database = FirebaseFirestore.getInstance()
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getUrlFromDatabase(status:String,lst:List<String>?,packageName:String){
        CoroutineScope(Dispatchers.IO).launch {
            database.collection("COLLECTION_URL")
                .document("DOCUMENT_URL")
                .get()
                .addOnSuccessListener { data ->
                    when(status){
                        ORGANIC_INSTALL -> {
                            Handler(Looper.getMainLooper()).post {
                                if(data["organic_url"]!=""){

                                    val timerBackend = ((System.currentTimeMillis()-timer)/1000).toInt() // время получения сырой ссылки

                                    val newUrl = data["organic_url"].toString().setValueInRawLink(lst,packageName,context)

                                    amplitude?.logEvent("backend_url",JSONObject().put("url",data["organic_url"]))
                                    amplitude?.logEvent("first_url",JSONObject().put("url",newUrl))
                                    amplitude?.logEvent("backend_received_time",JSONObject().put("time",timerBackend))

                                    sharedPreferences.updateCountStartApplication()
                                    sharedPreferences.saveMainUrl(newUrl) // сохранение полученной ссылки
                                    sharedPreferences.saveLastUrl(newUrl) // сохранение полученной ссылки
                                    interfaceSplash.goToStubApplication(FOR_WEBVIEW) // переход для показа WebView
                                }else{
                                    interfaceSplash.goToStubApplication(FOR_GAME) // переход на заглушку,если ссылка пустая
                                }
                            }
                        }
                        NOT_ORGANIC_INSTALL -> {
                            Handler(Looper.getMainLooper()).post {
                                if (data["inorganic_url"]!=""){

                                    val timerBackend = ((System.currentTimeMillis()-timer)/1000).toInt() // время получения сырой ссылки

                                    val newUrl = data["inorganic_url"].toString().setValueInRawLink(lst,packageName,context)

                                    amplitude?.logEvent("backend_url",JSONObject().put("url",data["inorganic_url"]))
                                    amplitude?.logEvent("first_url",JSONObject().put("url",newUrl))
                                    amplitude?.logEvent("backend_received_time",JSONObject().put("time",timerBackend))

                                    sharedPreferences.updateCountStartApplication()
                                    sharedPreferences.saveMainUrl(newUrl) // сохранение полученной ссылки
                                    sharedPreferences.saveLastUrl(newUrl) // сохранение полученной ссылки
                                    interfaceSplash.goToStubApplication(FOR_WEBVIEW) // переход для показа WebView
                                }else{
                                    interfaceSplash.goToStubApplication(FOR_GAME) // переход на заглушку,если ссылка пустая
                                }
                            }
                        }
                    }
                }
        }
    }

    // функция проверки значения в органике
    fun checkIsEmptyOrganic(){
        CoroutineScope(Dispatchers.IO).launch {
            database.collection("COLLECTION_URL")
                .document("DOCUMENT_URL")
                .get()
                .addOnSuccessListener { data ->
                    Handler(Looper.getMainLooper()).post{
                        if(data["organic_url"]==""){
                            interfaceSplash.goToStubApplication(FOR_GAME) // переход на заглушку при пустой органике
                        }else{
                            interfaceSplash.showLastWebView() // показ последней ссылки на экране заглушки
                        }
                    }
                }
        }
    }

    // функция проверки значения в неорганике
    fun checkIsEmptyNotOrganic(){
        CoroutineScope(Dispatchers.IO).launch {
            database.collection("COLLECTION_URL")
                .document("DOCUMENT_URL")
                .get()
                .addOnSuccessListener { data ->
                    Handler(Looper.getMainLooper()).post{
                        if(data["inorganic_url"]==""){
                            interfaceSplash.goToStubApplication(FOR_GAME) // переход на заглушку при пустой неорганике
                        }else{
                            interfaceSplash.showLastWebView() // показ последней ссылки на экране заглушки
                        }
                    }
                }
        }
    }

}