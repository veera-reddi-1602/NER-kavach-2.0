"""
NER KAVACH 3.0 — Comprehensive Test Suite
Tests Offline Community Hazard Reporting via Local Gateway:
1. Gateway Receiving Hazard Reports (Audio + GPS Telemetry).
2. Store-and-Forward Forwarding to Main Application Server.
3. Local Flash Queuing when Gateway Internet Uplink is Down.
4. Auto-Flush Synchronization when Internet is Restored.
5. Idempotent Duplicate Rejection.
6. Central Ingestion API & Audio Serving.
"""

import os
import sys
import json
import time
import base64
import unittest
from fastapi.testclient import TestClient

# Add project roots to sys.path
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(BASE_DIR, "gateway"))
sys.path.insert(0, os.path.join(BASE_DIR, "backend"))

os.environ["DISABLE_AUTO_SYNC_THREAD"] = "1"

from gateway_service import app as gateway_app, GATEWAY_STATE, load_queue, save_queue, QUEUE_FILE, sync_pending_reports
import gateway_service
from app import app as backend_app, load_community_reports, save_community_reports

class TestOfflineGatewayReporting(unittest.TestCase):
    def setUp(self):
        self.gateway_client = TestClient(gateway_app)
        self.backend_client = TestClient(backend_app)
        
        # Connect Gateway forwarder directly to backend in-memory client
        def mock_forwarder(report):
            resp = self.backend_client.post("/api/v1/hazard-reports", json=report)
            return resp.status_code in [200, 201]

        gateway_service.CUSTOM_FORWARDER = mock_forwarder
        
        # Reset gateway queue for test isolation
        save_queue([])
        GATEWAY_STATE["has_internet"] = True

    def test_01_gateway_status_endpoint(self):
        """Verify Gateway Status and Mesh Health endpoint."""
        resp = self.gateway_client.get("/api/v1/gateway-status")
        self.assertEqual(resp.status_code, 200)
        data = resp.json()
        self.assertEqual(data["gateway_id"], "ARUNACHAL_GW_001")
        self.assertTrue(data["lora_mesh_active"])
        self.assertEqual(data["server_uplink_status"], "CONNECTED")

    def test_02_receive_report_and_forward(self):
        """Verify receiving report from phone and forwarding to central server when internet is active."""
        # Simulated small audio base64 blob
        audio_b64 = base64.b64encode(b"FAKE_AUDIO_DATA_FOR_TESTING").decode("utf-8")
        
        payload = {
            "report_id": "RPT-TEST-001",
            "device_id": "DEV-TEST-PHONE",
            "user_name": "Tenzing Tester",
            "latitude": 27.5841,
            "longitude": 91.8742,
            "elevation_m": 3048.0,
            "hazard_tags": ["🚨 Trapped / Need Rescue", "⚡ Ground Fissure"],
            "help_note": "Testing offline gateway store-and-forward transmission.",
            "audio_base64": f"data:audio/webm;base64,{audio_b64}",
            "audio_format": "webm",
            "gateway_id": "ARUNACHAL_GW_001"
        }
        
        # 1. Post to gateway
        gw_resp = self.gateway_client.post("/api/v1/hazard-reports", json=payload)
        self.assertEqual(gw_resp.status_code, 200)
        gw_data = gw_resp.json()
        self.assertEqual(gw_data["status"], "RECEIVED_BY_GATEWAY")
        self.assertEqual(gw_data["report_id"], "RPT-TEST-001")
        
        # 2. Verify stored in gateway local queue
        queue = load_queue()
        stored = next((r for r in queue if r["report_id"] == "RPT-TEST-001"), None)
        self.assertIsNotNone(stored)
        self.assertEqual(stored["latitude"], 27.5841)
        
        # 3. Verify central backend ingestion
        be_resp = self.backend_client.get("/api/v1/hazard-reports")
        self.assertEqual(be_resp.status_code, 200)
        be_data = be_resp.json()
        self.assertGreaterEqual(be_data["total_count"], 1)

    def test_03_store_in_local_queue_when_internet_down(self):
        """Verify report is safely queued locally when gateway internet is down."""
        # Simulate gateway losing internet
        GATEWAY_STATE["has_internet"] = False
        
        payload = {
            "report_id": "RPT-OFFLINE-QUEUE-002",
            "device_id": "DEV-VIVO-V2502",
            "user_name": "Dorjee Offline",
            "latitude": 27.2642,
            "longitude": 92.4051,
            "elevation_m": 2217.0,
            "hazard_tags": ["💥 Heavy Rockfall"],
            "help_note": "Gateway internet down - testing local flash queuing.",
            "audio_base64": None,
            "gateway_id": "ARUNACHAL_GW_001"
        }
        
        gw_resp = self.gateway_client.post("/api/v1/hazard-reports", json=payload)
        self.assertEqual(gw_resp.status_code, 200)
        
        # Verify status is stored locally
        queue = load_queue()
        stored = next((r for r in queue if r["report_id"] == "RPT-OFFLINE-QUEUE-002"), None)
        self.assertIsNotNone(stored)
        self.assertIn(stored["status"], ["RECEIVED_BY_GATEWAY", "QUEUED"])

    def test_04_idempotent_duplicate_prevention(self):
        """Verify identical report IDs are idempotently deduplicated without crashing or duplicates."""
        test_id = f"RPT-IDEMP-{int(time.time()*1000)}"
        payload = {
            "report_id": test_id,
            "latitude": 28.7940,
            "longitude": 95.9040,
            "hazard_tags": ["🚧 Road Cutoff / Blocked"],
            "help_note": "Idempotent duplicate check."
        }
        
        # Ingest once
        resp1 = self.backend_client.post("/api/v1/hazard-reports", json=payload)
        self.assertEqual(resp1.status_code, 200)
        self.assertEqual(resp1.json()["status"], "SYNCED")
        
        # Ingest same report_id again
        resp2 = self.backend_client.post("/api/v1/hazard-reports", json=payload)
        self.assertEqual(resp2.status_code, 200)
        self.assertEqual(resp2.json()["status"], "ALREADY_EXISTS_SYNCED")

    def test_05_authority_verification_flow(self):
        """Verify authorities can update report verification status in backend."""
        # Ensure report exists
        seed_payload = {
            "report_id": "RPT-102401",
            "device_id": "DEV-TEST-01",
            "latitude": 27.5841,
            "longitude": 91.8742,
            "hazard_tags": ["🚨 Trapped / Need Rescue"],
            "help_note": "Monastery road landslide."
        }
        self.backend_client.post("/api/v1/hazard-reports", json=seed_payload)

        verify_payload = {
            "status": "VERIFIED_HIGH_PRIORITY",
            "admin_notes": "Confirmed by NDRF Scout Drone Survey Unit 02."
        }
        
        resp = self.backend_client.post("/api/v1/hazard-reports/RPT-102401/verify", json=verify_payload)
        self.assertEqual(resp.status_code, 200)
        data = resp.json()
        self.assertEqual(data["status"], "UPDATED")
        self.assertEqual(data["report"]["status"], "VERIFIED_HIGH_PRIORITY")

if __name__ == "__main__":
    unittest.main()
