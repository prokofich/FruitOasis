package com.example.orchardoasis.application

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.orchardoasis.model.extensions.createAndSaveUserId
import com.example.orchardoasis.model.extensions.getUserId

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if(sharedPreferences.getUserId() == ""){
            sharedPreferences.createAndSaveUserId() // генерация и сохранение кастомного USER ID
        }

    }

}