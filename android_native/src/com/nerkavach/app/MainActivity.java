package com.nerkavach.app;

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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.SmsManager;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private WebView webView;
    private TextToSpeech tts;
    private ToneGenerator toneGen;
    private Vibrator vibrator;
    private ServerSocket p2pServerSocket;
    private DatagramSocket p2pUdpSocket;
    private volatile boolean isP2pListening = true;
    public static MainActivity instance;
    private static final String CHANNEL_ID = "NER_KAVACH_ALERTS";
    private static final int P2P_PORT = 8989;
    private static final int LOCATION_PERMISSION_REQ = 1001;
    public static volatile long lastTriggerTime = 0;

    public static MainActivity getInstance() {
        return instance;
    }

    public void stopLocalTone() {
        try {
            if (toneGen != null) {
                toneGen.stopTone();
                toneGen.release();
                toneGen = null;
            }
        } catch (Exception ignored) {}
        try {
            if (vibrator != null) {
                vibrator.cancel();
            }
        } catch (Exception ignored) {}
        try {
            if (tts != null) {
                tts.stop();
            }
        } catch (Exception ignored) {}
        try {
            if (webView != null) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.evaluateJavascript("if(window.stopAllSirenAudio) { window.stopAllSirenAudio(); }", null);
                    }
                });
            }
        } catch (Exception ignored) {}
        initToneAndVibrator();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        initToneAndVibrator();
        initTTS();
        createNotificationChannel();
        startP2pSocketServer();
        checkAndRequestLocationPermission();

        try {
            Intent alertServiceIntent = new Intent(this, AlertReceiverService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(alertServiceIntent);
            } else {
                startService(alertServiceIntent);
            }
        } catch (Exception ignored) {}

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

    private void checkAndRequestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, LOCATION_PERMISSION_REQ);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQ) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                if (webView != null) {
                    webView.evaluateJavascript("if(window.onNativeLocationGranted) window.onNativeLocationGranted();", null);
                }
            }
        }
    }

    private android.net.wifi.WifiManager.MulticastLock multicastLock;

    // 📡 Background P2P Wi-Fi Socket Listener (TCP + UDP Broadcast on Ports 8988 & 8989)
    private void startP2pSocketServer() {
        // 0. Acquire Wi-Fi Multicast Lock to prevent Android from filtering UDP broadcast
        try {
            android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                multicastLock = wifiManager.createMulticastLock("NER_KAVACH_P2P_LOCK");
                multicastLock.setReferenceCounted(true);
                multicastLock.acquire();
            }
        } catch (Exception ignored) {}

        // 1. TCP ServerSocket Listener
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p2pServerSocket = new ServerSocket(P2P_PORT);
                    while (isP2pListening) {
                        final Socket client = p2pServerSocket.accept();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                                    StringBuilder sb = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        if (line.contains("##END_PACKET##")) {
                                            sb.append(line.replace("##END_PACKET##", ""));
                                            break;
                                        }
                                        sb.append(line);
                                    }
                                    final String packet = sb.toString().trim();

                                    // Send ACK back to sender mobile
                                    OutputStream os = client.getOutputStream();
                                    os.write("{\"status\":\"ACK_DELIVERED_TO_PEER\",\"code\":200}\n".getBytes("UTF-8"));
                                    os.flush();
                                    client.close();

                                    handleIncomingP2pPacket(packet);
                                } catch (Exception e) {}
                            }
                        }).start();
                    }
                } catch (Exception e) {}
            }
        }).start();

        // 2. UDP Broadcast Listener (Port 8989) for Zero-Configuration Peer Discovery
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p2pUdpSocket = new DatagramSocket(null);
                    p2pUdpSocket.setReuseAddress(true);
                    p2pUdpSocket.setBroadcast(true);
                    p2pUdpSocket.bind(new InetSocketAddress(P2P_PORT));
                    byte[] buffer = new byte[65507];
                    while (isP2pListening) {
                        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                        p2pUdpSocket.receive(datagram);
                        String raw = new String(datagram.getData(), 0, datagram.getLength(), "UTF-8").trim();
                        if (raw.contains("##END_PACKET##")) {
                            raw = raw.replace("##END_PACKET##", "").trim();
                        }
                        if (raw.length() > 0) {
                            handleIncomingP2pPacket(raw);
                        }
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    public void handleIncomingP2pPacket(final String packet) {
        if (packet == null || packet.trim().isEmpty()) return;
        try {
            String myId = AlertReceiverService.getDeviceId(MainActivity.this);
            if (packet.contains("\"origin_device\":\"" + myId + "\"") ||
                packet.contains("\"device_id\":\"" + myId + "\"") ||
                packet.contains("\"sender_id\":\"" + myId + "\"")) {
                android.util.Log.i("MainActivity", "Ignored incoming P2P packet from self device: " + myId);
                return;
            }
        } catch (Exception ignored) {}

        final boolean isLiveBeacon = packet.contains("\"type\":\"LIVE_SENSOR_BEACON\"") || packet.contains("\"beacon_type\":\"LIVE_COORDS\"");

        final String b64Packet = android.util.Base64.encodeToString(packet.getBytes(java.nio.charset.StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isLiveBeacon) {
                        // Live GPS coordinate stream from peer mobile (Zero Internet)
                        webView.evaluateJavascript("if(window.onP2pBeaconReceivedBase64) { window.onP2pBeaconReceivedBase64('" + b64Packet + "'); }", null);
                    } else {
                        // Emergency Hazard Report / Advisory from Peer Phone
                        if (toneGen != null) {
                            toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 1200);
                        }
                        if (vibrator != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(600);
                            }
                        }
                        Toast.makeText(MainActivity.this, "🚨 INCOMING P2P HAZARD REPORT FROM PEER MOBILE!", Toast.LENGTH_LONG).show();

                        webView.evaluateJavascript("if(window.onP2pPacketReceivedBase64) { window.onP2pPacketReceivedBase64('" + b64Packet + "'); }", null);
                    }
                } catch (Exception e) {}
            }
        });
    }

    public class AndroidBridge {
        @JavascriptInterface
        public void triggerSiren(int durationMs) {
            triggerSirenWithAdvisory(durationMs, "🚨 EMERGENCY LANDSLIDE ALERT!", "Critical slope saturation detected in Arunachal warning corridor. Evacuate to safe shelter immediately!");
        }

        @JavascriptInterface
        public void triggerSirenWithAdvisory(int durationMs, final String title, final String body) {
            try {
                lastTriggerTime = System.currentTimeMillis();
                AlertReceiverService.recordLocalBroadcast(lastTriggerTime);

                // Gentle tap feedback on sender device only (not siren!)
                if (vibrator != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(50);
                    }
                }

                // 🌐 Broadcast to all devices running the app over Internet HTTPS and Local UDP
                AlertReceiverService.broadcastSirenAlert(MainActivity.this, title, body);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "🚨 Emergency Landslide Alert Broadcasted to All Mobiles!", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {}
        }

        @JavascriptInterface
        public void stopSiren() {
            stopLocalTone();
            AlertReceiverService.stopAlarm(MainActivity.this);
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

        @JavascriptInterface
        public String getLastKnownLocation() {
            try {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (lm != null && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc == null) {
                        loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    if (loc != null) {
                        return loc.getLatitude() + "," + loc.getLongitude() + "," + loc.getAccuracy();
                    }
                }
            } catch (Exception e) {}
            return "";
        }

        @JavascriptInterface
        public void requestLocationPermission() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkAndRequestLocationPermission();
                }
            });
        }

        @JavascriptInterface
        public String getDeviceIpAddress() {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            String sAddr = addr.getHostAddress();
                            if (sAddr.indexOf(':') < 0) { // IPv4
                                return sAddr;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
            return "10.55.131.190";
        }

        @JavascriptInterface
        public String getLocalSubnetBroadcastIp() {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    for (InterfaceAddress addr : intf.getInterfaceAddresses()) {
                        InetAddress bcast = addr.getBroadcast();
                        if (bcast != null) {
                            return bcast.getHostAddress();
                        }
                    }
                }
            } catch (Exception ignored) {}
            return "255.255.255.255";
        }

        @JavascriptInterface
        public String getDiscoveredPeers() {
            JSONArray peers = new JSONArray();
            try {
                BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.trim().split("\\s+");
                    if (tokens.length >= 4 && !tokens[0].equalsIgnoreCase("IP")) {
                        String ip = tokens[0];
                        String flags = tokens[2];
                        String mac = tokens[3];
                        // Flags 0x2 indicates valid, reachable ARP entry
                        if (!flags.equals("0x0") && !mac.equals("00:00:00:00:00:00") && !ip.equals("0.0.0.0")) {
                            peers.put(ip);
                        }
                    }
                }
                br.close();
            } catch (Exception ignored) {}

            // If empty, supply common hotspot client defaults
            if (peers.length() == 0) {
                peers.put("10.55.131.195");
                peers.put("192.168.43.1");
                peers.put("192.168.43.2");
            }
            return peers.toString();
        }

        @JavascriptInterface
        public String getDeviceId() {
            try {
                return AlertReceiverService.getDeviceId(MainActivity.this);
            } catch (Exception e) {
                return "DEV-VIVO-DEMO";
            }
        }

        @JavascriptInterface
        public void sendP2pPacketToPeer(final String targetIp, final String packetJson) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean delivered = false;
                    final String payload = packetJson + "\n##END_PACKET##\n";
                    final byte[] bytes;
                    try {
                        bytes = payload.getBytes("UTF-8");
                    } catch (Exception e) { return; }

                    final int[] targetPorts = new int[]{8988, 8989};

                    // 1. BROADCAST OVER UDP (Universal Multi-Interface Subnet Broadcast)
                    try {
                        DatagramSocket udpSender = new DatagramSocket();
                        udpSender.setBroadcast(true);

                        // Universal fallback 255.255.255.255 on both ports
                        for (int port : targetPorts) {
                            try {
                                DatagramPacket p1 = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("255.255.255.255"), port);
                                udpSender.send(p1);
                            } catch (Exception ignored) {}
                        }

                        // Broadcast across all active network interfaces (e.g. Wi-Fi AP, Wi-Fi Direct, Local LAN)
                        try {
                            java.util.Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                            while (interfaces.hasMoreElements()) {
                                NetworkInterface networkInterface = interfaces.nextElement();
                                if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;
                                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                                    InetAddress broadcast = interfaceAddress.getBroadcast();
                                    if (broadcast != null) {
                                        for (int port : targetPorts) {
                                            try {
                                                DatagramPacket p = new DatagramPacket(bytes, bytes.length, broadcast, port);
                                                udpSender.send(p);
                                            } catch (Exception ignored) {}
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {}

                        // Broadcast to standard hotspot subnet (192.168.43.255)
                        for (int port : targetPorts) {
                            try {
                                DatagramPacket p3 = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("192.168.43.255"), port);
                                udpSender.send(p3);
                            } catch (Exception ignored) {}
                        }

                        udpSender.close();
                        delivered = true;
                    } catch (Exception ignored) {}

                    // 2. PARALLEL DIRECT TCP TO ALL PEERS
                    List<String> targets = new ArrayList<>();
                    if (targetIp != null && !targetIp.trim().isEmpty() && !targetIp.equals("AUTO_BROADCAST")) {
                        targets.add(targetIp.trim());
                    }

                    // Add ARP peers
                    try {
                        JSONArray arpList = new JSONArray(getDiscoveredPeers());
                        for (int i = 0; i < arpList.length(); i++) {
                            String peerIp = arpList.getString(i);
                            if (!targets.contains(peerIp)) targets.add(peerIp);
                        }
                    } catch (Exception ignored) {}

                    // Common hotspot and gateway IP addresses
                    if (!targets.contains("192.168.43.1")) targets.add("192.168.43.1");
                    if (!targets.contains("192.168.43.2")) targets.add("192.168.43.2");
                    if (!targets.contains("10.55.131.195")) targets.add("10.55.131.195");

                    for (String ip : targets) {
                        for (int port : targetPorts) {
                            try {
                                Socket socket = new Socket();
                                socket.connect(new InetSocketAddress(ip, port), 1000);
                                OutputStream os = socket.getOutputStream();
                                os.write(bytes);
                                os.flush();

                                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                                String ack = reader.readLine();
                                socket.close();
                                delivered = true;
                                break;
                            } catch (Exception ignored) {}
                        }
                    }

                    final boolean finalDelivered = delivered;
                    final String finalTarget = (targetIp != null && !targetIp.isEmpty()) ? targetIp : "Subnet Broadcast";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.evaluateJavascript("if(window.onP2pSendSuccess) window.onP2pSendSuccess('" + finalTarget + "');", null);
                        }
                    });
                }
            }).start();
        }

        @JavascriptInterface
        public void shareReportViaQuickShare(final String title, final String content) {
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TITLE, title);
                sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, "Beam via Android Quick Share / Wi-Fi Direct");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(shareIntent);
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Share error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @JavascriptInterface
        public boolean isAccessibilityEnabled() {
            return WhatsAppAutoSendService.isAccessibilityEnabled(MainActivity.this);
        }

        @JavascriptInterface
        public void openAccessibilitySettings() {
            try {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Please enable 'NER KAVACH 3.0' under Installed Services for 100% automated background WhatsApp dispatch.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Could not open Accessibility Settings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @JavascriptInterface
        public void dispatchWhatsAppDirect(final String phone, final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent waIntent = WhatsAppAutoSendService.buildWhatsAppIntent(MainActivity.this, phone, message);
                        startActivity(waIntent);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "WhatsApp launch error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @JavascriptInterface
        public void dispatchAdvisoryViaSmsAndWhatsApp(final String numbersCsv, final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] rawNumbers = (numbersCsv != null && !numbersCsv.isEmpty())
                        ? numbersCsv.split(",")
                        : new String[]{"8341687289", "6281967693", "7287006100"};

                    SmsManager sms;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            sms = getSystemService(SmsManager.class);
                            if (sms == null) {
                                sms = SmsManager.getDefault();
                            }
                        } else {
                            sms = SmsManager.getDefault();
                        }
                    } catch (Exception e) {
                        sms = SmsManager.getDefault();
                    }

                    // Direct silent background SMS to all 3 numbers without external app redirection
                    for (int i = 0; i < rawNumbers.length; i++) {
                        String num = rawNumbers[i].trim().replaceAll("[^0-9+]", "");
                        if (num.isEmpty()) continue;

                        try {
                            if (sms != null) {
                                ArrayList<String> parts = sms.divideMessage(message);
                                sms.sendMultipartTextMessage(num, null, parts, null, null);
                                android.util.Log.i("NER_SMS", "Direct SMS successfully sent to: " + num);
                            }
                        } catch (Exception e) {
                            android.util.Log.e("NER_SMS", "Direct SMS failed for " + num + ": " + e.getMessage());
                        }

                        // Delay 250ms between numbers so the carrier/modem queue handles each SMS smoothly
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ignored) {}
                    }

                    final String[] finalNumbers = rawNumbers;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean hasAccessibility = WhatsAppAutoSendService.isAccessibilityEnabled(MainActivity.this);
                            if (hasAccessibility) {
                                Toast.makeText(MainActivity.this, "📲 SMS sent! Automated WhatsApp dispatcher sending to 3 contacts...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "📲 SMS sent! Launching WhatsApp advisory...", Toast.LENGTH_SHORT).show();
                            }
                            WhatsAppAutoSendService.startAutoSend(MainActivity.this, finalNumbers, message);
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        isP2pListening = false;
        try {
            if (p2pServerSocket != null && !p2pServerSocket.isClosed()) {
                p2pServerSocket.close();
            }
        } catch (Exception e) {}
        try {
            if (p2pUdpSocket != null && !p2pUdpSocket.isClosed()) {
                p2pUdpSocket.close();
            }
        } catch (Exception e) {}
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (toneGen != null) {
            toneGen.release();
        }
        if (instance == this) {
            instance = null;
        }
        super.onDestroy();
    }
}
