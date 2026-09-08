package com.salmantoha.neolauncher.model

import android.graphics.drawable.Drawable

enum class AppCategory(val label: String) {
    ALL("ALL"),
    DEV("DEV"),
    SOCIAL("SOCIAL"),
    MEDIA("MEDIA"),
    TOOLS("TOOLS"),
    GAMES("GAMES")
}

data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable,
    val category: AppCategory = AppCategory.TOOLS,
    val isPinned: Boolean = false,
    val installTime: Long = 0L
)

data class BatteryInfo(
    val percentage: Int = 100,
    val isCharging: Boolean = false
)

data class WeatherInfo(
    val temperature: String = "30°C",
    val condition: String = "CLEAR",
    val humidity: String = "64%",
    val wind: String = "12 km/h"
)
