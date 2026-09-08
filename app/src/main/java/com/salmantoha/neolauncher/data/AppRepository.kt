package com.salmantoha.neolauncher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.salmantoha.neolauncher.model.AppCategory
import com.salmantoha.neolauncher.model.AppItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class AppRepository(private val context: Context) {
    private val pm: PackageManager = context.packageManager
    private val prefs: SharedPreferences = context.getSharedPreferences("neo_launcher_prefs", Context.MODE_PRIVATE)
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _allApps = MutableStateFlow<List<AppItem>>(emptyList())
    val allApps: StateFlow<List<AppItem>> = _allApps.asStateFlow()

    private val _pinnedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val pinnedApps: StateFlow<List<AppItem>> = _pinnedApps.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        repositoryScope.launch {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(intent, 0)
            }

            val pinnedPkgNames = prefs.getStringSet("pinned_packages", defaultPinnedPackages()) ?: defaultPinnedPackages()
            val ownPackage = context.packageName

            val items = resolveInfos.mapNotNull { ri ->
                val pkgName = ri.activityInfo.packageName
                if (pkgName == ownPackage) return@mapNotNull null
                val actName = ri.activityInfo.name
                val label = ri.loadLabel(pm).toString()
                val icon = ri.loadIcon(pm)
                val category = categorizeApp(pkgName, label)
                val isPinned = pinnedPkgNames.contains(pkgName)
                val installTime = try {
                    pm.getPackageInfo(pkgName, 0).firstInstallTime
                } catch (e: Exception) {
                    0L
                }

                AppItem(
                    packageName = pkgName,
                    activityName = actName,
                    label = label,
                    icon = icon,
                    category = category,
                    isPinned = isPinned,
                    installTime = installTime
                )
            }.sortedBy { it.label.lowercase(Locale.ROOT) }

            _allApps.value = items
            _pinnedApps.value = items.filter { it.isPinned }
        }
    }

    fun togglePin(app: AppItem) {
        val currentPinned = prefs.getStringSet("pinned_packages", defaultPinnedPackages())?.toMutableSet() ?: defaultPinnedPackages().toMutableSet()
        if (currentPinned.contains(app.packageName)) {
            currentPinned.remove(app.packageName)
        } else {
            currentPinned.add(app.packageName)
        }
        prefs.edit().putStringSet("pinned_packages", currentPinned).apply()
        loadApps()
    }

    fun launchApp(app: AppItem) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(app.packageName, app.activityName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
            launchIntent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        }
    }

    fun openAppInfo(app: AppItem) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", app.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun uninstallApp(app: AppItem) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", app.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }

    private fun defaultPinnedPackages(): Set<String> {
        return setOf(
            "com.android.chrome",
            "com.google.android.youtube",
            "com.whatsapp",
            "com.android.camera",
            "com.google.android.apps.photos",
            "com.google.android.gm",
            "com.spotify.music",
            "com.github.android"
        )
    }

    private fun categorizeApp(packageName: String, label: String): AppCategory {
        val lowerPkg = packageName.lowercase(Locale.ROOT)
        val lowerLabel = label.lowercase(Locale.ROOT)

        return when {
            lowerPkg.contains("git") || lowerPkg.contains("term") || lowerPkg.contains("code") ||
            lowerPkg.contains("dev") || lowerPkg.contains("ide") || lowerPkg.contains("terminal") ||
            lowerLabel.contains("terminal") || lowerLabel.contains("github") || lowerLabel.contains("termux") -> AppCategory.DEV

            lowerPkg.contains("whatsapp") || lowerPkg.contains("telegram") || lowerPkg.contains("facebook") ||
            lowerPkg.contains("instagram") || lowerPkg.contains("twitter") || lowerPkg.contains("discord") ||
            lowerPkg.contains("messenger") || lowerPkg.contains("reddit") || lowerPkg.contains("contacts") ||
            lowerPkg.contains("dialer") || lowerPkg.contains("phone") || lowerPkg.contains("messaging") -> AppCategory.SOCIAL

            lowerPkg.contains("youtube") || lowerPkg.contains("spotify") || lowerPkg.contains("music") ||
            lowerPkg.contains("video") || lowerPkg.contains("gallery") || lowerPkg.contains("camera") ||
            lowerPkg.contains("netflix") || lowerPkg.contains("vlc") || lowerPkg.contains("photos") -> AppCategory.MEDIA

            lowerPkg.contains("game") || lowerPkg.contains("play.games") || lowerPkg.contains("unity") -> AppCategory.GAMES

            else -> AppCategory.TOOLS
        }
    }
}
