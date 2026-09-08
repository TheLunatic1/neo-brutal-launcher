package com.salmantoha.neolauncher.controlcenter

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.salmantoha.neolauncher.controlcenter.ui.NeoControlCenterScreen
import com.salmantoha.neolauncher.ui.theme.NeoBrutalLauncherTheme

class NeoAccessibilityService : AccessibilityService(), LifecycleOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var topTriggerView: View? = null
    private var fullOverlayView: View? = null
    private var isOverlayExpanded by mutableStateOf(false)
    private lateinit var controlCenterManager: ControlCenterManager

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        controlCenterManager = ControlCenterManager(this)

        setupTopTriggerOverlay()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTopTriggerOverlay() {
        try {
            // Invisible top-edge gesture interceptor bar
            val triggerView = View(this)
            var startY = 0f

            triggerView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaY = event.rawY - startY
                        if (deltaY > 50f && !isOverlayExpanded) {
                            showControlCenterOverlay()
                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                (24 * resources.displayMetrics.density).toInt(),
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }

            windowManager.addView(triggerView, params)
            topTriggerView = triggerView
        } catch (e: Exception) {
            // ignore if unable to add view
        }
    }

    fun showControlCenterOverlay() {
        if (fullOverlayView == null) {
            val composeView = ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@NeoAccessibilityService)
                setViewTreeSavedStateRegistryOwner(this@NeoAccessibilityService)
                setContent {
                    NeoBrutalLauncherTheme {
                        AnimatedVisibility(
                            visible = isOverlayExpanded,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                        ) {
                            NeoControlCenterScreen(
                                manager = controlCenterManager,
                                onClose = { hideControlCenterOverlay() }
                            )
                        }
                    }
                }
            }

            val overlayParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }

            try {
                windowManager.addView(composeView, overlayParams)
                fullOverlayView = composeView
            } catch (e: Exception) {
                return
            }
        }

        controlCenterManager.checkAllStates()
        isOverlayExpanded = true
        fullOverlayView?.visibility = View.VISIBLE
    }

    fun hideControlCenterOverlay() {
        isOverlayExpanded = false
        fullOverlayView?.postDelayed({
            fullOverlayView?.visibility = View.GONE
        }, 300)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        topTriggerView?.let { windowManager.removeView(it) }
        fullOverlayView?.let { windowManager.removeView(it) }
    }

    companion object {
        var instance: NeoAccessibilityService? = null
            private set
    }
}
