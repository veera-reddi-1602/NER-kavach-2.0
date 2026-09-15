# NER KAVACH 3.0 — Bill of Materials (BOM) & Cost Comparison

## 1. Node Cost Breakdown (Rs. 4,500 vs Traditional Rs. 2,00,000)

Traditional Geological Survey of India (GSI) / State Disaster Management Authority (SDMA) automated weather & borehole stations cost upwards of **Rs. 2,00,000 to Rs. 5,00,000 per node** and require active 4G grid coverage. 

NER KAVACH 3.0 uses an ultra-low-cost, solar-powered, 10km LoRa mesh architecture costing **Rs. 4,500 (~44x cheaper)**:

| Component | Specification / Part Number | Unit Cost (INR) |
|---|---|---|
| **Microcontroller (MCU)** | ESP32-WROOM-32D Dual Core 240MHz (BLE + Wi-Fi) | ₹400 |
| **LoRa Transceiver** | Semtech SX1276 (868 MHz Long Range 10km) | ₹500 |
| **Soil Saturation Sensor** | Capacitive Soil Moisture Sensor v1.2 (Corrosion Resistant) | ₹250 |
| **6-DoF Tilt / Vibration IMU**| InvenSense MPU6050 (Slope Scarp Motion Detector) | ₹150 |
| **Solar Power Harvester** | 5W 6V Monocrystalline Solar Panel + TP4056 BMS | ₹500 |
| **Battery Storage** | 3.7V 3400mAh Panasonic 18650 Li-ion Cell (2-Year Life) | ₹300 |
| **Weatherproof Enclosure** | IP65 3D-Printed Conformal Coated Chassis (Monkey-Proof) | ₹800 |
| **Piezo Siren / LED Strobe** | 85dB Active Buzzer + High-Lumen Amber Alert LED | ₹100 |
| **Custom 2-Layer PCB** | FR4 Double Sided SMD Custom Breakout Board | ₹600 |
| **Assembly & Cabling** | RG58 LoRa Antenna pigtail + Ground Stake Mounts | ₹900 |
| **TOTAL PER SENSOR NODE** | **Complete Ruggedized Edge Hardware Unit** | **₹4,500** |

---

## 2. Gateway Node Cost (1 Gateway Covers 10-15 Villages)

| Component | Specification | Unit Cost (INR) |
|---|---|---|
| **SBC Processor** | Raspberry Pi 4 Model B (4GB RAM) / Pi Zero 2W | ₹4,200 |
| **Multi-Channel LoRa Concentrator**| RAK2287 8-Channel LoRaWAN SPI Gateway Hat | ₹7,500 |
| **Solar Subsystem** | 20W Solar Panel + 12V 12Ah LiFePO4 Battery + MPPT | ₹4,800 |
| **Local Wi-Fi AP Module** | High-Gain Omni Antenna (Hostapd Hotspot 192.168.1.1) | ₹1,500 |
| **TOTAL PER GATEWAY** | **Autonomous Solar LoRa Hub (Serves 15km Valley)** | **₹18,000** |

---

## 3. Cost-to-Coverage Comparison for MDoNER Budget (50 Crore Allocation)

- **Traditional Systems:** 50 Cr budget covers ~2,500 points (insufficient for 8,000 NER villages).
- **NER KAVACH 3.0:** 50 Cr budget deploys **111,000+ sensor nodes**, covering every single remote revenue village across Meghalaya, Arunachal Pradesh, and Sikkim with 100% density.
