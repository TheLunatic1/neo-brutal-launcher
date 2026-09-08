package com.salmantoha.neolauncher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.salmantoha.neolauncher.controlcenter.ControlCenterManager
import com.salmantoha.neolauncher.controlcenter.ui.NeoControlCenterScreen
import com.salmantoha.neolauncher.model.AppItem
import com.salmantoha.neolauncher.ui.components.NeoAppContextDialog
import com.salmantoha.neolauncher.ui.drawer.AppDrawerScreen
import com.salmantoha.neolauncher.ui.home.HomeScreen
import com.salmantoha.neolauncher.ui.theme.NeoBrutalLauncherTheme

class MainActivity : ComponentActivity() {

    private lateinit var controlCenterManager: ControlCenterManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-Edge System Bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        controlCenterManager = ControlCenterManager(this)

        setContent {
            NeoBrutalLauncherTheme {
                val appRepository = (application as NeoLauncherApp).appRepository
                val allApps by appRepository.allApps.collectAsState()
                val pinnedApps by appRepository.pinnedApps.collectAsState()

                var isDrawerOpen by remember { mutableStateOf(false) }
                var isControlCenterOpen by remember { mutableStateOf(false) }
                var selectedAppForContext by remember { mutableStateOf<AppItem?>(null) }

                // Back gesture closes open panels
                BackHandler(enabled = isDrawerOpen || isControlCenterOpen) {
                    if (isControlCenterOpen) {
                        isControlCenterOpen = false
                    } else if (isDrawerOpen) {
                        isDrawerOpen = false
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Home Screen
                    HomeScreen(
                        pinnedApps = pinnedApps,
                        allApps = allApps,
                        onAppClick = { app ->
                            appRepository.launchApp(app)
                        },
                        onAppLongClick = { app ->
                            selectedAppForContext = app
                        },
                        onOpenDrawer = {
                            isDrawerOpen = true
                        },
                        onOpenControlCenter = {
                            controlCenterManager.checkAllStates()
                            isControlCenterOpen = true
                        },
                        onOpenSettings = {
                            startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                    )

                    // App Drawer Slide-in from Bottom
                    AnimatedVisibility(
                        visible = isDrawerOpen,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        AppDrawerScreen(
                            apps = allApps,
                            onAppClick = { app ->
                                isDrawerOpen = false
                                appRepository.launchApp(app)
                            },
                            onAppLongClick = { app ->
                                selectedAppForContext = app
                            }
                        )
                    }

                    // Neo-Brutalist Control Center & Notifications Slide-in from Top
                    AnimatedVisibility(
                        visible = isControlCenterOpen,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        NeoControlCenterScreen(
                            manager = controlCenterManager,
                            onClose = {
                                isControlCenterOpen = false
                            }
                        )
                    }

                    // Context Menu Dialog (Pin, Info, Uninstall)
                    selectedAppForContext?.let { app ->
                        NeoAppContextDialog(
                            app = app,
                            onPinToggle = {
                                appRepository.togglePin(it)
                            },
                            onAppInfo = {
                                appRepository.openAppInfo(it)
                            },
                            onUninstall = {
                                appRepository.uninstallApp(it)
                            },
                            onDismiss = {
                                selectedAppForContext = null
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::controlCenterManager.isInitialized) {
            controlCenterManager.checkAllStates()
        }
    }
}
