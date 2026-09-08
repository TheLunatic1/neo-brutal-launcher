package com.salmantoha.neolauncher.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.salmantoha.neolauncher.model.AppCategory
import com.salmantoha.neolauncher.model.AppItem
import com.salmantoha.neolauncher.ui.theme.AccentAmber
import com.salmantoha.neolauncher.ui.theme.AccentCyan
import com.salmantoha.neolauncher.ui.theme.AccentEmerald
import com.salmantoha.neolauncher.ui.theme.AccentIndigo
import com.salmantoha.neolauncher.ui.theme.AccentRose
import com.salmantoha.neolauncher.ui.theme.BgAmoled
import com.salmantoha.neolauncher.ui.theme.CardBg
import com.salmantoha.neolauncher.ui.theme.CardBorder
import com.salmantoha.neolauncher.ui.theme.DotGridColor
import com.salmantoha.neolauncher.ui.theme.HardShadow
import com.salmantoha.neolauncher.ui.theme.TextMuted
import com.salmantoha.neolauncher.ui.theme.TextPrimary
import com.salmantoha.neolauncher.ui.theme.TextSecondary

/**
 * Neo-Brutalist Card with solid offset black shadow and crisp border.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NeoBrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardBg,
    borderColor: Color = CardBorder,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "press_scale"
    )
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    Box(
        modifier = modifier.scale(scale)
    ) {
        // Hard Shadow Layer (Offset)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(HardShadow, shape)
        )

        // Foreground Card Layer
        Box(
            modifier = Modifier
                .background(backgroundColor, shape)
                .border(borderWidth, borderColor, shape)
                .clip(shape)
                .then(
                    if (onClick != null && onLongClick != null) {
                        Modifier.combinedClickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick,
                            onLongClick = onLongClick
                        )
                    } else if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                    } else if (onLongClick != null) {
                        Modifier.combinedClickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {},
                            onLongClick = onLongClick
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }
    }
}

/**
 * Subtle Dot Grid interactive background
 */
@Composable
fun NeoDotGridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize().background(BgAmoled)) {
        val step = 28.dp.toPx()
        val radius = 1.5.dp.toPx()
        val cols = (size.width / step).toInt() + 1
        val rows = (size.height / step).toInt() + 1

        for (i in 0 until cols) {
            for (j in 0 until rows) {
                drawCircle(
                    color = DotGridColor,
                    radius = radius,
                    center = Offset(i * step, j * step)
                )
            }
        }
    }
}

/**
 * Dynamic Neo-Brutal App Icon Card
 * Centers the official/default app icon inside a high-contrast squircle card
 */
@Composable
fun NeoAppIcon(
    app: AppItem,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeoBrutalCard(
            modifier = Modifier.size(62.dp),
            backgroundColor = CardBg,
            borderColor = CardBorder,
            borderWidth = 2.dp,
            shadowOffset = 3.dp,
            cornerRadius = 18.dp,
            onClick = onClick,
            onLongClick = onLongClick
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberDrawablePainter(drawable = app.icon),
                    contentDescription = app.label,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.label,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Top Telemetry Banner
 */
@Composable
fun NeoTelemetryBar(
    batteryLevel: Int,
    isCharging: Boolean,
    appCount: Int,
    onControlCenterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [STATUS: ONLINE] (Tap opens Control Center)
        NeoBrutalCard(
            shadowOffset = 2.dp,
            cornerRadius = 8.dp,
            borderWidth = 1.5.dp,
            onClick = onControlCenterClick
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentEmerald, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STATUS: ONLINE",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = AccentEmerald
                    )
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Battery pill (Tap opens Control Center)
            NeoBrutalCard(
                shadowOffset = 2.dp,
                cornerRadius = 8.dp,
                borderWidth = 1.5.dp,
                onClick = onControlCenterClick
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Battery",
                        tint = if (isCharging) AccentEmerald else AccentAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$batteryLevel%",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = TextPrimary
                        )
                    )
                }
            }

            // Settings button
            NeoBrutalCard(
                shadowOffset = 2.dp,
                cornerRadius = 8.dp,
                borderWidth = 1.5.dp,
                onClick = onSettingsClick
            ) {
                Box(
                    modifier = Modifier.padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Signature Hero Clock Widget (Top-Left 2x2)
 */
@Composable
fun NeoHeroClockCard(
    timeString: String,
    dateString: String,
    amPmString: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NeoBrutalCard(
        modifier = modifier,
        backgroundColor = CardBg,
        borderColor = AccentIndigo,
        borderWidth = 2.5.dp,
        shadowOffset = 4.dp,
        cornerRadius = 20.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[TIME]",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = AccentIndigo
                    )
                )
                Box(
                    modifier = Modifier
                        .background(AccentIndigo, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = amPmString,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = BgAmoled
                        )
                    )
                }
            }

            Text(
                text = timeString,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TextPrimary,
                    letterSpacing = (-1).sp
                )
            )

            Text(
                text = dateString.uppercase(),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            )
        }
    }
}

