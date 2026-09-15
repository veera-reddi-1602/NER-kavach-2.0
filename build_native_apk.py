"""
NER KAVACH 3.0 — Direct Android Native APK Builder & Device Deployer
Compiles a standalone, offline-ready native Android APK with embedded assets,
native 85dB siren, Bhashini TTS voice synthesis, and real Android Notifications,
then installs and launches it directly on the connected Vivo V2502 smartphone.
"""

import sys
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

import os
import shutil
import subprocess
import json

SDK_ROOT = r"C:\Users\veerababu\AppData\Local\Android\Sdk"
BUILD_TOOLS = os.path.join(SDK_ROOT, "build-tools", "36.0.0")
PLATFORM_JAR = os.path.join(SDK_ROOT, "platforms", "android-36", "android.jar")
ADB_PATH = os.path.join(SDK_ROOT, "platform-tools", "adb.exe")

AAPT2 = os.path.join(BUILD_TOOLS, "aapt2.exe")
D8 = os.path.join(BUILD_TOOLS, "d8.bat")
ZIPALIGN = os.path.join(BUILD_TOOLS, "zipalign.exe")
APKSIGNER = os.path.join(BUILD_TOOLS, "apksigner.bat")
KEYTOOL = r"C:\Program Files\Java\jdk-25\bin\keytool.exe"

PROJECT_DIR = os.path.join(os.path.dirname(__file__), "android_native")
BUILD_DIR = os.path.join(PROJECT_DIR, "build")
SRC_DIR = os.path.join(PROJECT_DIR, "src", "com", "nerkavach", "app")
RES_DIR = os.path.join(PROJECT_DIR, "res")
ASSETS_DIR = os.path.join(PROJECT_DIR, "assets")

def setup_project_files():
    os.makedirs(SRC_DIR, exist_ok=True)
    os.makedirs(os.path.join(RES_DIR, "values"), exist_ok=True)
    os.makedirs(os.path.join(RES_DIR, "drawable"), exist_ok=True)
    os.makedirs(os.path.join(RES_DIR, "xml"), exist_ok=True)
    os.makedirs(ASSETS_DIR, exist_ok=True)
    os.makedirs(BUILD_DIR, exist_ok=True)

    # 1. AndroidManifest.xml
    manifest_content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.nerkavach.app"
    android:versionCode="300"
    android:versionName="3.0">

    <uses-sdk android:minSdkVersion="24" android:targetSdkVersion="36" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />

    <queries>
        <package android:name="com.whatsapp" />
        <package android:name="com.whatsapp.w4b" />
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="whatsapp" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="https" />
        </intent>
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="text/plain" />
        </intent>
    </queries>

    <application
        android:label="NER KAVACH 3.0"
        android:icon="@drawable/ic_shield"
        android:roundIcon="@drawable/ic_shield"
        android:theme="@android:style/Theme.NoTitleBar"
        android:hardwareAccelerated="true"
        android:usesCleartextTraffic="true">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".EmergencyAlertActivity"
            android:exported="true"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:showWhenLocked="true"
            android:turnScreenOn="true"
            android:excludeFromRecents="true"
            android:launchMode="singleInstance"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:screenOrientation="portrait" />

        <receiver
            android:name=".DismissAlertReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="com.nerkavach.app.ACTION_DISMISS_ALERT" />
                <action android:name="com.nerkavach.app.ACTION_STOP_SIREN" />
            </intent-filter>
        </receiver>

        <service
            android:name=".WhatsAppAutoSendService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/whatsapp_accessibility_config" />
        </service>

        <service
            android:name=".AlertReceiverService"
            android:exported="false"
            android:foregroundServiceType="dataSync" />
    </application>
