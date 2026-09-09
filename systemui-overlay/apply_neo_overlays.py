import subprocess
import time

ADB = r"C:\Users\Salman Toha\AppData\Local\Microsoft\WinGet\Packages\Google.PlatformTools_Microsoft.Winget.Source_8wekyb3d8bbwe\platform-tools\adb.exe"

def run_adb(cmd):
    full_cmd = [ADB, "-s", "emulator-5554", "shell"] + cmd.split(" ")
    res = subprocess.run(full_cmd, capture_output=True, text=True)
    return res.stdout.strip(), res.stderr.strip(), res.returncode

framework_colors = {
    # Accent 1 (Electric Indigo)
    "system_accent1_0_dark": "0xffffffff",
    "system_accent1_10_dark": "0xffeef2ff",
    "system_accent1_50_dark": "0xffe0e7ff",
    "system_accent1_100_dark": "0xffc7d2fe",
    "system_accent1_200_dark": "0xff6366f1",
    "system_accent1_300_dark": "0xff818cf8",
    "system_accent1_400_dark": "0xff6366f1",
    "system_accent1_500_dark": "0xff4f46e5",
    "system_accent1_600_dark": "0xff4338ca",
    "system_accent1_700_dark": "0xff3730a3",
    "system_accent1_800_dark": "0xff1e1b4b",
    "system_accent1_900_dark": "0xff0f172a",
    "system_accent1_1000_dark": "0xff000000",
    
    # Accent 2 (Solar Amber)
    "system_accent2_0_dark": "0xffffffff",
    "system_accent2_10_dark": "0xfffffbeb",
    "system_accent2_50_dark": "0xfffef3c7",
    "system_accent2_100_dark": "0xfffde68a",
    "system_accent2_200_dark": "0xfffbbf24",
    "system_accent2_300_dark": "0xfff59e0b",
    "system_accent2_400_dark": "0xffd97706",
    "system_accent2_500_dark": "0xffb45309",
    "system_accent2_600_dark": "0xff92400e",
    "system_accent2_700_dark": "0xff78350f",
    "system_accent2_800_dark": "0xff451a03",
    "system_accent2_900_dark": "0xff1e1b4b",
    "system_accent2_1000_dark": "0xff000000",

    # Accent 3 (Emerald Mint)
    "system_accent3_0_dark": "0xffffffff",
    "system_accent3_10_dark": "0xffecfdf5",
    "system_accent3_50_dark": "0xffd1fae5",
    "system_accent3_100_dark": "0xffa7f3d0",
    "system_accent3_200_dark": "0xff34d399",
    "system_accent3_300_dark": "0xff10b981",
    "system_accent3_400_dark": "0xff059669",
    "system_accent3_500_dark": "0xff047857",
    "system_accent3_600_dark": "0xff065f46",
    "system_accent3_700_dark": "0xff064e3b",
    "system_accent3_800_dark": "0xff022c22",
    "system_accent3_900_dark": "0xff061a14",
    "system_accent3_1000_dark": "0xff000000",

    # Neutral 1 (Deep AMOLED Slate Background)
    "system_neutral1_0_dark": "0xffffffff",
    "system_neutral1_10_dark": "0xfff8fafc",
    "system_neutral1_50_dark": "0xfff1f5f9",
    "system_neutral1_100_dark": "0xffe2e8f0",
    "system_neutral1_200_dark": "0xffcbd5e1",
    "system_neutral1_300_dark": "0xff94a3b8",
    "system_neutral1_400_dark": "0xff64748b",
    "system_neutral1_500_dark": "0xff475569",
    "system_neutral1_600_dark": "0xff334155",
    "system_neutral1_700_dark": "0xff1e293b",
    "system_neutral1_800_dark": "0xff141d2e",
    "system_neutral1_900_dark": "0xff0b0f19",
    "system_neutral1_1000_dark": "0xff000000",

    # Neutral 2 (Dark Cards & Controls)
    "system_neutral2_0_dark": "0xffffffff",
    "system_neutral2_10_dark": "0xfff8fafc",
    "system_neutral2_50_dark": "0xfff1f5f9",
    "system_neutral2_100_dark": "0xffe2e8f0",
    "system_neutral2_200_dark": "0xffcbd5e1",
    "system_neutral2_300_dark": "0xff94a3b8",
    "system_neutral2_400_dark": "0xff64748b",
    "system_neutral2_500_dark": "0xff475569",
    "system_neutral2_600_dark": "0xff334155",
    "system_neutral2_700_dark": "0xff1e293b",
    "system_neutral2_800_dark": "0xff141d2e",
    "system_neutral2_900_dark": "0xff0b0f19",
    "system_neutral2_1000_dark": "0xff000000",
}

print(f"Fabricating {len(framework_colors)} system color overlays...")
for res_name, hex_val in framework_colors.items():
    overlay_name = f"neo_{res_name}"
    # Fabricate
    cmd = f"cmd overlay fabricate --target android --name {overlay_name} android:color/{res_name} 0x1c {hex_val}"
    out, err, code = run_adb(cmd)
    
    # Enable
    pkg = f"com.android.shell:{overlay_name}"
    enable_cmd = f"cmd overlay enable {pkg}"
    out, err, code = run_adb(enable_cmd)
    if code == 0:
        print(f"  [OK] {res_name} -> {hex_val}")
    else:
        print(f"  [FAIL] {res_name}: {err or out}")

# SystemUI Overlays
systemui_colors = {
    "status_bar_clock_color": "0xffffffff",
    "notification_legacy_background_color": "0xff141d2e",
    "notification_scrim_base": "0xff0b0f19",
    "notification_scrim_fallback": "0xff0b0f19",
}

print("\nFabricating SystemUI overlays...")
for res_name, hex_val in systemui_colors.items():
    overlay_name = f"neo_sysui_{res_name}"
    cmd = f"cmd overlay fabricate --target com.android.systemui --name {overlay_name} com.android.systemui:color/{res_name} 0x1c {hex_val}"
    out, err, code = run_adb(cmd)
    
    pkg = f"com.android.shell:{overlay_name}"
    enable_cmd = f"cmd overlay enable {pkg}"
    out, err, code = run_adb(enable_cmd)
    if code == 0:
        print(f"  [OK] sysui:{res_name} -> {hex_val}")
    else:
        print(f"  [FAIL] sysui:{res_name}: {err or out}")

print("\nDone! Overlays installed.")
