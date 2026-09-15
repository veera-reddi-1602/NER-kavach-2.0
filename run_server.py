"""
NER KAVACH 3.0 — Unified Server Launcher
Starts the FastAPI Backend and Static Web Server on http://127.0.0.1:8000
"""

import sys
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

import os
import uvicorn

if __name__ == "__main__":
    # Ensure model is trained
    model_path = os.path.join(os.path.dirname(__file__), "backend", "models", "landslide_model.pkl")
    if not os.path.exists(model_path):
        print("[INIT] Landslide model not found. Running training script...")
        import train_model
        train_model.train_and_export()
        
    print("=" * 70)
    print("🛡️  NER KAVACH 3.0 — AI Landslide Early Warning & Risk Monitoring System")
    print("📍 Covering 15 Villages across Meghalaya, Arunachal Pradesh & Sikkim")
    print("🌐 FastAPI & Web App running on: http://127.0.0.1:8000")
    print("📊 Streamlit PDF-Compliant App:  streamlit run app.py")
    print("=" * 70)
    
    uvicorn.run("backend.app:app", host="127.0.0.1", port=8000, reload=False)
