package com.salmantoha.neolauncher.ui.home

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.salmantoha.neolauncher.model.AppItem
import com.salmantoha.neolauncher.ui.components.NeoAppIcon
import com.salmantoha.neolauncher.ui.components.NeoDotGridBackground
import com.salmantoha.neolauncher.ui.components.NeoFloatingDock
import com.salmantoha.neolauncher.ui.components.NeoHeroClockCard
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

    var dragOffsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgAmoled)
    ) {
        NeoDotGridBackground(
            modifier = Modifier
                .fillMaxSize()
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        dragOffsetY += delta
                    },
                    onDragStopped = {
                        if (dragOffsetY < -60f) {
                            onOpenDrawer()
                        }
                        dragOffsetY = 0f
                    }
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Hero Widgets Row (Clock 2x2 + Weather 2x2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(134.dp),
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
                            // Weather widget tap
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4-Column Pinned App Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
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
