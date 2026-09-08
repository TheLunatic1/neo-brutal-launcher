package com.salmantoha.neolauncher.controlcenter

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ControlCenterManager(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val scope = CoroutineScope(Dispatchers.Main)

    var isTorchOn by mutableStateOf(false)
        private set

    var isWifiOn by mutableStateOf(false)
        private set

    var wifiSsid by mutableStateOf("NOT CONNECTED")
        private set

    var isBluetoothOn by mutableStateOf(false)
        private set

    var ringerMode by mutableIntStateOf(AudioManager.RINGER_MODE_NORMAL)
        private set

    var isAutoRotateOn by mutableStateOf(false)
        private set

    var brightness by mutableFloatStateOf(0.7f)
        private set

    var currentVolume by mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        private set

    val maxVolume by mutableIntStateOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))

    var ramUsageText by mutableStateOf("3.2GB / 8GB")
        private set

    var storageUsageText by mutableStateOf("64GB / 128GB")
        private set

    var hasNotificationAccess by mutableStateOf(false)
        private set

    init {
        checkAllStates()
    }

    fun checkAllStates() {
        scope.launch {
            checkWifiState()
            checkRingerMode()
            checkAutoRotate()
            checkVolume()
            checkTelemetry()
            checkNotificationAccess()
        }
    }

    private fun checkWifiState() {
        try {
            isWifiOn = wifiManager.isWifiEnabled
            val activeNetwork = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val info = wifiManager.connectionInfo
                val ssid = info?.ssid?.replace("\"", "") ?: "CONNECTED"
                wifiSsid = if (ssid == "<unknown ssid>" || ssid.isBlank()) "WI-FI CONNECTED" else ssid
            } else {
                wifiSsid = if (isWifiOn) "WI-FI ON (DISCONNECTED)" else "WI-FI OFF"
            }
        } catch (e: Exception) {
            wifiSsid = "WI-FI"
        }
    }

    private fun checkRingerMode() {
        try {
            ringerMode = audioManager.ringerMode
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun checkAutoRotate() {
        try {
            val autoRotate = Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0)
            isAutoRotateOn = autoRotate == 1
        } catch (e: Exception) {
            isAutoRotateOn = false
        }
    }

    private fun checkVolume() {
        try {
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun checkTelemetry() {
        try {
            // RAM
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)
            val totalRamGB = (memInfo.totalMem / (1024.0 * 1024 * 1024)).let { "%.1f".format(it) }
            val usedRamGB = ((memInfo.totalMem - memInfo.availMem) / (1024.0 * 1024 * 1024)).let { "%.1f".format(it) }
            ramUsageText = "${usedRamGB}GB / ${totalRamGB}GB"

            // Storage
            val path: File = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong
            val totalStorageGB = (totalBlocks * blockSize / (1024.0 * 1024 * 1024)).toInt()
            val usedStorageGB = ((totalBlocks - availableBlocks) * blockSize / (1024.0 * 1024 * 1024)).toInt()
            storageUsageText = "${usedStorageGB}GB / ${totalStorageGB}GB"
        } catch (e: Exception) {
            // ignore
        }
    }

    fun checkNotificationAccess() {
        try {
            val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            hasNotificationAccess = flat != null && flat.contains(context.packageName)
        } catch (e: Exception) {
            hasNotificationAccess = false
        }
    }

    fun toggleTorch() {
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (cameraId != null) {
                val newState = !isTorchOn
                cameraManager.setTorchMode(cameraId, newState)
                isTorchOn = newState
            }
        } catch (e: Exception) {
            // fallback
        }
    }

    fun openWifiSettings() {
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun openBluetoothSettings() {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun openDataSettings() {
        try {
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallbackIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallbackIntent)
        }
    }

    fun openHotspotSettings() {
        try {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun openLocationSettings() {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun toggleRingerMode() {
        try {
            val nextMode = when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_NORMAL -> AudioManager.RINGER_MODE_VIBRATE
                AudioManager.RINGER_MODE_VIBRATE -> AudioManager.RINGER_MODE_SILENT
                else -> AudioManager.RINGER_MODE_NORMAL
            }
            audioManager.ringerMode = nextMode
            ringerMode = nextMode
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                // ignore
            }
        }
    }

    fun toggleAutoRotate() {
        try {
            if (Settings.System.canWrite(context)) {
                val newState = if (isAutoRotateOn) 0 else 1
                Settings.System.putInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, newState)
                isAutoRotateOn = newState == 1
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun setVolumeLevel(targetVolume: Int) {
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, AudioManager.FLAG_SHOW_UI)
            currentVolume = targetVolume
        } catch (e: Exception) {
            // ignore
        }
    }

    fun setBrightnessLevel(value: Float) {
        brightness = value.coerceIn(0.05f, 1f)
    }

    fun requestNotificationAccess() {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }
}
