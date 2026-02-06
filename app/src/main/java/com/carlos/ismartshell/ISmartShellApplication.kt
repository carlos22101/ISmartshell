package com.carlos.ismartshell

import android.app.Application
import com.carlos.ismartshell.core.di.AppContainer
import com.carlos.ismartshell.core.di.DefaultAppContainer

class ISmartShellApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}