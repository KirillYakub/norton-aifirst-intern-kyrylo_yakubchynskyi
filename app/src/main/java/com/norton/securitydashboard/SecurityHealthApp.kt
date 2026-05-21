package com.norton.securitydashboard

import android.app.Application
import com.norton.securitydashboard.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SecurityHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SecurityHealthApp)
            modules(appModule)
        }
    }
}