</manifest>
"""
    with open(os.path.join(PROJECT_DIR, "AndroidManifest.xml"), "w", encoding="utf-8") as f:
        f.write(manifest_content)

    # 2. strings.xml
    strings_content = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">NER KAVACH 3.0</string>
    <string name="accessibility_service_desc">Automated Emergency Landslide Warning Dispatcher for WhatsApp</string>
</resources>
"""
    with open(os.path.join(RES_DIR, "values", "strings.xml"), "w", encoding="utf-8") as f:
        f.write(strings_content)

    # 3. Vector Drawable Icon (ic_shield.xml)
    icon_content = """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
  <path
      android:fillColor="#0284C7"
      android:pathData="M12,1L3,5v6c0,5.55 3.84,10.74 9,12 5.16,-1.26 9,-6.45 9,-12V5l-9,-4z"/>
  <path
      android:fillColor="#38BDF8"
      android:pathData="M12,3.18L19,6.3v4.7c0,4.52 -3.13,8.74 -7,9.81 -3.87,-1.07 -7,-5.29 -7,-9.81V6.3l7,-3.12z"/>
</vector>
"""
    with open(os.path.join(RES_DIR, "drawable", "ic_shield.xml"), "w", encoding="utf-8") as f:
        f.write(icon_content)

    # 4. Copy Web & Data Assets into assets/
    frontend_dir = os.path.join(os.path.dirname(__file__), "frontend")
    backend_data_dir = os.path.join(os.path.dirname(__file__), "backend", "data")

    for item in os.listdir(frontend_dir):
        s = os.path.join(frontend_dir, item)
        d = os.path.join(ASSETS_DIR, item)
        if os.path.isfile(s):
            shutil.copy2(s, d)

    os.makedirs(os.path.join(ASSETS_DIR, "data"), exist_ok=True)
    for item in os.listdir(backend_data_dir):
        s = os.path.join(backend_data_dir, item)
        d = os.path.join(ASSETS_DIR, "data", item)
        if os.path.isfile(s):
            shutil.copy2(s, d)

    # 5. Native Java MainActivity with JavaScript Bridge
    main_activity_java = """package com.nerkavach.app;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.PermissionRequest;
import android.webkit.GeolocationPermissions;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends Activity {
    private WebView webView;
    private TextToSpeech tts;
    private ToneGenerator toneGen;
    private Vibrator vibrator;
    private static final String CHANNEL_ID = "NER_KAVACH_ALERTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToneAndVibrator();
        initTTS();
        createNotificationChannel();

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("/api/villages")) {
                    return getAssetResponse("data/villages.json", "application/json");
                } else if (url.contains("/api/evacuation/households")) {
                    return getAssetResponse("data/households.json", "application/json");
                } else if (url.contains("/api/routes/network") || url.contains("/api/routes/safe-route")) {
                    return getAssetResponse("data/routes.json", "application/json");
                } else if (url.contains("/api/analytics/historical")) {
                    return getAssetResponse("data/historical_trends.json", "application/json");
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.loadUrl("file:///android_asset/index.html");
    }

    private WebResourceResponse getAssetResponse(String assetPath, String mimeType) {
        try {
            InputStream is = getAssets().open(assetPath);
            return new WebResourceResponse(mimeType, "UTF-8", is);
        } catch (Exception e) {
            return null;
        }
    }

    private void initToneAndVibrator() {
        try {
            toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        } catch (Exception e) {}
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initTTS() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "NER Early Warning Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Critical landslide early warning alerts");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    public class AndroidBridge {
        @JavascriptInterface
        public void triggerSiren(int durationMs) {
            try {
                if (toneGen != null) {
                    toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, durationMs);
                }
                if (vibrator != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(durationMs);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "🔊 85dB Siren Triggered (LoRa Gateway Simulation)", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {}
        }

        @JavascriptInterface
        public void speakText(String text, String lang) {
            if (tts != null) {
                if ("Hindi".equalsIgnoreCase(lang)) {
                    tts.setLanguage(new Locale("hi", "IN"));
                } else {
                    tts.setLanguage(Locale.ENGLISH);
                }
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NER_ALERT_ID");
            }
        }

        @JavascriptInterface
        public void showToast(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (toneGen != null) {
            toneGen.release();
        }
        super.onDestroy();
    }
}
"""
    main_java_file = os.path.join(SRC_DIR, "MainActivity.java")
    if not os.path.exists(main_java_file):
        with open(main_java_file, "w", encoding="utf-8") as f:
            f.write(main_activity_java)

def run_cmd(cmd, cwd=None):
    print(f"--> {cmd}")
    res = subprocess.run(cmd, shell=True, cwd=cwd, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"STDERR: {res.stderr}")
        print(f"STDOUT: {res.stdout}")
        raise RuntimeError(f"Command failed: {cmd}")
    return res.stdout

