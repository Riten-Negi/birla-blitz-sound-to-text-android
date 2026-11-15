# Birla Blitz: Android Acoustic Receiver & Voice Synthesizer

A standalone native Android application that receives high-frequency acoustic data signals transmitted through laptop speakers, reconstructs planetary Earth observation telemetry in real time, and narrates the scientific findings aloud using native voice synthesis.

Developed as part of the **NASA Space Apps Challenge / Earth Information Center (EIC)** initiative to explore multimodal sonification and accessible science communication for blind and visually impaired users.

---

## Overview

Traditional data transfer relies on Wi-Fi, Bluetooth, cellular data, or physical cables. Birla Blitz operates entirely **over-the-air via acoustic soundwaves**, using the device microphone to detect and demodulate high-speed Frequency Shift Keying (FSK) audio bursts emitted by the companion web transmitter.

The application is completely **self-contained and 100% offline**:
* No cloud servers or backend databases.
* No API keys or authentication requirements.
* No internet or cellular connectivity needed during operation.

```
[Laptop Speaker]  ═══ (Acoustic Sound Waves) ═══►  [Phone Microphone]
                                                          │
                                                    [Web Audio API]
                                                          │
                                                [WASM Demodulator]
                                                          │
                                            ┌─────────────┴─────────────┐
                                            ▼                           ▼
                                  [Planetary Telemetry]        [Optical Payload]
                                   • SVS 5147 (Temp)            • General Visuals
                                   • SVS 5148 (Wind)            • Luminance Matrix
                                   • SVS 5149 (Precip)          • Optical Inspection
                                   • SVS 5150 (Humid)                   │
                                            │                           │
                                            └─────────────┬─────────────┘
                                                          ▼
                                            [Spoken Audio Narration]
                                            (Android Native TTS Engine)
```

---

## Key Capabilities

### 1. Real-Time Acoustic Demodulation
* Captures microphone input at 44.1 kHz / 48 kHz.
* Uses an embedded WebAssembly signal processing core (`acoustic-modem.js`) running a Discrete Fourier Transform (DFT) filter bank to detect FSK tone frequencies.
* Demodulates error-corrected data payloads from acoustic bursts into structured telemetry codewords.

### 2. Live Spectrogram & Oscilloscope
* Renders continuous visual audio waveform oscilloscopes tracking ambient sound levels and incoming acoustic bursts.
* Provides visual feedback for signal lock and packet reception status.

### 3. Dual Payload Telemetry Engine
* **Planetary Earth Observations:** When an atmospheric dataset token is received (`TEMP`, `WIND`, `RAIN`, `HUMID`), the app renders high-resolution NASA satellite observation maps, multi-frame timeline scrubbers, calibrated measurement scales, and scientific summaries:
  * **Global Air Temperature:** Sampling 2m surface temperature extremes (-40°C to +50°C).
  * **Near-Surface Wind Velocity:** Tracking 10m velocity belts from gentle breezes to storm-force gales (0–35+ m/s).
  * **Precipitation & Clouds:** Measuring rainfall intensity and convective cloud cores (0.1–30+ mm/hr).
  * **Specific Atmospheric Humidity:** Mapping water vapor mass concentrations (0–24+ g/kg).
* **Standard Optical Visual Imagery:** When non-planetary or custom visual tokens are received (`OPTICAL`), the app surfaces a clean general image card, bypassing atmospheric models and displaying optical matrix details.

### 4. Accessible Voice Narration
* Integrates directly with Android's native `android.speech.tts.TextToSpeech` engine via an internal Java bridge.
* Automatically speaks the complete scientific observation or optical summary aloud as soon as a transmission is decoded.
* Includes on-screen voice controls (Replay / Stop) allowing users to pause or repeat the spoken narrative on demand.

---

## Technical Architecture

The app is built as a lightweight, performant native Android wrapper embedding a hardware-accelerated WebView:

```
BirlaBlitzSoundToText.apk (3.18 MB)
├── Android Native Layer (Java 8)
│   ├── MainActivity.java           # Window manager, audio permissions & lifecycle
│   ├── LocalAssetServer.java       # Internal loopback asset server (127.0.0.1)
│   └── WebAppInterface.java        # Native TextToSpeech accessibility bridge
│
├── WebAssembly & Signal Layer
│   ├── acoustic-modem.js           # Compiled FSK audio demodulation core
│   └── datasets.js                 # NASA SVS dataset manifest & telemetry models
│
└── Frontend Interface Layer
    ├── phone.html                  # Responsive receiver UI & oscilloscope canvas
    └── assets/images/              # Pre-packaged satellite observation frames & colorbars
```

### Why the Internal Loopback Server?
Standard Android WebViews loading from `file:///android_asset/` enforce strict origin sandboxing (`origin = null`), which blocks modern browser features like `AudioContext` and WebAssembly streaming. To overcome this completely offline without any internet access, `MainActivity.java` runs a zero-latency internal HTTP loopback server on `127.0.0.1` that streams packaged assets directly from the APK's compressed storage.

---

## Repository Structure

```
.
├── AndroidManifest.xml             # Android permissions, hardware features & activities
├── build.bat                       # Windows one-click compilation launcher
├── build_apk.py                    # Standalone CLI build pipeline
├── .gitignore                      # Git ignore rules for build artifacts
├── README.md                       # Documentation
├── release/
│   └── BirlaBlitzSoundToText.apk   # Pre-compiled, signed production APK (3.18 MB)
├── src/
│   └── com/birlablitz/soundtotext/
│       └── MainActivity.java       # Native WebView, loopback server & TTS bridge
├── res/
│   ├── mipmap-hdpi/ic_launcher.png
│   ├── mipmap-xxhdpi/ic_launcher.png
│   └── values/
│       ├── strings.xml
│       └── styles.xml
└── assets/
    ├── phone.html                  # Receiver UI & waveform visualizer
    ├── js/
    │   ├── acoustic-modem.js       # WebAssembly FSK demodulator
    │   └── datasets.js             # NASA SVS dataset catalog & telemetry data
    └── images/                     # Pre-packaged satellite observation frames & scales
```

---

## Building from Source

A pre-compiled, cryptographically signed APK is already provided in `release/BirlaBlitzSoundToText.apk`.

To compile the APK from source using the included automated build pipeline:

### Prerequisites
* Python 3.8+
* Android SDK Build Tools (`aapt2`, `d8`, `zipalign`, `apksigner`) & JDK 17

### Build Commands
* **Windows (1-Click):** Double-click `build.bat`
* **Command Line:**
  ```bash
  python build_apk.py
  ```

The pipeline automatically compiles XML resources, builds Java sources, dexes classes into Dalvik bytecode, aligns to 4-byte boundaries, and cryptographically signs the output APK using `apksigner`.

---

## Permissions

| Permission | Purpose |
| :--- | :--- |
| `android.permission.RECORD_AUDIO` | Required to capture high-frequency sound waves through device microphone. |
| `android.permission.MODIFY_AUDIO_SETTINGS` | Ensures audio sampling rates (44.1 kHz / 48 kHz) are configured for acoustic demodulation. |
| `android.permission.INTERNET` | Allows localhost loopback binding (`127.0.0.1`) for the internal offline asset server. |

---

## System Requirements

* **Operating System:** Android 5.0 (Lollipop, API Level 21) or higher.
* **Architecture:** Universal (ARMv7, ARM64, x86, x86_64).
* **Hardware:** Any functional device microphone and speaker.
