# NER KAVACH 3.0 — Smart India Hackathon (SIH) Presentation Pitch

## 🎙️ Verbatim 2-Minute Elevator Pitch (Memorize for Judges)

> "Respected Judges, during the monsoon season across Meghalaya, Arunachal Pradesh, and Sikkim, landslides do not just block roads like NH-6 and NH-13 — they completely cut off electrical grids and cellular networks in remote mountain valleys like Anini and Lachung for up to 15 days.
>
> Traditional early warning systems fail when the risk is highest because they require active cloud connectivity, use expensive Rs. 2 Lakh sensors, and only support English or Hindi.
>
> **NER KAVACH 3.0** is an offline-resilient, AI-powered Landslide Early Warning and Risk Monitoring System engineered specifically for the North Eastern Region.
>
> **How it works:**
> 1. **In Online Mode:** It ingests live IMD rainfall grids, NASA SMAP soil moisture, and Sentinel-1 InSAR millimeter radar deformation to run our trained XGBoost AI Core with real SHAP explainability.
> 2. **In No-Signal Areas:** Our custom **Rs. 4,500 solar ESP32 IoT nodes** communicate over a **10km LoRa mesh** to a local solar Raspberry Pi gateway creating a local Wi-Fi hotspot (`192.168.1.1`). The exact same AI model runs locally on the gateway to trigger 85dB sirens, LED beacons, and local multilingual voice alerts in **6 NER languages (Khasi, Adi, Bhutia, Nepali, Hindi, English)** via Bhashini AI.
> 3. **When Evacuating:** Our **Dijkstra Safe Route Engine** automatically routes citizens around blocked highways like NH-6 and NH-10 with elevation slope smoothing, prioritizing vulnerable households with our GNN ranking algorithm.
>
> NER KAVACH 3.0 is 44 times cheaper, 100% offline-resilient, and gives collectors and villagers hours of actionable advance warning to save lives."

---

## 📊 12-Slide SIH Presentation Structure

1. **Slide 1: Title & Problem Statement (MDoNER SIH26001)** — Disaster challenges in NER (Meghalaya, Arunachal, Sikkim).
2. **Slide 2: Current System Limitations** — The 3 Fatal Flaws: No Internet Resilience, Prohibitive Cost (Rs. 2L), Black Box AI.
3. **Slide 3: High-Level Solution Architecture** — Online Cloud + Local Gateway Hotspot + LoRa 10km Mesh.
4. **Slide 4: 15 NER Villages Database** — Geological stratification (Meghalaya Sandstone, Arunachal Scree, Sikkim Glacial Moraine).
5. **Slide 5: Real AI Core & SHAP Explainability** — 90% accuracy, 87% ±5% confidence band, 5 SHAP factors.
6. **Slide 6: PDF Section 12 Compliance & Mode Selector** — Automatic Online vs Automatic Offline (with timestamped Red Cached Warning banner).
7. **Slide 7: Micro-Evacuation & Dijkstra Safe Route Engine** — Household GNN prioritization & NH-6/NH-13 landslide bypass.
8. **Slide 8: Multilingual Alert Center & 85dB Gateway Siren** — 6 Tribal languages (Bhashini AI) + Local Siren + WhatsApp/SMS queue.
9. **Slide 9: Community Intelligence & Drone Survey Planner** — YOLOv8 tension crack verification + SHA-256 ledger + DJI KML survey flight plan.
10. **Slide 10: Hardware Design & BOM Cost Breakdown** — Rs. 4,500 ESP32 solar node (44x cheaper) with 10uA deep sleep.
11. **Slide 11: 10 Disadvantages Defense Matrix** — How NER KAVACH solves data scarcity, false alarms, and extreme cold.
12. **Slide 12: Deployment Roadmap & MDoNER Scaling Plan** — Scaling from 15 pilot villages to 8,000 NER settlements within budget.
