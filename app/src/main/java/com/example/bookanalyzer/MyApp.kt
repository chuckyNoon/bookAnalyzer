package com.example.bookanalyzer

import android.app.Application
import com.example.bookanalyzer.di.AppComponent
import com.example.bookanalyzer.di.modules.ContextModule
import com.example.bookanalyzer.di.DaggerAppComponent

class MyApp() : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().contextModule(ContextModule(this)).build()
    }
}