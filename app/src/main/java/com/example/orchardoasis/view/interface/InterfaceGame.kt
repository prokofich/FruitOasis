package com.example.orchardoasis.view.`interface`

import android.content.Intent
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebView

interface InterfaceGame {

    fun goActivityForResult(intent: Intent, code:Int,cb: ValueCallback<Array<Uri>>?) // функция запуска activity
    fun setHorizontalScreen() // установка горизонтального режима экрана
    fun setVerticalScreen() // установка портретного режима экрана
    fun showToast(str:String)
    fun addNewWebView(webView: WebView) // функция добавление нового WebView на экран
    fun perm2(request: PermissionRequest,res:Array<String>)

}