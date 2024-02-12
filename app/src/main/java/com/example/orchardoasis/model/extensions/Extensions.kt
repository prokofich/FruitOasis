package com.example.orchardoasis.model.extensions

import android.content.Context
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import android.webkit.WebView
import android.widget.FrameLayout
import com.example.orchardoasis.model.constant.ADVERTISING_ID
import com.example.orchardoasis.model.constant.COUNT_START_APPLICATION
import com.example.orchardoasis.model.constant.LAST_URL_IN_WEB
import com.example.orchardoasis.model.constant.MAIN_ATTRIBUTE
import com.example.orchardoasis.model.constant.MAIN_URL_IN_WEB
import com.example.orchardoasis.model.constant.STATUS_INSTALLATION
import com.example.orchardoasis.model.constant.USER_ID
import com.example.orchardoasis.model.repository.Repository
import java.util.UUID

// функция расширения для вставки параметров в сырую ссылку
fun String.setValueInRawLink(listName:List<String>?,packageName:String,context: Context):String{

    val repository = Repository(context)

    if(listName==null){
        var s = this
        s = s.replace("{bundle}", packageName)
        s = s.replace("{devName}","razrab10")
        s = s.replace("{appsflyer_id}",repository.getUserId() )
        s = s.replace("{advertising_id}", repository.getAdvertisingId())
        s = s.replace("{appsdv}","none")

        return s

    }else{
        var s = this
        s = s.replace("{bundle}", packageName)
        s = s.replace("{devName}","razrab10")
        s = s.replace("{appsflyer_id}",repository.getUserId() )
        s = s.replace("{advertising_id}", repository.getAdvertisingId())
        s = s.replace("{appsdv}","none")
            .let {
                listName.foldIndexed(it) { index, acc, value ->
                    acc.replace("{dp${index + 1}}", value)
                }
            }

        return s

    }
}

// функция добавления ссылки и показа первого WebView
fun WebView.loadUrlAndShowFirstWebView(
    context: Context,
    webArray:MutableList<WebView>,
    constLayout: FrameLayout?
){
    this.loadUrl(Repository(context).getMainUrl()) // добавление полученной ссылки с параметрами в первый WebView
    webArray.add(this)                             // добавление первого WebView в список
    constLayout?.addView(this)                 // добавление первого WebView на экран
}

// функция добавления ссылки и показа старого WebView
fun WebView.loadUrlAndShowOldWebView(
    context: Context,
    webArray:MutableList<WebView>,
    constLayout: FrameLayout?
){
    this.loadUrl(Repository(context).getLastUrl()) // добавление полученной ссылки с параметрами в старый WebView
    webArray.add(this)                             // добавление старого WebView в список
    constLayout?.addView(this)                 // добавление старого WebView на экран
}

fun WebView.loadUrlAndShowNewWebView(url:String, webArray:MutableList<WebView>, constLayout: FrameLayout){
    this.loadUrl(url)                              // добавление ссылки в новый WebView
    webArray.add(this)                             // добавление нового WebView в список
    constLayout.addView(this)                 // добавление нового WebView на экран
}

// функция сохранения последнего URL адреса
fun SharedPreferences.saveLastUrl(url:String){
    this.edit()
        .putString(LAST_URL_IN_WEB,url)
        .apply()
}

// функция сохранения первого полученного URL адреса
fun SharedPreferences.saveMainUrl(url:String){
    this.edit()
        .putString(MAIN_URL_IN_WEB,url)
        .apply()
}

// функция сохранения главного аттрибута
fun SharedPreferences.saveMainAttribute(nameCompany:String){
    this.edit()
        .putString(MAIN_ATTRIBUTE,nameCompany)
        .apply()
}

// функция сохранения статуса установки
fun SharedPreferences.saveStatusInstallation(status:String){
    this.edit()
        .putString(STATUS_INSTALLATION,status)
        .apply()
}

// функция получения статуса установки
fun SharedPreferences.getStatusInstallation():String{
    return this.getString(STATUS_INSTALLATION,"").toString()
}

// функция обновления количества запусков приложения
fun SharedPreferences.updateCountStartApplication(){
    this.edit()
        .putInt(COUNT_START_APPLICATION,getCountStartApplication()+1)
        .apply()
}

// функция получения последнего url адреса
fun SharedPreferences.getLastUrl(): String{
    return this.getString(LAST_URL_IN_WEB,"").toString()
}

// функция получения главного аттрибута
fun SharedPreferences.getMainAttribute(): String{
    return this.getString(MAIN_ATTRIBUTE,"").toString()
}

//функция получения iso кода симки устройства
fun SharedPreferences.getIsoCodeInPhone(appContext: Context): String {
    val tlphnMngr = appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return tlphnMngr.simCountryIso.toString()
}

//функция получения первого полученного url адреса
fun SharedPreferences.getMainUrl(): String {
    return this.getString(MAIN_URL_IN_WEB,"").toString()
}

//функция получения количества запусков приложения
fun SharedPreferences.getCountStartApplication(): Int {
    return this.getInt(COUNT_START_APPLICATION,0)
}

//функция проверки первого запуска приложения
fun SharedPreferences.checkFirstStartApplication(): Boolean {
    return getCountStartApplication()==0
}

//функция создания кастомного ID юзера для OneSignal
fun SharedPreferences.createAndSaveUserId(){
    val srId = UUID.randomUUID().toString()
    this.edit()
        .putString(USER_ID,srId)
        .apply()
}

//функция получения кастомного ID юзера
fun SharedPreferences.getUserId():String{
    return this.getString(USER_ID,"").toString()
}

//функция получения advertising id
fun SharedPreferences.getAdvertisingId(): String {
    return this.getString(ADVERTISING_ID,"").toString()
}

//функция сохранения advertising id
fun SharedPreferences.saveAdvertisingId(id:String){
    this.edit()
        .putString(ADVERTISING_ID,id)
        .apply()
}