/**
 * Signature Live Weather Widget (Top-Right 2x2)
 */
@Composable
fun NeoWeatherCard(
    temperature: String,
    condition: String,
    humidity: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NeoBrutalCard(
        modifier = modifier,
        backgroundColor = CardBg,
        borderColor = AccentAmber,
        borderWidth = 2.5.dp,
        shadowOffset = 4.dp,
        cornerRadius = 20.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[WEATHER]",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = AccentAmber
                    )
                )
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Weather Icon",
                    tint = AccentAmber,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = temperature,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TextPrimary,
                    letterSpacing = (-1).sp
                )
            )

            Text(
                text = "$condition • $humidity",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            )
        }
    }
}

/**
 * Search Bar with JetBrains Mono Styling
 */
@Composable
fun NeoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoBrutalCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = CardBg,
        borderColor = CardBorder,
        borderWidth = 2.dp,
        shadowOffset = 3.dp,
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AccentCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "SEARCH ALL APPS...",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    cursorBrush = SolidColor(AccentEmerald),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * Category Filter Pills
 */
@Composable
fun NeoCategoryChips(
    selectedCategory: AppCategory,
    onCategorySelected: (AppCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppCategory.values().forEach { category ->
            val isSelected = category == selectedCategory
            NeoBrutalCard(
                shadowOffset = if (isSelected) 3.dp else 2.dp,
                cornerRadius = 8.dp,
                backgroundColor = if (isSelected) AccentIndigo else CardBg,
                borderColor = if (isSelected) Color.White else CardBorder,
                borderWidth = if (isSelected) 2.dp else 1.5.dp,
                onClick = { onCategorySelected(category) }
            ) {
                Text(
                    text = "[${category.label}]",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else TextSecondary
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Neo-Brutalist Floating Dock
 */
@Composable
fun NeoFloatingDock(
    dockApps: List<AppItem>,
    onOpenDrawer: () -> Unit,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NeoBrutalCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        backgroundColor = CardBg,
        borderColor = CardBorder,
        borderWidth = 2.dp,
        shadowOffset = 4.dp,
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.take(4).forEach { app ->
                NeoBrutalCard(
                    modifier = Modifier.size(54.dp),
                    backgroundColor = CardBg,
                    borderColor = CardBorder,
                    borderWidth = 2.dp,
                    shadowOffset = 3.dp,
                    cornerRadius = 16.dp,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberDrawablePainter(drawable = app.icon),
                            contentDescription = app.label,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Dedicated Neo-Brutalist App Drawer Button
            NeoBrutalCard(
                modifier = Modifier.size(54.dp),
                backgroundColor = AccentIndigo,
                borderColor = Color.White,
                borderWidth = 2.dp,
                shadowOffset = 3.dp,
                cornerRadius = 16.dp,
                onClick = onOpenDrawer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = "App Drawer",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

/**
 * Neo-Brutalist Context Menu Dialog (Pin, Info, Uninstall)
 */
@Composable
fun NeoAppContextDialog(
    app: AppItem,
    onPinToggle: (AppItem) -> Unit,
    onAppInfo: (AppItem) -> Unit,
    onUninstall: (AppItem) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        NeoBrutalCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = CardBg,
            borderColor = AccentIndigo,
            borderWidth = 2.5.dp,
            shadowOffset = 6.dp,
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberDrawablePainter(drawable = app.icon),
                    contentDescription = app.label,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = app.label,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = app.packageName,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Pin / Unpin
                NeoBrutalCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (app.isPinned) AccentAmber else CardBg,
                    borderWidth = 1.5.dp,
                    shadowOffset = 2.dp,
                    cornerRadius = 10.dp,
                    onClick = {
                        onPinToggle(app)
                        onDismiss()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = if (app.isPinned) BgAmoled else AccentAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (app.isPinned) "UNPIN FROM HOME" else "PIN TO HOME",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (app.isPinned) BgAmoled else TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // App Info
                NeoBrutalCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CardBg,
                    borderWidth = 1.5.dp,
                    shadowOffset = 2.dp,
                    cornerRadius = 10.dp,
                    onClick = {
                        onAppInfo(app)
                        onDismiss()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "APP INFO / SETTINGS",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Uninstall
                NeoBrutalCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = AccentRose.copy(alpha = 0.15f),
                    borderColor = AccentRose,
                    borderWidth = 1.5.dp,
                    shadowOffset = 2.dp,
                    cornerRadius = 10.dp,
                    onClick = {
                        onUninstall(app)
                        onDismiss()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = AccentRose,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "UNINSTALL APPLICATION",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AccentRose
                            )
                        )
                    }
                }
            }
        }
    }
}
