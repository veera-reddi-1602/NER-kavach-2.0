# NER KAVACH 3.0 — IoT Edge Node Circuit Schematic & Pinout

## 1. ESP32-WROOM-32 Pin Mapping

| ESP32 GPIO | Connected Component | Functionality / Protocol |
|---|---|---|
| **GPIO 18** | SX1276 LoRa `NSS / CS` | SPI Chip Select |
| **GPIO 23** | SX1276 LoRa `MOSI` | SPI Master Output |
| **GPIO 19** | SX1276 LoRa `MISO` | SPI Master Input |
| **GPIO 5**  | SX1276 LoRa `SCK` | SPI Clock |
| **GPIO 14** | SX1276 LoRa `RST` | Hardware Reset |
| **GPIO 26** | SX1276 LoRa `DIO0` | Packet RX/TX Interrupt |
| **GPIO 21** | MPU6050 `SDA` | I2C Data (Tilt / Vibration) |
| **GPIO 22** | MPU6050 `SCL` | I2C Clock |
| **GPIO 35** | Capacitive Soil Moisture `AOUT` | Analog ADC1 (0-3.3V) |
| **GPIO 34** | Tipping Bucket Rain Gauge | Digital Pulse Interrupt |
| **GPIO 36** | 18650 Battery Voltage Divider | 100kΩ / 100kΩ Resistor Divider |
| **GPIO 25** | Piezo Siren / Relay | 85dB Audio Alert Trigger |
| **GPIO 2**  | High-Lumen Amber LED | Visual Beacon Strobe |

---

## 2. Solar Harvesting & Power Management

```
[5W 6V Solar Panel]
         │
         ▼
[TP4056 BMS with Over-Discharge Protection (DW01A)]
         │
         ▼
[3.7V 3400mAh 18650 Li-ion Battery]
         │
         ▼
[ME6211 Ultra-Low Quiescent LDO 3.3V Regulator (40uA Iq)]
         │
         ▼
[ESP32 3V3 Rail & Sensors]
```

- **Active Measurement Mode:** 120 mA (5 seconds duration).
- **LoRa Packet Transmission:** 90 mA (0.8 seconds duration).
- **Deep Sleep Mode:** 10 µA (55 minutes duration).
- **Autonomy:** Operates indefinitely with 3 hours of sunlight per week; 45 days runtime on battery alone during heavy cloud cover.
