# ⚡ Birla Blitz: Sound-to-Text Acoustic Receiver (Android)

> **Standalone Android App for High-Frequency Acoustic Signal Reception, Real-Time Spectrogram Analysis, Optical Reconstruction, and Accessible Voice Narration.**

[![Platform](https://img.shields.io/badge/Platform-Android_5.0%2B_(API_21%2B)-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Engine](https://img.shields.io/badge/Engine-Native_WebView_%2B_WASM-blue?style=flat)](https://webassembly.org)
[![Privacy](https://img.shields.io/badge/Privacy-100%25_Offline_•_Zero_Cloud_APIs-success?style=flat)](https://github.com)
[![License](https://img.shields.io/badge/License-MIT-purple?style=flat)](LICENSE)

---

## 📖 Overview

**Birla Blitz Sound to Text (Android)** is a native Android application engineered to capture sound waves transmitted through air via laptop or external speakers, demodulate encoded FSK (Frequency Shift Keying) acoustic data bursts in real time through the phone's microphone, and reconstruct visual image telemetry accompanied by spoken voice narration.

The app is **100% offline**, requiring **no cloud accounts, no API keys, and no internet access** once installed.

---

## 🛠️ How We Built the APK

### 1. High-Performance Hybrid Architecture
Rather than relying on heavy frameworks, this application uses a lightweight, high-performance hybrid architecture combining native Android runtime with WebAssembly:

```
┌────────────────────────────────────────────────────────┐
│             Birla Blitz Android Native Wrapper         │
│                                                        │
│  ┌──────────────────────┐    ┌──────────────────────┐  │
│  │ LocalAssetServer     │    │ AndroidTTS Bridge    │  │
│  │ (127.0.0.1 Loopback) │    │ (Native Speech TTS)  │  │
│  └──────────┬───────────┘    └──────────▲───────────┘  │
│             │                           │              │
│  ┌──────────▼───────────────────────────┴───────────┐  │
│  │   Hardware-Accelerated Android WebView           │  │
│  │                                                  │  │
│  │   • phone.html UI & Waveform Canvas              │  │
│  │   • Web Audio API Microphone Stream Capture      │  │
│  │   • acoustic-modem.js (High-Speed WASM Core)     │  │
│  │   • Dynamic Earth Observation / Optical Router   │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

### 2. The Internal Zero-Latency Loopback Server (`LocalAssetServer`)
Standard Android WebViews loading from `file:///android_asset/` enforce strict origin sandboxing (`origin = null`), which blocks modern browser features like `AudioContext`, WebAssembly streaming, and microphone permissions.

To eliminate this barrier without requiring external internet or remote servers:
- `MainActivity.java` initializes an embedded micro-HTTP loopback server (`LocalAssetServer`) running on `127.0.0.1:<random_port>`.
- The loopback server streams static assets (`phone.html`, WebAssembly binaries, datasets, images) directly from the APK's compressed `AssetManager`.
- This gives the WebView a valid `http://127.0.0.1` origin, enabling full Web Audio API, WebAssembly, and native microphone access.

### 3. Native Microphone Permission & Audio Pipeline
- The app requests Android native `RECORD_AUDIO` and `MODIFY_AUDIO_SETTINGS` permissions.
- In `MainActivity.java`, `WebChromeClient.onPermissionRequest()` intercepts Web Audio permission requests and grants them automatically once native system permission is approved by the user.
- A callback `onNativeMicPermissionGranted()` bridges the native permission state back to the frontend JavaScript.

### 4. Acoustic Demodulation via WebAssembly
- Captures microphone audio samples at 44.1 kHz / 48 kHz.
- Feeds raw audio frames into `acoustic-modem.js`, which runs a Discrete Fourier Transform (DFT) filter bank to detect ultrasonic and near-audible FSK tone bursts.
- Decodes error-corrected data packets into codewords (`TEMP`, `WIND`, `RAIN`, `HUMID`, or `OPTICAL`).

### 5. Native Spoken Voice Narration Bridge (`WebAppInterface`)
- The app exposes a native Java interface to JavaScript via `window.AndroidTTS`.
- When an acoustic signal is decoded, the app triggers Android's native `android.speech.tts.TextToSpeech` engine to read aloud the scientific or optical explanation, providing complete accessibility for blind or visually impaired users without any third-party cloud speech services.

### 6. Intelligent Payload Routing
- **NASA Earth Observation Imagery:** Codewords `TEMP` (SVS 5147), `WIND` (SVS 5148), `RAIN` (SVS 5149), and `HUMID` (SVS 5150) render calibrated satellite observation maps and telemetry.
- **Standard Visual Imagery:** Custom or non-Earth images (portraits, personal photos, general images) trigger the dedicated **Standard Visual Image** profile, disabling planetary atmospheric readings and providing neutral optical matrix feedback.

---

## 🗂️ Project Structure

```
BirlaBlitz_SoundToText_Android_App/
├── AndroidManifest.xml             # App configuration, permissions & activities
├── build.bat                       # 1-Click Windows build script
├── build_apk.py                    # Standalone CLI compilation pipeline
├── .gitignore                      # Git ignore rules for Android artifacts
├── README.md                       # Complete documentation
├── release/
│   └── BirlaBlitzSoundToText.apk   # Pre-compiled, signed production APK
├── src/
│   └── com/
│       └── birlablitz/
│           └── soundtotext/
│               └── MainActivity.java # Native WebView, Local server & TTS Bridge
├── res/
│   ├── mipmap-hdpi/
│   │   └── ic_launcher.png         # High-resolution launcher icon
│   ├── mipmap-xxhdpi/
│   │   └── ic_launcher.png         # Extra-high resolution launcher icon
│   └── values/
│       ├── strings.xml             # App name & UI strings
│       └── styles.xml              # Material full-screen themes
└── assets/
    ├── phone.html                  # Responsive mobile acoustic receiver UI
    ├── js/
    │   ├── acoustic-modem.js       # WebAssembly FSK acoustic core
    │   └── datasets.js             # NASA SVS dataset catalog & telemetry data
    ├── images/                     # Pre-packaged satellite observation frames & scales
    └── assets/                     # PWA icons & branding assets
```

---

## 🔨 How to Build the APK from Source

### Prerequisites
- Python 3.8+ (for running the automated build pipeline)
- Android SDK Build Tools (aapt2, d8, zipalign, apksigner) & JDK 17

### Option 1: One-Click Build (Windows)
Double-click `build.bat` in the repository root.

### Option 2: Python Command Line
```bash
python build_apk.py
```
The script will execute:
1. `aapt2 compile`: Compiles Android XML layouts and resources into `.flat` files.
2. `aapt2 link`: Generates `R.java`, links assets, and produces `unaligned.apk`.
3. `javac`: Compiles `MainActivity.java` and `R.java` targeting Java 1.8 bytecode.
4. `d8`: Dexes compiled Java `.class` files into `classes.dex`.
5. `zip`: Packages `classes.dex` into the APK.
6. `zipalign`: Performs 4-byte boundary alignment.
7. `apksigner`: Signs the APK with Scheme v1, v2, and v3 signatures.

Output binary will be located in:
```
release/BirlaBlitzSoundToText.apk
```

---

## 🚀 How to Upload this Code to a Separate GitHub Repo

To publish this standalone Android app in its own dedicated GitHub repository:

### Step 1: Create a New GitHub Repository
1. Go to [GitHub New Repository](https://github.com/new).
2. Set the repository name (e.g., `birla-blitz-sound-to-text-android` or `birla-blitz-apk`).
3. Set visibility to **Public** (or Private).
4. Do **not** initialize with a README, .gitignore, or license (they are already included).
5. Click **Create repository**.

### Step 2: Initialize Git and Push
Open PowerShell or Terminal inside this folder:

```bash
# Navigate to the Android App directory
cd "C:\Users\naiti\OneDrive\Desktop\BirlaBlitz_SoundToText_Android_App"

# Initialize git repository
git init

# Add all files to staging
git add .

# Create initial commit
git commit -m "feat: initial commit of Birla Blitz standalone Android APK repository"

# Set branch to main
git branch -M main

# Add your new remote repository URL (replace with your repo URL)
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPO_NAME>.git

# Push code to GitHub
git push -u origin main
```

---

## 📱 Permissions Used

| Permission | Purpose |
| :--- | :--- |
| `android.permission.RECORD_AUDIO` | Required to capture high-frequency sound waves through device microphone. |
| `android.permission.MODIFY_AUDIO_SETTINGS` | Ensures audio sampling rates (44.1 kHz / 48 kHz) are optimized for acoustic demodulation. |
| `android.permission.INTERNET` | Allows localhost loopback binding (`127.0.0.1`) for the internal asset server. |

---

## 📄 License
This project is licensed under the MIT License - feel free to inspect, modify, and distribute.
