import os
import sys
import subprocess
import shutil
import zipfile

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# 1. Detect Android Toolchain
TOOLCHAIN_DEFAULT = r"C:\Users\naiti\.gemini\antigravity\scratch\android-toolchain"
if os.path.exists(TOOLCHAIN_DEFAULT):
    TOOLCHAIN = TOOLCHAIN_DEFAULT
else:
    TOOLCHAIN = os.environ.get("ANDROID_TOOLCHAIN", os.path.join(BASE_DIR, "toolchain"))

BUILD_TOOLS = os.path.join(TOOLCHAIN, "build-tools", "android-14")
JDK = os.path.join(TOOLCHAIN, "jdk", "jdk-17.0.10+7")
PLATFORM = os.path.join(TOOLCHAIN, "platform", "android-13")

AAPT2 = os.path.join(BUILD_TOOLS, "aapt2.exe")
D8 = os.path.join(BUILD_TOOLS, "d8.bat")
ZIPALIGN = os.path.join(BUILD_TOOLS, "zipalign.exe")
APKSIGNER = os.path.join(BUILD_TOOLS, "lib", "apksigner.jar")
JAVAC = os.path.join(JDK, "bin", "javac.exe")
JAVA = os.path.join(JDK, "bin", "java.exe")
KEYSTORE = os.path.join(TOOLCHAIN, "debug.keystore")
ANDROID_JAR = os.path.join(PLATFORM, "android.jar")

OUTPUT_APK = os.path.join(BASE_DIR, "release", "BirlaBlitzSoundToText.apk")

def run_cmd(cmd, desc, env=None):
    print(f"\n[STEP] {desc}...")
    res = subprocess.run(cmd, cwd=BASE_DIR, env=env, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if res.returncode != 0:
        print(f"FAILED (Exit {res.returncode}):")
        print("STDOUT:", res.stdout)
        print("STDERR:", res.stderr)
        sys.exit(1)
    else:
        print("  OK.")

def main():
    print("=" * 65)
    print("  Compiling Birla Blitz Sound-to-Text Standalone Android APK")
    print("=" * 65)

    if not os.path.exists(TOOLCHAIN):
        print(f"ERROR: Toolchain not found at: {TOOLCHAIN}")
        print("Please ensure the android-toolchain is installed or set ANDROID_TOOLCHAIN.")
        sys.exit(1)

    # Clean previous build artifacts
    for d in ["build", "gen", "classes", "dex"]:
        p = os.path.join(BASE_DIR, d)
        if os.path.exists(p):
            shutil.rmtree(p)
    os.makedirs(os.path.join(BASE_DIR, "build"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "gen"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "classes"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "dex"), exist_ok=True)
    os.makedirs(os.path.join(BASE_DIR, "release"), exist_ok=True)

    # 1. Compile resources
    compiled_res = os.path.join(BASE_DIR, "build", "compiled_res.zip")
    run_cmd([AAPT2, "compile", "--dir", "res", "-o", compiled_res], "Compiling XML & Image Resources")

    # 2. Link resources & assets
    manifest = os.path.join(BASE_DIR, "AndroidManifest.xml")
    unaligned_apk = os.path.join(BASE_DIR, "build", "unaligned.apk")
    assets_dir = os.path.join(BASE_DIR, "assets")
    run_cmd([
        AAPT2, "link",
        "-I", ANDROID_JAR,
        "--manifest", manifest,
        "--min-sdk-version", "21",
        "--target-sdk-version", "34",
        "-o", unaligned_apk,
        "--java", os.path.join(BASE_DIR, "gen"),
        "-A", assets_dir,
        compiled_res
    ], "Linking Resources, Assets & Generating R.java")

    # 3. Compile Java Source Code
    r_java = os.path.join(BASE_DIR, "gen", "com", "birlablitz", "soundtotext", "R.java")
    main_java = os.path.join(BASE_DIR, "src", "com", "birlablitz", "soundtotext", "MainActivity.java")
    run_cmd([
        JAVAC,
        "-source", "1.8",
        "-target", "1.8",
        "-cp", ANDROID_JAR,
        "-d", os.path.join(BASE_DIR, "classes"),
        r_java, main_java
    ], "Compiling Java Source Code (MainActivity.java)")

    # 4. Dexing classes with D8
    class_files = []
    for root, _, files in os.walk(os.path.join(BASE_DIR, "classes")):
        for f in files:
            if f.endswith(".class"):
                class_files.append(os.path.join(root, f))
    
    env = os.environ.copy()
    env["JAVA_HOME"] = JDK
    env["PATH"] = os.path.join(JDK, "bin") + os.pathsep + env.get("PATH", "")

    run_cmd([
        D8,
        "--min-api", "21",
        "--output", os.path.join(BASE_DIR, "dex"),
        "--lib", ANDROID_JAR,
        *class_files
    ], "Dexing Classes into classes.dex with D8", env=env)

    # 5. Add classes.dex into unaligned.apk
    dex_file = os.path.join(BASE_DIR, "dex", "classes.dex")
    print(f"\n[STEP] Packaging classes.dex into APK...")
    with zipfile.ZipFile(unaligned_apk, "a", compression=zipfile.ZIP_DEFLATED) as apk_zip:
        apk_zip.write(dex_file, "classes.dex")
    print("  classes.dex added successfully.")

    # 6. Zipalign APK
    aligned_apk = os.path.join(BASE_DIR, "build", "aligned.apk")
    run_cmd([
        ZIPALIGN,
        "-p", "-f", "4",
        unaligned_apk,
        aligned_apk
    ], "Zipaligning APK (4-byte alignment)")

    # 7. Sign APK with apksigner
    run_cmd([
        JAVA,
        "-jar", APKSIGNER,
        "sign",
        "--ks", KEYSTORE,
        "--ks-pass", "pass:android",
        "--ks-key-alias", "androiddebugkey",
        "--key-pass", "pass:android",
        "--out", OUTPUT_APK,
        aligned_apk
    ], "Signing APK with Debug Keystore")

    # 8. Verify APK Signature
    run_cmd([
        JAVA,
        "-jar", APKSIGNER,
        "verify",
        "--verbose",
        OUTPUT_APK
    ], "Verifying APK Signature & Integrity")

    apk_size = os.path.getsize(OUTPUT_APK) / (1024 * 1024)
    print("\n" + "=" * 65)
    print(f"[SUCCESS] Standalone APK Built Successfully!")
    print(f"Output File: {OUTPUT_APK}")
    print(f"File Size:   {apk_size:.2f} MB")
    print("=" * 65)

if __name__ == "__main__":
    main()
