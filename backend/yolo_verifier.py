"""
NER KAVACH 3.0 — YOLOv8 Visual Hazard Verifier & Tamper-Proof Ledger
Simulates real-time computer vision inference for landslide tension cracks,
mudflow deposits, and rockfalls with SHA-256 cryptographically chained audit trail.
"""

import os
import json
import hashlib
import time
from datetime import datetime

class CommunityIntelligenceEngine:
    def __init__(self):
        self.reports = []
        self.leaderboard = [
            {"citizen_name": "Tenzing Monpa", "state": "Arunachal Pradesh", "points": 140, "badge": "Master Mountain Scout", "reports_verified": 14},
            {"citizen_name": "Kmenlang Lyngdoh", "state": "Meghalaya", "points": 110, "badge": "Senior Ridge Sentinel", "reports_verified": 11},
            {"citizen_name": "Karma Bhutia", "state": "Sikkim", "points": 90, "badge": "High Altitude Warden", "reports_verified": 9},
            {"citizen_name": "Dawa Lepcha", "state": "Sikkim", "points": 80, "badge": "Active Scout", "reports_verified": 8},
            {"citizen_name": "Eri Mihu", "state": "Arunachal Pradesh", "points": 60, "badge": "Valley Watcher", "reports_verified": 6}
        ]
        self.init_default_reports()

    def init_default_reports(self):
        self.submit_hazard_report(
            village_id="AR_01",
            citizen_name="Tenzing Monpa",
            hazard_type="Ground Tension Crack",
            description="3-meter lateral fissure observed along upper Sela ridge path.",
            lat=27.584,
            lon=91.874,
            device_id="DEV_MOTO_G54_AR88",
            image_filename="crack_sample_sela.jpg"
        )
        self.submit_hazard_report(
            village_id="ML_01",
            citizen_name="Kmenlang Lyngdoh",
            hazard_type="Debris Mudflow",
            description="Active mud slurry and gravel overflow crossing Sohra bypass.",
            lat=25.302,
            lon=91.705,
            device_id="DEV_SAMS_A34_ML12",
            image_filename="mudflow_sohra.jpg"
        )

    def submit_hazard_report(self, village_id: str, citizen_name: str, hazard_type: str,
                             description: str, lat: float, lon: float, device_id: str,
                             image_filename: str = "uploaded_photo.jpg") -> dict:
        timestamp_str = datetime.now().isoformat()
        prev_hash = self.reports[0]["tamper_proof_ledger"]["block_hash"] if self.reports else "GENESIS_NER_KAVACH_0000000000"

        # YOLOv8 Visual Inference Simulation
        confidence = 0.94 if "crack" in hazard_type.lower() or "tension" in hazard_type.lower() else 0.89
        is_hazard_verified = True
        severity = "HIGH" if "crack" in hazard_type.lower() or "rockfall" in hazard_type.lower() else "CRITICAL"
        
        detections = [
            {
                "label": hazard_type,
                "confidence": confidence,
                "bbox": {"ymin": 28, "xmin": 22, "ymax": 74, "xmax": 81},
                "hazard_class": "Geotechnical Instability Fissure"
            }
        ]

        # SHA-256 Tamper-Proof Cryptographic Hash Chaining
        hash_payload = f"{prev_hash}|{village_id}|{citizen_name}|{lat}|{lon}|{device_id}|{timestamp_str}"
        block_hash = hashlib.sha256(hash_payload.encode('utf-8')).hexdigest()

        report_entry = {
            "id": f"REP_NER_{len(self.reports) + 101}",
            "village_id": village_id,
            "citizen_name": citizen_name,
            "hazard_type": hazard_type,
            "description": description,
            "lat": lat,
            "lon": lon,
            "device_id": device_id,
            "timestamp": timestamp_str,
            "yolo_inference": {
                "verified": is_hazard_verified,
                "confidence": confidence,
                "detected_objects": detections,
                "severity_level": severity,
                "model_version": "YOLOv8x-NER-Landslide-v3.2"
            },
            "tamper_proof_ledger": {
                "previous_hash": prev_hash,
                "block_hash": block_hash,
                "immutability_verified": True
            },
            "status": "APPROVED_BY_NDRF",
            "gamification_points_awarded": 10
        }

        self.reports.insert(0, report_entry)

        # Update leaderboard
        found = False
        for user in self.leaderboard:
            if user["citizen_name"].lower() == citizen_name.lower():
                user["points"] += 10
                user["reports_verified"] += 1
                found = True
                break
        if not found:
            self.leaderboard.append({
                "citizen_name": citizen_name,
                "state": "North Eastern Region",
                "points": 10,
                "badge": "Junior Scout",
                "reports_verified": 1
            })

        self.leaderboard.sort(key=lambda x: x["points"], reverse=True)

        return report_entry

    def get_all_reports(self) -> list:
        return self.reports

    def get_leaderboard(self) -> list:
        return self.leaderboard

yolo_verifier = CommunityIntelligenceEngine()
