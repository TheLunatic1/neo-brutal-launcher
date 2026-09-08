package com.salmantoha.neolauncher.controlcenter

import android.app.Notification
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NeoNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        refreshNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        refreshNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        refreshNotifications()
    }

    fun refreshNotifications() {
        try {
            val active = activeNotifications ?: return
            val pm = packageManager

            val list = active.mapNotNull { sbn ->
                val extras = sbn.notification.extras ?: return@mapNotNull null
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
                if (title.isBlank() && text.isBlank()) return@mapNotNull null

                val pkgName = sbn.packageName
                val appName = try {
                    pm.getApplicationLabel(pm.getApplicationInfo(pkgName, 0)).toString()
                } catch (e: Exception) {
                    pkgName
                }
                val appIcon = try {
                    pm.getApplicationIcon(pkgName)
                } catch (e: Exception) {
                    null
                }

                NeoNotificationItem(
                    key = sbn.key,
                    packageName = pkgName,
                    appName = appName,
                    appIcon = appIcon,
                    title = title,
                    text = text,
                    timestamp = sbn.postTime,
                    contentIntent = sbn.notification.contentIntent
                )
            }.reversed()

            _notificationsFlow.value = list
        } catch (e: Exception) {
            // handle security exception if permission isn't granted yet
        }
    }

    fun dismissNotification(key: String) {
        try {
            cancelNotification(key)
            refreshNotifications()
        } catch (e: Exception) {
            // ignore
        }
    }

    companion object {
        var instance: NeoNotificationListener? = null
            private set

        private val _notificationsFlow = MutableStateFlow<List<NeoNotificationItem>>(emptyList())
        val notificationsFlow: StateFlow<List<NeoNotificationItem>> = _notificationsFlow.asStateFlow()
    }
}
