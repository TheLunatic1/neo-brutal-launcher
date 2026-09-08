package com.salmantoha.neolauncher.ui.home

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.AlarmClock
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.salmantoha.neolauncher.model.AppItem
import com.salmantoha.neolauncher.ui.components.NeoAppIcon
import com.salmantoha.neolauncher.ui.components.NeoDotGridBackground
import com.salmantoha.neolauncher.ui.components.NeoFloatingDock
import com.salmantoha.neolauncher.ui.components.NeoHeroClockCard
import com.salmantoha.neolauncher.ui.components.NeoTelemetryBar
import com.salmantoha.neolauncher.ui.components.NeoWeatherCard
import com.salmantoha.neolauncher.ui.theme.BgAmoled
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    pinnedApps: List<AppItem>,
    allApps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Live Clock State
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    var amPm by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val amPmFormat = SimpleDateFormat("a", Locale.getDefault())

        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            amPm = amPmFormat.format(now)
            delay(1000)
        }
    }

    // Battery Telemetry
    var batteryLevel by remember { mutableIntStateOf(85) }
    var isCharging by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        batteryStatus?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            batteryLevel = if (level != -1 && scale != -1) (level * 100 / scale) else 85
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        }
    }

    // Gesture detection (Swipe UP -> Drawer, Swipe DOWN -> Notifications)
    var dragAccumulator by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgAmoled)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { dragAccumulator = 0f },
                    onDragEnd = {
                        if (dragAccumulator < -80f) {
                            onOpenDrawer()
                        } else if (dragAccumulator > 80f) {
                            onOpenControlCenter()
                        }
                        dragAccumulator = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        dragAccumulator += dragAmount
                    }
                )
            }
    ) {
        NeoDotGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Status & Telemetry Bar
                NeoTelemetryBar(
                    batteryLevel = batteryLevel,
                    isCharging = isCharging,
                    appCount = allApps.size,
                    onSettingsClick = onOpenSettings
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hero Widgets Row (Clock 2x2 + Weather 2x2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(130.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    NeoHeroClockCard(
                        timeString = if (currentTime.isEmpty()) "04:05" else currentTime,
                        dateString = if (currentDate.isEmpty()) "MON, SEP 8" else currentDate,
                        amPmString = if (amPm.isEmpty()) "PM" else amPm,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            try {
                                val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(clockIntent)
                            } catch (e: Exception) {
                                // fallback
                            }
                        }
                    )

                    NeoWeatherCard(
                        temperature = "30°C",
                        condition = "CLEAR",
                        humidity = "64%",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Weather intent or browser
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4-Column Pinned App Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val displayApps = if (pinnedApps.isNotEmpty()) pinnedApps else allApps.take(8)
                    items(displayApps, key = { it.packageName + it.activityName }) { app ->
                        NeoAppIcon(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }

            // Floating Neo-Brutalist Bottom Dock
            NeoFloatingDock(
                dockApps = pinnedApps.take(4).ifEmpty { allApps.take(4) },
                onOpenDrawer = onOpenDrawer,
                onAppClick = onAppClick,
                onAppLongClick = onAppLongClick
            )
        }
    }
}
