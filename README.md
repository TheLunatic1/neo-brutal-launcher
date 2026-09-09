# NeoBrutal Launcher & SystemUI Exploration

> ⚠️ **Project Status: Unresolved / Experimental Proof-of-Concept (WIP)**  
> This repository contains the code for a custom Android Launcher built with Jetpack Compose following a Dark Neo-Brutalist design language, alongside research, overlays, and technical findings on Android SystemUI / Quick Settings customization.

---

## 🎨 Overview

NeoBrutal Launcher is an experimental Android launcher designed around high-contrast, minimalist Dark Neo-Brutalism inspired by modern typography, dot grids, squircle card containers, and sharp offset drop shadows.

### Key Visual & Design Pillars
* **Deep AMOLED Base**: `#0b0f19` dark canvas with subtle dot grid background.
* **Elevated Card Surfaces**: `#141d2e` squircle containers with 2px crisp borders (`rgba(255, 255, 255, 0.45)`) and 3px hard black drop shadows (`#000000`).
* **Vibrant Accent Accents**: Electric Indigo (`#6366f1`), Solar Amber (`#fbbf24`), and Emerald Mint (`#34d399`).
* **Typography**: Space Grotesk inspired bold hero elements and high-contrast labels (`#f8fafc`).

---

## 📱 Features Implemented (Launcher)

1. **Hero Widgets**:
   * **Live Digital Clock**: Bold hero card with real-time updates and one-tap access to the system Alarm/Clock.
   * **Live Weather Card**: Clean temperature, conditions, and humidity readout.
2. **Dynamic App Icon Cards**:
   * Automatically extracts and renders official system app icons inside Neo-Brutalist card containers with 2px borders and 3px drop shadows.
3. **Floating Bottom Dock**:
   * Elevated floating bar housing pinned primary apps and the quick App Drawer launcher button.
4. **Categorized App Drawer**:
   * Fast-scrolling bottom sheet with fuzzy search.
   * Filter pills: `[ALL]`, `[DEV]`, `[SOCIAL]`, `[MEDIA]`, `[TOOLS]`.
   * Swipe-down gesture or tap-outside to dismiss.
5. **App Context Menu**:
   * Long-press any app to Pin/Unpin from Homescreen, open App Info, or Uninstall.

---

## 🔬 SystemUI & Control Center Technical Findings

During development, extensive experiments were conducted to achieve a unified, system-wide Neo-Brutalist Control Center / Quick Settings shade and Lock Screen. The findings are documented below:

### 1. Non-Root Approach (Accessibility & Window Overlays)
* **Limitation**: Android's `WindowManagerService` and `PhoneWindowManager` assign top touch priority (0–24dp top bezel) to the native OS `StatusBar`. 
* Dragging from the top physical edge triggers the native Android notification shade before any third-party app or accessibility service can intercept it.

### 2. Root / RRO (Runtime Resource Overlay) & Fabricated Overlays
* Using root and Android 14's `cmd overlay fabricate`, Material 3 semantic color tokens (`system_accent1_*`, `system_surface_*`, `system_neutral1_*`) and SystemUI background colors can be successfully overridden (scripts available in `systemui-overlay/`).
* **Limitation**: While colorways, scrims, and status bar text colors adapt, stock Android hardcodes its Quick Settings view hierarchy, pill buttons, and slider drawables in compiled Java/Kotlin classes (`QSPanel`, `QSTileViewImpl`, `BrightnessSliderController`). Resource overlays cannot rewrite the underlying layout tree or inject custom Compose UI.

### 3. Conclusion & OS-Level Path
* Achieving a 100% custom-built Control Center shade, lockscreen layout, and system dialogs identical to the custom Compose design requires compiling a **Custom AOSP ROM** (e.g. LineageOS / crDroid device tree with modified `SystemUI.apk` source) for the target device kernel.

---

## 📂 Repository Structure

```
neo-brutal-launcher/
├── app/
│   ├── src/main/java/com/salmantoha/neolauncher/
│   │   ├── controlcenter/     # Control center managers & listeners
│   │   ├── data/              # App list repository & package receivers
│   │   ├── model/             # Data models (AppItem, etc.)
│   │   ├── ui/
│   │   │   ├── components/    # Reusable Neo-Brutalist Compose components
│   │   │   ├── drawer/        # Categorized App Drawer sheet
│   │   │   ├── home/          # Main Homescreen & widgets
│   │   │   └── theme/         # Color palettes, typography & themes
│   │   └── MainActivity.kt    # Main launcher entrypoint
├── systemui-overlay/          # SystemUI & Framework-res overlay scripts (Root)
│   ├── apply_neo_overlays.py  # Script to fabricate palette overlays
│   ├── apply_m3_overlays.py   # Script to fabricate M3 semantic tokens
│   └── AndroidManifest.xml    # RRO Overlay manifest definitions
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🛠️ Building & Running

### Prerequisites
* Android Studio
* Android SDK
* Java JDK

### Build Command
```bash
# Build debug APK
./gradlew assembleDebug

# Install via ADB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📜 License & Acknowledgements
Developed as an experimental exploration into Android UI customization, Jetpack Compose launcher architecture, and AOSP SystemUI theming.
