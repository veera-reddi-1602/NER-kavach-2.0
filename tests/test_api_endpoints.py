"""
NER KAVACH 3.0 — FastAPI REST Endpoints Integration Test Suite
Validates all system, village, telemetry, prediction, routing, and evacuation endpoints.
"""

import sys
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

import unittest
from fastapi.testclient import TestClient
from backend.app import app

client = TestClient(app)

class TestAPIEndpoints(unittest.TestCase):

    def test_01_system_status(self):
        """Test /api/system-status returns healthy status and gateway metrics."""
        response = client.get("/api/system-status")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertEqual(data["status"], "HEALTHY")
        self.assertIn("mode", data)
        self.assertIn("local_gateway", data)
        self.assertEqual(data["local_gateway"]["ip"], "192.168.1.1")
        print("[API TEST PASS] /api/system-status")

    def test_02_villages_list(self):
        """Test /api/villages returns 15 villages and supports state filtering."""
        response = client.get("/api/villages")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertEqual(data["count"], 15)

        # Test state filter for Arunachal Pradesh
        ar_res = client.get("/api/villages?state=AR")
        self.assertEqual(ar_res.status_code, 200)
        self.assertEqual(ar_res.json()["count"], 5)
        print("[API TEST PASS] /api/villages & state filtering")

    def test_03_sensors_latest(self):
        """Test /api/sensors/latest returns telemetry cards for all 15 nodes."""
        response = client.get("/api/sensors/latest")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertEqual(data["sensor_nodes_count"], 15)
        for s in data["sensors"]:
            self.assertIn("sensor_id", s)
            self.assertIn("telemetry", s)
            self.assertIn("rain_24h_mm", s["telemetry"])
        print("[API TEST PASS] /api/sensors/latest")

    def test_04_predict_risk(self):
        """Test /api/predict-risk executes XGBoost prediction."""
        payload = {
            "village_id": "AR_01",
            "slope_deg": 50.0,
            "rainfall_24h_mm": 290.0,
            "soil_moisture_pct": 86.0,
            "insar_deformation_mm_yr": 42.0,
            "elevation_m": 3048.0
        }
        response = client.post("/api/predict-risk", json=payload)
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIn("risk_score", data)
        self.assertIn("risk_tier", data)
        self.assertEqual(data["risk_tier"], "HIGH")
        self.assertGreaterEqual(data["risk_score"], 70.0)
        print(f"[API TEST PASS] /api/predict-risk -> Risk: {data['risk_score']}% ({data['risk_tier']})")

    def test_05_dijkstra_routing(self):
        """Test /api/routes/safe-route computes safe bypass."""
        response = client.get("/api/routes/safe-route?start=N_CHERRA&destination=N_SHILLONG&avoid_blocked=true")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertEqual(data["status"], "SUCCESS")
        self.assertGreater(data["total_distance_km"], 0)
        self.assertIn("waypoint_coordinates", data)
        print(f"[API TEST PASS] /api/routes/safe-route -> {data['total_distance_km']} km")

    def test_06_evacuation_households(self):
        """Test /api/evacuation/households returns GNN priority roster."""
        response = client.get("/api/evacuation/households")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIn("households", data)
        self.assertGreater(len(data["households"]), 0)
        print(f"[API TEST PASS] /api/evacuation/households -> {len(data['households'])} households")

    def test_07_crowdsource_hazard(self):
        """Test /api/crowdsource/submit verifies report and records SHA-256 block."""
        payload = {
            "village_id": "AR_01",
            "citizen_name": "Dorjee Khandu",
            "hazard_type": "Slope Fissure / Tension Crack",
            "description": "Tension crack observed along ridge edge",
            "lat": 27.58,
            "lon": 91.86,
            "device_id": "VIVO_V2502_TEST"
        }
        response = client.post("/api/crowdsource/submit", json=payload)
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertTrue(data["yolo_inference"]["verified"])
        self.assertIn("tamper_proof_ledger", data)
        print("[API TEST PASS] /api/crowdsource/submit")

    def test_08_drone_flight_plan(self):
        """Test /api/drone/flight-plan generates DJI survey waypoints."""
        response = client.get("/api/drone/flight-plan?village_id=AR_01")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIn("waypoints", data)
        self.assertEqual(len(data["waypoints"]), 6)
        print("[API TEST PASS] /api/drone/flight-plan")

if __name__ == "__main__":
    unittest.main()