def build_apk():
    print("🚀 [Step 1] Preparing Android Native project structure...")
    setup_project_files()

    frontend_dir = os.path.join(os.path.dirname(__file__), "frontend")
    if os.path.exists(frontend_dir):
        print(f"🔄 Syncing frontend web assets from {frontend_dir} to {ASSETS_DIR}...")
        for item in os.listdir(frontend_dir):
            s = os.path.join(frontend_dir, item)
            d = os.path.join(ASSETS_DIR, item)
            if os.path.isfile(s):
                shutil.copy2(s, d)
            elif os.path.isdir(s):
                shutil.copytree(s, d, dirs_exist_ok=True)

    res_zip = os.path.join(BUILD_DIR, "compiled_res.zip")
    gen_dir = os.path.join(BUILD_DIR, "gen")
    classes_dir = os.path.join(BUILD_DIR, "classes")
    os.makedirs(gen_dir, exist_ok=True)
    os.makedirs(classes_dir, exist_ok=True)

    print("📦 [Step 2] Compiling resources with aapt2...")
    run_cmd(f'"{AAPT2}" compile --dir "{RES_DIR}" -o "{res_zip}"')

    unaligned_apk = os.path.join(BUILD_DIR, "app-unaligned.apk")
    manifest = os.path.join(PROJECT_DIR, "AndroidManifest.xml")

    print("🔗 [Step 3] Linking resources and generating R.java...")
    run_cmd(f'"{AAPT2}" link -I "{PLATFORM_JAR}" --manifest "{manifest}" --min-sdk-version 24 --target-sdk-version 36 "{res_zip}" -A "{ASSETS_DIR}" --java "{gen_dir}" -o "{unaligned_apk}" --auto-add-overlay')

    print("☕ [Step 4] Compiling Java classes with javac...")
    java_files = []
    for root, _, files in os.walk(os.path.join(PROJECT_DIR, "src")):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))
    for root, _, files in os.walk(gen_dir):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))

    sources_arg = " ".join([f'"{f}"' for f in java_files])
    run_cmd(f'javac -source 1.8 -target 1.8 -bootclasspath "{PLATFORM_JAR}" -d "{classes_dir}" {sources_arg}')

    print("⚡ [Step 5] Dexing with D8...")
    class_files = []
    for root, _, files in os.walk(classes_dir):
        for file in files:
            if file.endswith(".class"):
                class_files.append(os.path.join(root, file))
    classes_arg = " ".join([f'"{f}"' for f in class_files])
    run_cmd(f'"{D8}" --min-api 24 --output "{BUILD_DIR}" {classes_arg}')

    print("📦 [Step 6] Adding classes.dex into APK...")
    classes_dex = os.path.join(BUILD_DIR, "classes.dex")
    import zipfile
    with zipfile.ZipFile(unaligned_apk, 'a') as zipf:
        zipf.write(classes_dex, "classes.dex")

    print("📐 [Step 7] Zipalign APK...")
    aligned_apk = os.path.join(BUILD_DIR, "ner-kavach-release-unsigned.apk")
    if os.path.exists(aligned_apk):
        os.remove(aligned_apk)
    run_cmd(f'"{ZIPALIGN}" -v -p 4 "{unaligned_apk}" "{aligned_apk}"')

    print("🔑 [Step 8] Creating permanent keystore and signing APK...")
    keystore = os.path.join(PROJECT_DIR, "ner_kavach_release.keystore")
    if not os.path.exists(keystore):
        build_ks = os.path.join(BUILD_DIR, "debug.keystore")
        if os.path.exists(build_ks):
            shutil.copy2(build_ks, keystore)
        else:
            run_cmd(f'"{KEYTOOL}" -genkey -v -keystore "{keystore}" -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"')

    final_apk = os.path.join(PROJECT_DIR, "NER_KAVACH_3.0.apk")
    if os.path.exists(final_apk):
        os.remove(final_apk)
    shutil.copy2(aligned_apk, final_apk)
    run_cmd(f'"{APKSIGNER}" sign --ks "{keystore}" --ks-pass pass:android --ks-key-alias androiddebugkey --key-pass pass:android "{final_apk}"')

    print(f"🎉 [SUCCESS] APK built and signed successfully: {final_apk}")
    return final_apk

def deploy_to_vivo(apk_path):
    print("📲 [Step 9] Checking connected Vivo phone...")
    devices_out = run_cmd(f'"{ADB_PATH}" devices')
    print(devices_out)
    if "10BG490UHC00DS7" in devices_out or "device" in devices_out:
        print("📲 [Step 10] Deploying NER_KAVACH_3.0.apk onto Vivo V2502 phone...")
        run_cmd(f'"{ADB_PATH}" install -r -g "{apk_path}"')
        run_cmd(f'"{ADB_PATH}" shell appops set com.nerkavach.app SYSTEM_ALERT_WINDOW allow')
        print("🚀 [Step 11] Launching NER KAVACH 3.0 on your phone...")
        run_cmd(f'"{ADB_PATH}" shell am start -n com.nerkavach.app/.MainActivity')
        print("✅ APPLICATION SUCCESSFULLY INSTALLED AND RUNNING ON YOUR VIVO PHONE!")
    else:
        print("⚠️ No device detected. Please ensure USB debugging is enabled on your Vivo phone.")

if __name__ == "__main__":
    apk = build_apk()
    deploy_to_vivo(apk)
