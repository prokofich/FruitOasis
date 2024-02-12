package com.example.orchardoasis.model.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.orchardoasis.model.constant.ADVERTISING_ID
import com.example.orchardoasis.model.constant.LAST_URL_IN_WEB
import com.example.orchardoasis.model.constant.MAIN_URL_IN_WEB
import com.example.orchardoasis.model.constant.USER_ID

class Repository(context: Context){

    @Suppress("DEPRECATION")
    //использование SharedPreferences в качестве базы данных
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    //функция получения последнего url адреса
    fun getLastUrl(): String {
        return sharedPreferences.getString(LAST_URL_IN_WEB,"").toString()
    }

    //функция получения первого полученного url адреса
    fun getMainUrl(): String {
        return sharedPreferences.getString(MAIN_URL_IN_WEB,"").toString()
    }

    //функция получения кастомного ID юзера
    fun getUserId():String{
        return sharedPreferences.getString(USER_ID,"").toString()
    }

    //функция получения advertising id
    fun getAdvertisingId(): String {
        return sharedPreferences.getString(ADVERTISING_ID,"").toString()
    }

}