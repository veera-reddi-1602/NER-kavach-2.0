# NER KAVACH 3.0 — Offline-First Mesh & Gateway Architecture

## 1. Physical Layer Architecture (No-Signal Remote Valley Protocol)

```
[Slope Sensor Node 1] ──┐
                         │ (LoRa SX1276 868MHz / 1km-10km Mesh)
[Slope Sensor Node 2] ──┼──► [Solar Raspberry Pi Gateway @ Collector Office / ITBP Post]
                         │     ├─► SQLite Local Ring Buffer (Stores 7-day readings)
[Slope Sensor Node 3] ──┘     ├─► Local AI Inference Core (Same XGBoost Model)
                               ├─► Hostapd Wi-Fi Hotspot: "NER-KAVACH-LOCAL" (192.168.1.1)
                               ├─► Physical 85dB Siren / Relay Buzzer + Flashing Strobe
                               └─► Offline PWA UI served to Connected Smartphone Browsers
```

## 2. Synchronization Lifecycle (PDF Section 10 & 12 Compliant)

1. **Monsoon Disconnection Phase:**
   - Cellular tower goes down due to storm/rockfall in Anini or Lachung.
   - PWA detects network drop and activates **Automatic Offline Mode**.
   - UI immediately flashes the mandatory **Red Cached Warning Banner**:
     `"OFFLINE MODE: Internet unavailable; Using cached forecast from 8:15 PM; Weather data is not currently live."`
   - Real-time IoT sensor readings continue transmitting over LoRa to the local gateway and buffer in SQLite.
   - If critical thresholds are crossed (Rain >350mm + Soil >85% + InSAR >5mm), the local gateway immediately activates the **85dB Siren and local hotspot dashboard alert**.

2. **Network Restoration Phase:**
   - Cellular/VSAT signal returns.
   - The Gateway / PWA detects internet restoration and issues a batch `POST /api/offline/sync`.
   - All timestamped buffered sensor readings (e.g. 12 readings) and incident logs are uploaded to PostgreSQL/TimescaleDB.
   - Latest IMD weather radar forecasts are fetched and cached.
   - Pending WhatsApp/SMS text notifications are dispatched to state disaster registers for audit records.
