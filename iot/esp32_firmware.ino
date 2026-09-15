/*
 * NER KAVACH 3.0 — IoT Edge Sensor Node Firmware
 * Target MCU: ESP32-WROOM-32D / ESP32-S3
 * Transceiver: Semtech SX1276 LoRa (868 MHz)
 * Sensors: Capacitive Soil Moisture v1.2, MPU6050 6-DoF IMU, Tipping Bucket
 * Rain Gauge Power: 5W Monocrystalline Solar Panel + TP4056 + 18650 Li-ion
 * 3400mAh (Deep Sleep: 10uA)
 */

#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <LoRa.h>
#include <SPI.h>
#include <Wire.h>

// Pin Definitions
#define LORA_SS 18
#define LORA_RST 14
#define LORA_DIO0 26

#define RAIN_PIN 34
#define SOIL_PIN 35
#define BATT_PIN 36
#define BUZZER_PIN 25
#define LED_PIN 2

// Node Identification
const char *NODE_ID = "SEN_ML_001";
const char *VILLAGE_NAME = "Cherrapunji";

Adafruit_MPU6050 mpu;
RTC_DATA_ATTR int bootCount = 0;
RTC_DATA_ATTR float rainAccum24h = 0.0;

void setup() {
  Serial.begin(115200);
  delay(500);
  ++bootCount;

  pinMode(LED_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(LED_PIN, HIGH);

  // Initialize I2C and MPU6050
  Wire.begin(21, 22);
  if (!mpu.begin()) {
    Serial.println("MPU6050 tilt sensor not detected!");
  } else {
    mpu.setAccelerometerRange(MPU6050_RANGE_4_G);
    mpu.setFilterBandwidth(MPU6050_BAND_21_HZ);
  }

  // Initialize LoRa Module (868 MHz Band for India / ISM)
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(868E6)) {
    Serial.println("LoRa initialization failed!");
  } else {
    LoRa.setTxPower(20); // 20 dBm for 10km mountain line-of-sight
    LoRa.setSpreadingFactor(11);
    LoRa.setSignalBandwidth(125E3);
  }

  // Read Telemetry
  float soilAnalog = analogRead(SOIL_PIN);
  float soilMoisturePct = map(soilAnalog, 3200, 1400, 0, 100);
  soilMoisturePct = constrain(soilMoisturePct, 0.0, 100.0);

  float battRaw = analogRead(BATT_PIN);
  float battVoltage = (battRaw / 4095.0) * 2.0 * 3.3 * 1.1; // Voltage divider
  int battPct = map((int)(battVoltage * 100), 320, 420, 0, 100);
  battPct = constrain(battPct, 0, 100);

  sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);
  float tilt_accel = sqrt(a.acceleration.x * a.acceleration.x +
                          a.acceleration.y * a.acceleration.y);

  // Check Local Siren Trigger (PDF Sec 6 & 8: Local Alert without Internet)
  if (soilMoisturePct > 85.0 || tilt_accel > 12.5) {
    // Sound local buzzer for 3 seconds
    digitalWrite(BUZZER_PIN, HIGH);
    delay(3000);
    digitalWrite(BUZZER_PIN, LOW);
  }

  // Transmit LoRa Mesh Packet to Raspberry Pi Gateway
  LoRa.beginPacket();
  LoRa.print("{\"node\":\"");
  LoRa.print(NODE_ID);
  LoRa.print("\",\"rain\":");
  LoRa.print(rainAccum24h);
  LoRa.print(",\"soil\":");
  LoRa.print(soilMoisturePct);
  LoRa.print(",\"tilt\":");
  LoRa.print(tilt_accel);
  LoRa.print(",\"batt\":");
  LoRa.print(battPct);
  LoRa.print(",\"boot\":");
  LoRa.print(bootCount);
  LoRa.print("}");
  LoRa.endPacket();

  digitalWrite(LED_PIN, LOW);

  // Configure Deep Sleep for 55 minutes (wakes up for 5 seconds -> 10uA
  // consumption)
  esp_sleep_enable_timer_wakeup(55ULL * 60ULL * 1000000ULL);
  esp_deep_sleep_start();
}

void loop() {
  // Never reached in Deep Sleep architecture
}
