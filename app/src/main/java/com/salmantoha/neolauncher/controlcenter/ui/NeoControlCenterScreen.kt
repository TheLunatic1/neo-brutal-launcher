package com.salmantoha.neolauncher.controlcenter.ui

import android.media.AudioManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.salmantoha.neolauncher.controlcenter.ControlCenterManager
import com.salmantoha.neolauncher.controlcenter.NeoNotificationItem
import com.salmantoha.neolauncher.controlcenter.NeoNotificationListener
import com.salmantoha.neolauncher.ui.components.NeoBrutalCard
import com.salmantoha.neolauncher.ui.components.NeoDotGridBackground
import com.salmantoha.neolauncher.ui.theme.AccentAmber
import com.salmantoha.neolauncher.ui.theme.AccentCyan
import com.salmantoha.neolauncher.ui.theme.AccentEmerald
import com.salmantoha.neolauncher.ui.theme.AccentIndigo
import com.salmantoha.neolauncher.ui.theme.AccentRose
import com.salmantoha.neolauncher.ui.theme.BgAmoled
import com.salmantoha.neolauncher.ui.theme.CardBg
import com.salmantoha.neolauncher.ui.theme.CardBorder
import com.salmantoha.neolauncher.ui.theme.TextMuted
import com.salmantoha.neolauncher.ui.theme.TextPrimary
import com.salmantoha.neolauncher.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NeoControlCenterScreen(
    manager: ControlCenterManager,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by NeoNotificationListener.notificationsFlow.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgAmoled)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -60f) {
                        onClose()
                    }
                }
            }
    ) {
        NeoDotGridBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "// CONTROL CENTER",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "[SYSTEM QUICK SETTINGS & TELEMETRY]",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = AccentIndigo
                            )
                        )
                    }

                    // Close Button
                    NeoBrutalCard(
                        shadowOffset = 2.dp,
                        cornerRadius = 8.dp,
                        borderWidth = 1.5.dp,
                        onClick = onClose
                    ) {
                        Box(
                            modifier = Modifier.padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Hero 2-column Quick Toggles (Wi-Fi & Bluetooth)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Wi-Fi Tile
                    NeoQuickTile(
                        title = "[WI-FI]",
                        subtitle = manager.wifiSsid,
                        icon = Icons.Default.Wifi,
                        isActive = manager.isWifiOn,
                        activeColor = AccentIndigo,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.openWifiSettings() }
                    )

                    // Bluetooth Tile
                    NeoQuickTile(
                        title = "[BLUETOOTH]",
                        subtitle = if (manager.isBluetoothOn) "ACTIVE" else "DISABLED",
                        icon = Icons.Default.Bluetooth,
                        isActive = manager.isBluetoothOn,
                        activeColor = AccentCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.openBluetoothSettings() }
                    )
                }
            }

            // 4-column Quick Toggles Grid (Torch, Data, Sound, Rotate)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Torch
                    NeoMiniTile(
                        icon = Icons.Default.FlashlightOn,
                        label = "TORCH",
                        isActive = manager.isTorchOn,
                        activeColor = AccentAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.toggleTorch() }
                    )

                    // Mobile Data
                    NeoMiniTile(
                        icon = Icons.Default.SignalCellularAlt,
                        label = "CELLULAR",
                        isActive = true,
                        activeColor = AccentEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.openDataSettings() }
                    )

                    // Sound Mode
                    val soundIcon = when (manager.ringerMode) {
                        AudioManager.RINGER_MODE_SILENT -> Icons.Default.VolumeOff
                        AudioManager.RINGER_MODE_VIBRATE -> Icons.Default.Vibration
                        else -> Icons.Default.VolumeUp
                    }
                    val soundLabel = when (manager.ringerMode) {
                        AudioManager.RINGER_MODE_SILENT -> "SILENT"
                        AudioManager.RINGER_MODE_VIBRATE -> "VIBRATE"
                        else -> "RING"
                    }
                    NeoMiniTile(
                        icon = soundIcon,
                        label = soundLabel,
                        isActive = manager.ringerMode != AudioManager.RINGER_MODE_SILENT,
                        activeColor = AccentIndigo,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.toggleRingerMode() }
                    )

                    // Auto Rotate
                    NeoMiniTile(
                        icon = Icons.Default.ScreenRotation,
                        label = if (manager.isAutoRotateOn) "AUTO" else "LOCKED",
                        isActive = manager.isAutoRotateOn,
                        activeColor = AccentRose,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.toggleAutoRotate() }
                    )
                }
            }

            // Second 4-column Quick Toggles Grid (Hotspot, Location, Airplane, etc.)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeoMiniTile(
                        icon = Icons.Default.WifiTethering,
                        label = "HOTSPOT",
                        isActive = false,
                        activeColor = AccentIndigo,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.openHotspotSettings() }
                    )

                    NeoMiniTile(
                        icon = Icons.Default.LocationOn,
                        label = "LOCATION",
                        isActive = true,
                        activeColor = AccentEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.openLocationSettings() }
                    )

                    NeoMiniTile(
                        icon = Icons.Default.AirplanemodeActive,
                        label = "AIRPLANE",
                        isActive = false,
                        activeColor = AccentAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.openDataSettings() }
                    )

                    NeoMiniTile(
                        icon = Icons.Default.Notifications,
                        label = "NOTIFS",
                        isActive = manager.hasNotificationAccess,
                        activeColor = AccentCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { manager.requestNotificationAccess() }
                    )
                }
            }

            // Dual Sliders (Brightness & Volume)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Brightness Slider
                    NeoSliderCard(
                        title = "BRIGHTNESS",
                        icon = Icons.Default.Brightness6,
                        value = manager.brightness,
                        onValueChange = { manager.setBrightnessLevel(it) },
                        accentColor = AccentAmber
                    )

                    // Volume Slider
                    val volumeRatio = if (manager.maxVolume > 0) manager.currentVolume.toFloat() / manager.maxVolume.toFloat() else 0.5f
                    NeoSliderCard(
                        title = "MEDIA VOLUME",
                        icon = Icons.Default.VolumeUp,
                        value = volumeRatio,
                        onValueChange = { ratio ->
                            val target = (ratio * manager.maxVolume).toInt()
                            manager.setVolumeLevel(target)
                        },
                        accentColor = AccentIndigo
                    )
                }
            }

            // Hardware Telemetry Banner (RAM & Storage)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeoBrutalCard(
                        modifier = Modifier.weight(1f),
                        cornerRadius = 14.dp,
                        borderWidth = 1.5.dp,
                        shadowOffset = 3.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "[RAM TELEMETRY]",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = AccentEmerald
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = manager.ramUsageText,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    NeoBrutalCard(
                        modifier = Modifier.weight(1f),
                        cornerRadius = 14.dp,
                        borderWidth = 1.5.dp,
                        shadowOffset = 3.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "[STORAGE USAGE]",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = AccentCyan
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = manager.storageUsageText,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Notification Stream Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// ACTIVE NOTIFICATIONS (${notifications.size})",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AccentIndigo
                        )
                    )

                    if (!manager.hasNotificationAccess) {
                        NeoBrutalCard(
                            shadowOffset = 2.dp,
                            cornerRadius = 6.dp,
                            backgroundColor = AccentAmber.copy(alpha = 0.2f),
                            borderColor = AccentAmber,
                            borderWidth = 1.dp,
                            onClick = { manager.requestNotificationAccess() }
                        ) {
                            Text(
                                text = "[GRANT ACCESS]",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = AccentAmber
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            if (!manager.hasNotificationAccess) {
                item {
                    NeoBrutalCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = CardBg,
                        borderColor = AccentAmber,
                        cornerRadius = 14.dp,
                        borderWidth = 1.5.dp,
                        shadowOffset = 3.dp,
                        onClick = { manager.requestNotificationAccess() }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "[!] NOTIFICATION LISTENER REQUIRED",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AccentAmber
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap here to allow Neo Launcher to intercept and format your phone's notifications in Neo-Brutalist cards.",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            } else if (notifications.isEmpty()) {
                item {
                    NeoBrutalCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 14.dp,
                        borderWidth = 1.5.dp,
                        shadowOffset = 3.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "[SYSTEM CLEAR // NO ACTIVE NOTIFICATIONS]",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }
            } else {
                items(notifications, key = { it.key }) { notif ->
                    NeoNotificationCard(
                        item = notif,
                        onDismiss = {
                            NeoNotificationListener.instance?.dismissNotification(notif.key)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun NeoQuickTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NeoBrutalCard(
        modifier = modifier.height(76.dp),
        backgroundColor = if (isActive) activeColor.copy(alpha = 0.15f) else CardBg,
        borderColor = if (isActive) activeColor else CardBorder,
        borderWidth = 2.dp,
        shadowOffset = 3.dp,
        cornerRadius = 16.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (isActive) activeColor else CardBg, RoundedCornerShape(10.dp))
                    .border(1.5.dp, if (isActive) Color.White else CardBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isActive) BgAmoled else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isActive) activeColor else TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun NeoMiniTile(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NeoBrutalCard(
        modifier = modifier.height(72.dp),
        backgroundColor = if (isActive) activeColor.copy(alpha = 0.15f) else CardBg,
        borderColor = if (isActive) activeColor else CardBorder,
        borderWidth = 1.5.dp,
        shadowOffset = 2.5.dp,
        cornerRadius = 14.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = if (isActive) activeColor else TextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NeoSliderCard(
    title: String,
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    NeoBrutalCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = CardBg,
        borderColor = CardBorder,
        borderWidth = 1.5.dp,
        shadowOffset = 3.dp,
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "[$title]",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = accentColor
                        )
                    )
                    Text(
                        text = "${(value * 100).toInt()}%",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = TextPrimary
                        )
                    )
                }
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NeoNotificationCard(
    item: NeoNotificationItem,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatted = remember(item.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    NeoBrutalCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = CardBg,
        borderColor = CardBorder,
        borderWidth = 1.5.dp,
        shadowOffset = 3.dp,
        cornerRadius = 14.dp,
        onClick = {
            try {
                item.contentIntent?.send()
            } catch (e: Exception) {
                // ignore
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    item.appIcon?.let { icon ->
                        Image(
                            painter = rememberDrawablePainter(drawable = icon),
                            contentDescription = item.appName,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "[${item.appName.uppercase()}]",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = AccentIndigo
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• $timeFormatted",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (item.title.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.text,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextSecondary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
