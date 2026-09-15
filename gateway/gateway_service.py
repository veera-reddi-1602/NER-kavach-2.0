"""
NER KAVACH 3.0 — Local Gateway Service & Store-and-Forward Hub
Operates on local Wi-Fi / Subnet (e.g., 192.168.1.1 or configurable host/port).
Enables zero-internet community hazard reporting by storing reports in a persistent local queue
and forwarding them to the Main Application Server as soon as Internet is available.
"""

import sys
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

import os
import json
import time
import base64
import uuid
import threading
import urllib.request
import urllib.error
from datetime import datetime
from typing import Optional, Dict, Any, List
from fastapi import FastAPI, HTTPException, UploadFile, File, Form, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse, FileResponse
from pydantic import BaseModel

# Configuration (Configurable via Environment Variables)
GATEWAY_ID = os.getenv("GATEWAY_ID", "ARUNACHAL_GW_001")
GATEWAY_PORT = int(os.getenv("GATEWAY_PORT", "8001"))
GATEWAY_IP = os.getenv("GATEWAY_IP", "192.168.1.1")
MAIN_SERVER_URL = os.getenv("MAIN_SERVER_URL", "http://127.0.0.1:8000")

# Storage Directories
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
STORAGE_DIR = os.path.join(BASE_DIR, "storage")
AUDIO_DIR = os.path.join(STORAGE_DIR, "audio")
QUEUE_FILE = os.path.join(STORAGE_DIR, "reports_queue.json")

os.makedirs(AUDIO_DIR, exist_ok=True)

# In-Memory & Persistent State
state_lock = threading.Lock()
GATEWAY_STATE = {
    "gateway_id": GATEWAY_ID,
    "ip_address": GATEWAY_IP,
    "has_internet": True,  # Can be toggled for demo/testing
    "lora_mesh_active": True,
    "wifi_ap_ssid": "NER_KAVACH_GATEWAY_001",
    "last_sync_timestamp": None,
    "total_received_count": 0,
    "total_synced_count": 0,
    "total_queued_count": 0
}

def load_queue() -> List[Dict[str, Any]]:
    with state_lock:
        if os.path.exists(QUEUE_FILE):
            try:
                with open(QUEUE_FILE, "r", encoding="utf-8") as f:
                    return json.load(f)
            except Exception:
                return []
        return []

def save_queue(queue: List[Dict[str, Any]]):
    with state_lock:
        with open(QUEUE_FILE, "w", encoding="utf-8") as f:
            json.dump(queue, f, indent=2, ensure_ascii=False)

def update_queue_counts():
    q = load_queue()
    queued = sum(1 for r in q if r.get("status") in ["QUEUED", "RECEIVED_BY_GATEWAY", "RETRYING"])
    synced = sum(1 for r in q if r.get("status") == "SYNCED")
    GATEWAY_STATE["total_queued_count"] = queued
    GATEWAY_STATE["total_synced_count"] = synced
    GATEWAY_STATE["total_received_count"] = len(q)

