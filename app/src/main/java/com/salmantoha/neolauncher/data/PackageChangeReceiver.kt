package com.salmantoha.neolauncher.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.salmantoha.neolauncher.NeoLauncherApp

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                try {
                    NeoLauncherApp.instance.appRepository.loadApps()
                } catch (e: Exception) {
                    // ignore if app is not yet initialized
                }
            }
        }
    }
}
