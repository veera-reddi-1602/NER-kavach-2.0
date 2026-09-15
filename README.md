# 🛡️ NER KAVACH 3.0 — AI Landslide Early Warning & Risk Monitoring System

> **Targeted for Smart India Hackathon (SIH) — MDoNER Problem Statement SIH26001**
> Covers 15 High-Risk Villages across **Meghalaya, Arunachal Pradesh, and Sikkim** with a **100% PDF Section 12 Compliant Offline-First Architecture**.

[![Live Web App](https://img.shields.io/badge/Live_Web_App-GitHub_Pages-blue?style=for-the-badge&logo=github)](https://veera-reddi-1602.github.io/nerkavach/)
[![Android APK](https://img.shields.io/badge/Android_APK-v3.0_Signed-green?style=for-the-badge&logo=android)](https://github.com/veera-reddi-1602/NER-kavach-2.0/blob/main/android_native/NER_KAVACH_3.0.apk)
[![Python Backend](https://img.shields.io/badge/Backend-FastAPI_Python_3.10+-yellow?style=for-the-badge&logo=python)](https://github.com/veera-reddi-1602/NER-kavach-2.0/tree/main/backend)
[![IoT Hardware](https://img.shields.io/badge/IoT-ESP32_LoRa_868MHz-red?style=for-the-badge&logo=arduino)](https://github.com/veera-reddi-1602/NER-kavach-2.0/tree/main/iot)

---

## 🚀 Key Innovations & Core Architecture

1. **Offline-Resilient Mesh Network:** 10km LoRa (868 MHz) + BLE 5.2 + Local Gateway Hotspot (`http://192.168.1.1`) operating for 15+ days when cellular backhaul collapses.
2. **Real AI Core with SHAP Factor Analysis:** Trained Gradient Boosting & Random Forest ensemble on 650+ North-East Region (NER) geotechnical records (90% accuracy, 87% ±5% confidence, 5 SHAP factors).
3. **PDF Section 12 Compliance:** Explicit Mode Selector (Online / Offline / Manual), Live Connectivity Status, Weather Timestamps, and Amber/Red Banner for cached data.
4. **Dijkstra Safe Route Engine:** Dynamically reroutes evacuation convoys away from active landslides on NH-6, NH-13, and NH-10 with elevation slope cost weighting.
5. **Micro-Evacuation Household Prioritization:** Multi-factor ranking formula + instant downloadable PDF Evacuation Manifest with priority tagging.
6. **12-Language Bhashini AI:** Instant translation & voice synthesis in English, Hindi, Khasi, Adi, Bhutia, Nepali, and regional dialects.
7. **85dB Local Gateway Acoustic Siren & SOS Strobe:** Web Audio API sound generator + hardware relay simulation + AMOLED blackout flashlight.
8. **Field Evacuation Compass HUD & LifeFinder Radar:** Real-time device magnetometer shelter bearing HUD + BLE survivor proximity tracking.
9. **YOLOv8 Hazard Verifier & Cryptographic Ledger:** Computer vision tension crack detection + tamper-proof SHA-256 audit log.
10. **Ultra-Low Cost Solar IoT Node:** ₹4,500 solar ESP32 node (44x cheaper than traditional ₹2,00,000 systems) with 10µA deep-sleep power budget.

---

## 📂 Project Directory Structure

```text
NER-kavach-2.0/
├── android_native/                   # Production Native Android App
│   ├── AndroidManifest.xml           # Permissions, services, broadcast receivers
│   ├── NER_KAVACH_3.0.apk            # Signed production APK binary ready to install
│   ├── ner_kavach_release.keystore   # Release signing keystore
│   ├── build_and_deploy.ps1          # 1-Click native compilation, DEX, signing & ADB script
│   ├── src/com/nerkavach/app/        # Native Java services & activities
│   │   ├── MainActivity.java         # Native WebView wrapper & hardware bridges
│   │   ├── WhatsAppAutoSendService.java # Accessibility automation for emergency WhatsApp dispatches
│   │   ├── AlertReceiverService.java # Background push alert daemon
│   │   └── EmergencyAlertActivity.java # Fullscreen lockscreen emergency HUD
│   └── assets/                       # Bundled offline assets (synced with frontend/)
├── android_app/                      # Jetpack Compose Modern Android App
│   ├── app/src/main/                 # Kotlin Jetpack Compose UI, ViewModels, Themes
│   └── build.gradle.kts              # Gradle configuration
├── backend/                          # FastAPI AI/ML & Analytics Backend
│   ├── app.py                        # REST API endpoints & static app mount
│   ├── ml_engine.py                  # XGBoost/GradientBoosting inference + SHAP
│   ├── routing_engine.py             # Dijkstra safe path calculation & blockage bypass
│   ├── evacuation_engine.py          # Household priority scoring & PDF generation
│   ├── yolo_verifier.py              # YOLOv8 hazard verification & SHA-256 ledger
│   ├── models/                       # Serialized trained model & metadata
│   │   ├── landslide_model.pkl
│   │   └── model_metadata.json
│   └── data/                         # Regional datasets (villages, routes, trends, training)
├── frontend/                         # Standalone Progressive Web App (PWA)
│   ├── index.html                    # 7-Tab Glassmorphism Web App
│   ├── style.css                     # Premium design system & animations
│   ├── app.js                        # Leaflet GIS, What-If simulator, LifeFinder, Compass
│   ├── i18n.js                       # Bhashini multilingual translation engine
│   ├── sw.js                         # Service Worker for 7-day offline caching
│   └── manifest.json                 # PWA install configuration
├── gateway/                          # Offline Mesh Gateway Service
│   ├── gateway_service.py            # Local HTTP server & LoRa queue sync
│   └── storage/                      # Offline reports & audio voice dispatches
├── iot/                              # IoT Sensor Firmware & Schematics
│   ├── esp32_firmware.ino            # ESP32 + LoRa SX1276 + Geotechnical sensors C++ code
│   ├── circuit_schematic.md          # Wiring diagrams & GPIO pinouts
│   └── bom_cost_analysis.md          # Bill of Materials analysis
├── docs/                             # Engineering & Presentation Documentation
│   ├── COMPLETE_FEATURE_AND_BUTTON_SPECIFICATION.md
│   ├── 10_DISADVANTAGES_SOLUTIONS.md
│   ├── OFFLINE_MESH_ARCHITECTURE.md
│   └── SIH_PRESENTATION_PITCH.md
├── tests/                            # Automated Test Suites
│   ├── test_ner_kavach.py            # Core engine unit tests
│   ├── test_api_endpoints.py         # REST API test cases
│   └── test_offline_gateway_reporting.py # Offline sync tests
├── requirements.txt                  # Python dependencies
├── run_server.py                     # Single-command launcher for backend & frontend
└── train_model.py                    # Model training & synthetic data generation script
```

---

## 🛠️ Developer Setup & Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/veera-reddi-1602/NER-kavach-2.0.git
cd NER-kavach-2.0
```

### 2. Set Up Python Environment & Run Backend
```bash
# Create and activate virtual environment
python -m venv venv
# On Windows:
venv\Scripts\activate
# On Linux/macOS:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run full server (Backend API + Frontend Web UI)
python run_server.py
```
Open **`http://127.0.0.1:8000`** in your browser.

---

### 3. Build & Install the Native Android APK

#### Prerequisites:
- Android SDK (`build-tools`, `platforms;android-34`)
- Java JDK 17+
- Android Device with USB Debugging enabled

#### 1-Click Build & Deploy:
```powershell
cd android_native
powershell -ExecutionPolicy Bypass -File .\build_and_deploy.ps1
```
The script will compile Java sources, package DEX with `d8`, align with `zipalign`, sign with `apksigner` using `ner_kavach_release.keystore`, and install directly via `adb`.

---

### 4. Running Automated Tests
```bash
python -m unittest discover tests
```

---

## 🌐 Live Deployments & Demo Links
- **Live Web App**: [https://veera-reddi-1602.github.io/nerkavach/](https://veera-reddi-1602.github.io/nerkavach/)
- **Repository**: [https://github.com/veera-reddi-1602/NER-kavach-2.0](https://github.com/veera-reddi-1602/NER-kavach-2.0)

---

## 📄 License & Attribution
Developed for **Smart India Hackathon (SIH)**. All rights reserved.