app = FastAPI(
    title=f"NER KAVACH 3.0 — Local Gateway Hub ({GATEWAY_ID})",
    description="Local Store-and-Forward Gateway Hub for Zero-Internet Disaster Reporting",
    version="3.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ----------------- Forwarding & Auto-Sync Engine -----------------
CUSTOM_FORWARDER = None

def forward_report_to_main_server(report: Dict[str, Any]) -> bool:
    """Attempts to forward a single report to the Main Application Server."""
    if not GATEWAY_STATE["has_internet"]:
        return False

    if CUSTOM_FORWARDER is not None:
        return CUSTOM_FORWARDER(report)

    url = f"{MAIN_SERVER_URL}/api/v1/hazard-reports"
    try:
        data_json = json.dumps(report).encode("utf-8")
        req = urllib.request.Request(
            url,
            data=data_json,
            headers={"Content-Type": "application/json", "X-Gateway-ID": GATEWAY_ID}
        )
        with urllib.request.urlopen(req, timeout=3) as response:
            if response.status in [200, 201]:
                return True
    except Exception as e:
        print(f"⚠️ [Gateway {GATEWAY_ID}] Forwarding error for {report.get('report_id')}: {e}")
        return False
    return False

def sync_pending_reports():
    """Background worker loop that automatically flushes queued reports when Internet is available."""
    queue = load_queue()
    modified = False

    for report in queue:
        if report.get("status") in ["QUEUED", "RECEIVED_BY_GATEWAY", "RETRYING"]:
            # Check if Internet is available and attempt forward
            if GATEWAY_STATE["has_internet"]:
                report["status"] = "UPLOADING"
                report["retry_count"] = report.get("retry_count", 0) + 1
                report["last_retry_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S IST")
                
                success = forward_report_to_main_server(report)
                if success:
                    report["status"] = "SYNCED"
                    report["synced_at"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S IST")
                    GATEWAY_STATE["last_sync_timestamp"] = report["synced_at"]
                    print(f"✅ [Gateway {GATEWAY_ID}] Successfully synced report: {report['report_id']} to Main Server!")
                else:
                    report["status"] = "QUEUED"
                modified = True

    if modified:
        save_queue(queue)
        update_queue_counts()

def background_sync_daemon():
    """Periodic daemon thread running store-and-forward synchronizations."""
    while True:
        try:
            sync_pending_reports()
        except Exception as e:
            print(f"⚠️ [Gateway Sync Daemon Error]: {e}")
        time.sleep(4)

@app.on_event("startup")
def startup_event():
    if os.getenv("DISABLE_AUTO_SYNC_THREAD") != "1":
        sync_thread = threading.Thread(target=background_sync_daemon, daemon=True)
        sync_thread.start()

# ----------------- Gateway Endpoints -----------------
class HazardReportPayload(BaseModel):
    report_id: Optional[str] = None
    device_id: Optional[str] = "DEVICE_ANON"
    user_name: Optional[str] = "Local Resident"
    latitude: float
    longitude: float
    elevation_m: Optional[float] = 0.0
    timestamp: Optional[str] = None
    hazard_tags: List[str] = ["Observation"]
    help_note: Optional[str] = ""
    audio_base64: Optional[str] = None
    audio_format: Optional[str] = "webm"
    gateway_id: Optional[str] = GATEWAY_ID
    packet_id: Optional[str] = None
    target_peer_ip: Optional[str] = None
    hop_count: Optional[int] = 1
    route_hops: Optional[List[str]] = None
    transport: Optional[str] = "P2P_WIFI_DIRECT"

@app.get("/api/v1/gateway-status")
def get_gateway_status():
    """Returns local gateway connectivity, queue status, and mesh health."""
    update_queue_counts()
    return {
        "status": "ONLINE",
        "gateway_id": GATEWAY_ID,
        "gateway_ip": GATEWAY_IP,
        "has_internet_uplink": GATEWAY_STATE["has_internet"],
        "lora_mesh_active": GATEWAY_STATE["lora_mesh_active"],
        "wifi_ap_ssid": GATEWAY_STATE["wifi_ap_ssid"],
        "total_received": GATEWAY_STATE["total_received_count"],
        "total_queued": GATEWAY_STATE["total_queued_count"],
        "total_synced": GATEWAY_STATE["total_synced_count"],
        "last_sync": GATEWAY_STATE["last_sync_timestamp"],
        "server_uplink_status": "CONNECTED" if GATEWAY_STATE["has_internet"] else "DISCONNECTED_QUEUING"
    }

@app.post("/api/v1/hazard-reports")
def receive_hazard_report(payload: HazardReportPayload, background_tasks: BackgroundTasks):
    """
    Primary endpoint receiving hazard reports from phones over local Wi-Fi.
    Validates, saves audio to local vault, queues report, and attempts forwarding.
    """
    report_id = payload.report_id or f"RPT-{int(time.time()*1000)}"
    ts = payload.timestamp or datetime.now().strftime("%Y-%m-%d %H:%M:%S IST")
    
    # Save audio if provided
    audio_path = None
    audio_filename = None
    if payload.audio_base64:
        try:
            # Strip header if present (e.g. data:audio/webm;base64,...)
            raw_b64 = payload.audio_base64.split(",")[-1]
            audio_bytes = base64.b64decode(raw_b64)
            audio_filename = f"{report_id}.{payload.audio_format}"
            audio_path = os.path.join(AUDIO_DIR, audio_filename)
            with open(audio_path, "wb") as af:
                af.write(audio_bytes)
        except Exception as e:
            print(f"⚠️ Failed to save audio: {e}")

    report_record = {
        "report_id": report_id,
        "device_id": payload.device_id,
        "user_name": payload.user_name,
        "latitude": payload.latitude,
        "longitude": payload.longitude,
        "elevation_m": payload.elevation_m,
        "timestamp": ts,
        "hazard_tags": payload.hazard_tags,
        "help_note": payload.help_note,
        "audio_filename": audio_filename,
        "audio_base64": payload.audio_base64,  # Keep payload ready for forward
        "gateway_id": payload.gateway_id or GATEWAY_ID,
        "status": "RECEIVED_BY_GATEWAY",
        "created_at": datetime.now().strftime("%Y-%m-%d %H:%M:%S IST"),
        "retry_count": 0,
        "last_retry_at": None,
        "synced_at": None
    }

    # Idempotent storage in queue
    queue = load_queue()
    existing = next((r for r in queue if r["report_id"] == report_id), None)
    if not existing:
        queue.append(report_record)
        save_queue(queue)
        update_queue_counts()
    else:
        report_record = existing

    # Attempt immediate sync in background if internet is active
    if GATEWAY_STATE["has_internet"]:
        background_tasks.add_task(sync_pending_reports)

    return {
        "status": "RECEIVED_BY_GATEWAY",
        "report_id": report_id,
        "gateway_id": GATEWAY_ID,
        "stored_locally": True,
        "internet_uplink_active": GATEWAY_STATE["has_internet"],
        "message": "Report safely stored in local gateway flash queue and scheduled for central server sync."
    }

@app.get("/api/v1/hazard-reports")
def get_gateway_reports():
    """Returns all locally queued/synced reports on this gateway."""
    update_queue_counts()
    return {
        "gateway_id": GATEWAY_ID,
        "reports": load_queue()
    }

@app.get("/api/v1/audio/{filename}")
def get_gateway_audio(filename: str):
    """Serves stored audio recordings locally."""
    path = os.path.join(AUDIO_DIR, filename)
    if os.path.exists(path):
        return FileResponse(path)
    raise HTTPException(status_code=404, detail="Audio file not found")

@app.post("/api/v1/toggle-internet")
def toggle_gateway_internet(enable: Optional[bool] = None):
    """
    Demo / Testing endpoint: Enables or disables the gateway's simulated Internet connection.
    When enabled, queued reports immediately flush to the main server.
    """
    if enable is not None:
        GATEWAY_STATE["has_internet"] = enable
    else:
        GATEWAY_STATE["has_internet"] = not GATEWAY_STATE["has_internet"]

    if GATEWAY_STATE["has_internet"]:
        # Trigger immediate sync
        threading.Thread(target=sync_pending_reports, daemon=True).start()

    return {
        "gateway_id": GATEWAY_ID,
        "has_internet_uplink": GATEWAY_STATE["has_internet"],
        "message": f"Gateway Internet uplink is now {'ONLINE (Auto-Syncing)' if GATEWAY_STATE['has_internet'] else 'OFFLINE (Queuing Reports Locally)'}"
    }

if __name__ == "__main__":
    import uvicorn
    print(f"🚀 [NER KAVACH 3.0] Starting Local Gateway Hub on port {GATEWAY_PORT}...")
    uvicorn.run(app, host="0.0.0.0", port=GATEWAY_PORT)
