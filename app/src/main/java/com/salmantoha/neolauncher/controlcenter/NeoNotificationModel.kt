package com.salmantoha.neolauncher.controlcenter

import android.app.PendingIntent
import android.graphics.drawable.Drawable

data class NeoNotificationItem(
    val key: String,
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val title: String,
    val text: String,
    val timestamp: Long,
    val contentIntent: PendingIntent? = null
)
