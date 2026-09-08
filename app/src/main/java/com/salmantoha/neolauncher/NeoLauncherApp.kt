package com.salmantoha.neolauncher

import android.app.Application
import com.salmantoha.neolauncher.data.AppRepository

class NeoLauncherApp : Application() {
    lateinit var appRepository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appRepository = AppRepository(this)
        appRepository.loadApps()
    }

    companion object {
        lateinit var instance: NeoLauncherApp
            private set
    }
}
