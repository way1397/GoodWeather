package com.wsf.goodweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object {
        const val TOKEN="123"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}