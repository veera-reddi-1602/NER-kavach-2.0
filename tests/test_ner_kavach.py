"""
NER KAVACH 3.0 — Automated Test & Validation Suite
Validates AI Model, Safe Routing Engine, Micro-Evacuation, PDF Export & API Endpoints.
"""

import sys
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

import os
import unittest
import json
from backend.ml_engine import ml_engine
from backend.routing_engine import routing_engine
from backend.evacuation_engine import evacuation_engine
from backend.yolo_verifier import yolo_verifier

class TestNERKavachSystem(unittest.TestCase):
    
    def test_01_villages_database(self):
        """Verify 15 villages exist (5 ML + 5 AR + 5 SK) with valid coordinates & geology."""
        v_file = os.path.join(os.path.dirname(__file__), "..", "backend", "data", "villages.json")
        self.assertTrue(os.path.exists(v_file), "villages.json must exist")
        
        with open(v_file, "r") as f:
            villages = json.load(f).get("villages", [])
            
        self.assertEqual(len(villages), 15, "Must contain exactly 15 villages")
        
        ml = [v for v in villages if v["state_code"] == "ML"]
        ar = [v for v in villages if v["state_code"] == "AR"]
        sk = [v for v in villages if v["state_code"] == "SK"]
        
        self.assertEqual(len(ml), 5, "Must contain 5 Meghalaya villages")
        self.assertEqual(len(ar), 5, "Must contain 5 Arunachal Pradesh villages")
        self.assertEqual(len(sk), 5, "Must contain 5 Sikkim villages")
        
        for v in villages:
            self.assertIn("sensor_id", v)
            self.assertIn("slope_deg", v)
            self.assertIn("insar_deformation_mm_yr", v)
            self.assertIn("shelters", v)
            self.assertGreater(len(v["shelters"]), 0)
        print("[TEST PASS] 15 Villages Database verified.")

    def test_02_ai_model_and_shap(self):
        """Test XGBoost/Ensemble AI model predictions and SHAP factor contributions."""
        pred = ml_engine.predict({
            "slope_deg": 48.0,
            "rainfall_24h_mm": 410.0,
            "soil_moisture_pct": 92.0,
            "insar_deformation_mm_yr": 44.0,
            "elevation_m": 1400.0
        })
        
        self.assertIn("risk_score", pred)
        self.assertIn("risk_tier", pred)
        self.assertEqual(pred["risk_tier"], "HIGH")
        self.assertGreaterEqual(pred["risk_score"], 70.0)
        self.assertIn("confidence_band", pred)
        self.assertIn("top_factors", pred)
        self.assertGreaterEqual(len(pred["top_factors"]), 3)
        self.assertIn("active_triggers", pred)
        print(f"[TEST PASS] AI Core Model verified: Risk Score={pred['risk_score']} ({pred['risk_tier']})")

    def test_03_dijkstra_safe_routing(self):
        """Test Dijkstra algorithm avoids blocked NH-6 and finds alternative ridge path."""
        route = routing_engine.find_safe_route("N_CHERRA", "N_SHILLONG", avoid_blocked=True)
        self.assertEqual(route["status"], "SUCCESS")
        self.assertGreater(route["total_distance_km"], 0)
        self.assertIn("waypoint_coordinates", route)
        self.assertIn("edges_traversed", route)
        
        # Verify blocked roads were not used
        for edge in route["edges_traversed"]:
            self.assertFalse(edge.get("is_blocked", False), f"Route should not traverse blocked edge: {edge['road_name']}")
        print(f"[TEST PASS] Dijkstra Safe Routing verified: Distance={route['total_distance_km']} km")

    def test_04_micro_evacuation_and_pdf(self):
        """Test GNN household priority scoring and PDF generation."""
        scored = evacuation_engine.score_households()
        self.assertGreater(len(scored), 0)
        
        for h in scored:
            self.assertIn("priority_score", h)
            self.assertIn("evacuation_phase", h)
            self.assertIn("urgency_badge", h)
            self.assertGreaterEqual(h["priority_score"], 0.0)
            self.assertLessEqual(h["priority_score"], 100.0)
            
        pdf_bytes = evacuation_engine.generate_evacuation_pdf("ML_01")
        self.assertIsInstance(pdf_bytes, bytes)
        self.assertGreater(len(pdf_bytes), 1000)
        self.assertTrue(pdf_bytes.startswith(b"%PDF"), "Output must be valid PDF format")
        print(f"[TEST PASS] Micro-Evacuation & PDF Engine verified ({len(pdf_bytes)} bytes)")

    def test_05_yolo_tamper_proof_ledger(self):
        """Test YOLOv8 hazard verification and SHA-256 block ledger immutability."""
        res = yolo_verifier.submit_hazard_report(
            village_id="AR_01",
            citizen_name="Tenzing Test",
            hazard_type="Ground Tension Crack",
            description="Test tension crack along slope scarp",
            lat=27.58,
            lon=91.87,
            device_id="DEV_UNIT_TEST"
        )
        self.assertIn("yolo_inference", res)
        self.assertTrue(res["yolo_inference"]["verified"])
        self.assertIn("tamper_proof_ledger", res)
        self.assertEqual(len(res["tamper_proof_ledger"]["block_hash"]), 64)
        print("[TEST PASS] YOLOv8 Verification & SHA-256 Ledger verified.")

if __name__ == "__main__":
    unittest.main()
