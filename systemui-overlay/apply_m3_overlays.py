import subprocess

ADB = r"C:\Users\Salman Toha\AppData\Local\Microsoft\WinGet\Packages\Google.PlatformTools_Microsoft.Winget.Source_8wekyb3d8bbwe\platform-tools\adb.exe"

def run_adb(cmd):
    full_cmd = [ADB, "-s", "emulator-5554", "shell"] + cmd.split(" ")
    res = subprocess.run(full_cmd, capture_output=True, text=True)
    return res.stdout.strip(), res.stderr.strip(), res.returncode

m3_colors = {
    # M3 Semantic Tokens (Dark)
    "system_primary_dark": "0xff6366f1",                # Electric Indigo active tile
    "system_on_primary_dark": "0xffffffff",             # Crisp white icon/text
    "system_primary_container_dark": "0xff312e81",
    "system_on_primary_container_dark": "0xffc7d2fe",
    "system_secondary_dark": "0xfffbbf24",              # Solar Amber
    "system_on_secondary_dark": "0xff000000",
    "system_secondary_container_dark": "0xff78350f",
    "system_on_secondary_container_dark": "0xfffde68a",
    "system_tertiary_dark": "0xff34d399",               # Emerald Mint
    "system_on_tertiary_dark": "0xff000000",
    "system_tertiary_container_dark": "0xff064e3b",
    "system_on_tertiary_container_dark": "0xffa7f3d0",
    
    # Surfaces & Cards (Deep Dark Slate Neo-Brutalism)
    "system_background_dark": "0xff0b0f19",
    "system_on_background_dark": "0xfff8fafc",
    "system_surface_dark": "0xff0b0f19",
    "system_on_surface_dark": "0xfff8fafc",
    "system_surface_container_lowest_dark": "0xff070a12",
    "system_surface_container_low_dark": "0xff0e1422",
    "system_surface_container_dark": "0xff141d2e",      # Inactive QS tile & Card
    "system_surface_container_high_dark": "0xff1e293b",
    "system_surface_container_highest_dark": "0xff334155",
    "system_surface_bright_dark": "0xff1e293b",
    "system_surface_dim_dark": "0xff0b0f19",
    "system_surface_variant_dark": "0xff141d2e",
    "system_on_surface_variant_dark": "0xffcbd5e1",
    "system_outline_dark": "0xff475569",                # Card border stroke
    "system_outline_variant_dark": "0xff334155",
    
    # Controls
    "system_control_activated_dark": "0xff6366f1",
    "system_control_normal_dark": "0xff94a3b8",
    "system_control_highlight_dark": "0xff818cf8",
    "system_scrim_dark": "0xe60b0f19",                  # 90% opacity deep dark shade scrim
}

print(f"Fabricating {len(m3_colors)} M3 system color overlays...")
for res_name, hex_val in m3_colors.items():
    overlay_name = f"neo_m3_{res_name}"
    cmd = f"cmd overlay fabricate --target android --name {overlay_name} android:color/{res_name} 0x1c {hex_val}"
    out, err, code = run_adb(cmd)
    
    pkg = f"com.android.shell:{overlay_name}"
    enable_cmd = f"cmd overlay enable {pkg}"
    out, err, code = run_adb(enable_cmd)
    if code == 0:
        print(f"  [OK] {res_name} -> {hex_val}")
    else:
        print(f"  [FAIL] {res_name}: {err or out}")

print("\nDone!")
