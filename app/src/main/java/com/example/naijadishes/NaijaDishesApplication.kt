package com.example.naijadishes

import android.app.Application
import com.example.naijadishes.data.AppContainer
import com.example.naijadishes.data.DefaultAppContainer

class NaijaDishesApplication: Application(){
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}