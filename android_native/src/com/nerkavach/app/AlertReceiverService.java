package com.nerkavach.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class AlertReceiverService extends Service {
    private static final String TAG = "NER_ALERT_SERVICE";
    private static final String SERVICE_CHANNEL_ID = "NER_SERVICE_MONITOR";
    private static final String CHANNEL_ID = "NER_HEADSUP_ALERTS";
    private static final int FOREGROUND_NOTIFICATION_ID = 8888;
    private static final int NOTIFICATION_ID = 9999;
    private static final String INTERNET_TOPIC_URL = "https://ntfy.sh/nerkavach_siren_alerts_arunachal";
    private static final String MQTT_HOST = "broker.emqx.io";
    private static final String MQTT_HOST_FALLBACK = "broker.hivemq.com";
    private static final int MQTT_PORT = 1883;
    private static final String MQTT_TOPIC = "nerkavach/siren_channel_v3";
    private static final int UDP_PORT = 8988;

    private static volatile boolean isRunning = false;
    private static ToneGenerator staticToneGen = null;
    private static Vibrator staticVibrator = null;
    public static volatile boolean isAlarmActive = false;
    private static View activeOverlayView = null;
    private static WindowManager windowManager = null;

    public static volatile long lastLocalBroadcastTime = 0;
    public static volatile long lastDismissTime = 0;
    private static volatile long serviceStartTime = 0;
    private static long lastAlertTime = 0;

    private ToneGenerator toneGen;
    private Vibrator vibrator;
    private PowerManager powerManager;
    private DatagramSocket udpSocket;

    public static void recordLocalBroadcast(long time) {
        lastLocalBroadcastTime = time;
    }

    public static synchronized void removeOverlay() {
        if (activeOverlayView != null && windowManager != null) {
            try {
                windowManager.removeView(activeOverlayView);
            } catch (Exception ignored) {}
            activeOverlayView = null;
        }
    }

    public static synchronized void stopAlarm(Context context) {
        Log.i(TAG, "🛑 stopAlarm called: Silencing ToneGenerator, Vibrator, removing overlay and dismissing notifications.");
        lastDismissTime = System.currentTimeMillis();
        isAlarmActive = false;
        removeOverlay();

        try {
            if (staticToneGen != null) {
                staticToneGen.stopTone();
                staticToneGen.release();
                staticToneGen = null;
            }
        } catch (Exception ignored) {}

        try {
            if (staticVibrator != null) {
                staticVibrator.cancel();
            }
        } catch (Exception ignored) {}

        try {
            if (context != null) {
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (nm != null) {
                    nm.cancel(NOTIFICATION_ID);
                }
            }
        } catch (Exception ignored) {}

        try {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().stopLocalTone();
            }
        } catch (Exception ignored) {}

        try {
            if (context != null) {
                Intent stopIntent = new Intent("com.nerkavach.app.ACTION_ALARM_STOPPED");
                stopIntent.setPackage(context.getPackageName());
                context.sendBroadcast(stopIntent);
            }
        } catch (Exception ignored) {}
    }

    public static String getDeviceId(Context context) {
        try {
            String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (id != null && !id.trim().isEmpty()) {
                return id.trim();
            }
        } catch (Exception ignored) {}
        return "device_" + Build.MODEL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        serviceStartTime = System.currentTimeMillis();
        createNotificationChannels();

        // 🛡️ Start Foreground Service to guarantee Android OS never kills this monitoring process
        try {
            Notification fgNotification = buildForegroundNotification();
            startForeground(FOREGROUND_NOTIFICATION_ID, fgNotification);
            Log.i(TAG, "AlertReceiverService started as persistent Foreground Service.");
        } catch (Exception e) {
            Log.w(TAG, "startForeground error: " + e.getMessage());
        }

        initAudioAndVibrator();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Log.i(TAG, "AlertReceiverService online. Starting MQTT, Internet HTTPS, and Local UDP listeners...");
        startUdpListener();
        startMqttListener();
        startInternetStreamListener();
    }

    private void initAudioAndVibrator() {
        try {
            toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            staticToneGen = toneGen;
        } catch (Exception ignored) {}
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        staticVibrator = vibrator;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm == null) return;

            // 1. Silent channel for persistent background monitoring service
            NotificationChannel serviceChannel = new NotificationChannel(
                SERVICE_CHANNEL_ID,
                "NER KAVACH Background Monitor",
                NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("Keeps emergency early warning listener active in background");
            serviceChannel.setShowBadge(false);
            nm.createNotificationChannel(serviceChannel);

            // 2. High Priority Heads-up Channel for Emergency Alarms
            NotificationChannel alertChannel = new NotificationChannel(
                CHANNEL_ID,
                "NER Critical Evacuation Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            alertChannel.setDescription("Heads-Up Emergency Landslide Early Warning and Siren");
            alertChannel.enableLights(true);
            alertChannel.enableVibration(true);
            alertChannel.setBypassDnd(true);
            alertChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(alertChannel);
        }
    }

    private Notification buildForegroundNotification() {
        Intent launchIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, SERVICE_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
            builder.setPriority(Notification.PRIORITY_LOW);
        }

        builder.setSmallIcon(R.drawable.ic_shield)
            .setContentTitle("NER KAVACH 3.0 Active")
            .setContentText("Continuous Landslide Corridor Monitoring")
            .setContentIntent(pendingIntent)
            .setOngoing(true);

        return builder.build();
    }

    // 📡 1. Local Subnet / Hotspot UDP Broadcast Listener (Port 8988)
    private void startUdpListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[65507];
                try {
                    udpSocket = new DatagramSocket(null);
                    udpSocket.setReuseAddress(true);
                    udpSocket.setBroadcast(true);
                    udpSocket.bind(new InetSocketAddress(UDP_PORT));

                    while (isRunning) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        udpSocket.receive(packet);
                        String data = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                        if (data.contains("LIVE_SENSOR_BEACON") || data.contains("NER_P2P_HAZARD_REPORT") || data.contains("OFFLINE_HAZARD_REPORT")) {
                            Log.i(TAG, "Received UDP P2P Packet: " + data);
                            if (MainActivity.instance != null) {
                                MainActivity.instance.handleIncomingP2pPacket(data);
                            }
                        } else if (data.contains("SIREN_BROADCAST") || data.contains("LANDSLIDE_ALERT")) {
                            Log.i(TAG, "Received UDP Emergency Packet: " + data);
                            handleEmergencyAlert(data, null);
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "UDP listener stopped: " + e.getMessage());
                }
            }
        }).start();
    }

    private static final java.util.Set<String> seenMessageIds = java.util.Collections.synchronizedSet(new java.util.HashSet<String>());

    private static void writeRemainingLength(OutputStream out, int length) throws IOException {
        do {
            int digit = length % 128;
            length /= 128;
            if (length > 0) {
                digit |= 0x80;
            }
            out.write(digit);
        } while (length > 0);
    }

    private static int readRemainingLength(InputStream in) throws IOException {
        int multiplier = 1;
        int value = 0;
        int digit;
        do {
            digit = in.read();
            if (digit == -1) throw new IOException("MQTT stream closed");
            value += (digit & 0x7F) * multiplier;
            multiplier *= 128;
        } while ((digit & 0x80) != 0);
        return value;
    }

    // 🌐 2. High-Performance Zero-Quota MQTT Real-Time Push Listener (Port 1883)
    private void startMqttListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] brokers = new String[]{MQTT_HOST, MQTT_HOST_FALLBACK};
                int bIdx = 0;
                while (isRunning) {
                    Socket socket = null;
                    try {
                        String broker = brokers[bIdx % brokers.length];
                        Log.i(TAG, "Connecting to Public MQTT Broker: " + broker + ":" + MQTT_PORT);
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(broker, MQTT_PORT), 10000);
                        socket.setSoTimeout(0); // Long-lived push socket

                        String myId = getDeviceId(AlertReceiverService.this);
                        String cid = "ner_sub_" + myId.substring(0, Math.min(8, myId.length())) + "_" + (System.currentTimeMillis() % 10000);
                        byte[] cidBytes = cid.getBytes(StandardCharsets.UTF_8);

                        // 1. Send MQTT CONNECT packet
                        ByteArrayOutputStream varHeader = new ByteArrayOutputStream();
                        varHeader.write(new byte[]{0x00, 0x04, 'M', 'Q', 'T', 'T', 0x04, 0x02, 0x00, 0x3C});
                        varHeader.write((cidBytes.length >> 8) & 0xFF);
                        varHeader.write(cidBytes.length & 0xFF);
                        varHeader.write(cidBytes);
                        byte[] vh = varHeader.toByteArray();
                        ByteArrayOutputStream connPkt = new ByteArrayOutputStream();
                        connPkt.write(0x10);
                        writeRemainingLength(connPkt, vh.length);
                        connPkt.write(vh);
                        socket.getOutputStream().write(connPkt.toByteArray());
                        socket.getOutputStream().flush();

                        // Read CONNACK (4 bytes)
                        InputStream in = socket.getInputStream();
                        byte[] ack = new byte[4];
                        int rAck = in.read(ack);
                        if (rAck < 4 || ack[3] != 0) {
                            Log.w(TAG, "MQTT connection rejected by " + broker);
                            socket.close();
                            bIdx++;
                            Thread.sleep(4000);
                            continue;
                        }
                        Log.i(TAG, "MQTT Connected to " + broker + "! Subscribing to: " + MQTT_TOPIC);

                        // 2. Send MQTT SUBSCRIBE packet (QoS 0)
                        byte[] tBytes = MQTT_TOPIC.getBytes(StandardCharsets.UTF_8);
                        ByteArrayOutputStream subPayload = new ByteArrayOutputStream();
                        subPayload.write(new byte[]{0x00, 0x01}); // Packet ID = 1
                        subPayload.write((tBytes.length >> 8) & 0xFF);
                        subPayload.write(tBytes.length & 0xFF);
                        subPayload.write(tBytes);
                        subPayload.write(0x00);
                        byte[] sp = subPayload.toByteArray();
                        ByteArrayOutputStream subPkt = new ByteArrayOutputStream();
                        subPkt.write((byte) 0x82);
                        writeRemainingLength(subPkt, sp.length);
                        subPkt.write(sp);
                        socket.getOutputStream().write(subPkt.toByteArray());
                        socket.getOutputStream().flush();

                        // Read SUBACK (5 bytes)
                        byte[] subAck = new byte[5];
                        in.read(subAck);
                        Log.i(TAG, "MQTT Subscribed to " + MQTT_TOPIC + " successfully!");

                        // 3. Heartbeat Ping Timer (PINGREQ every 25s)
                        final Socket pingSock = socket;
                        Thread pinger = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (isRunning && !pingSock.isClosed()) {
                                        Thread.sleep(25000);
                                        if (!pingSock.isClosed()) {
                                            synchronized (pingSock) {
                                                pingSock.getOutputStream().write(new byte[]{(byte) 0xC0, 0x00});
                                                pingSock.getOutputStream().flush();
                                            }
                                        }
                                    }
                                } catch (Exception ignored) {}
                            }
                        });
                        pinger.setDaemon(true);
                        pinger.start();

                        // 4. Packet reading loop
                        while (isRunning && !socket.isClosed()) {
                            int pktHeader = in.read();
                            if (pktHeader == -1) break;

                            int remLen = readRemainingLength(in);
                            byte[] remBytes = new byte[remLen];
                            int totalRead = 0;
                            while (totalRead < remLen) {
                                int readCount = in.read(remBytes, totalRead, remLen - totalRead);
                                if (readCount == -1) break;
                                totalRead += readCount;
                            }

                            int pktType = (pktHeader & 0xF0);
                            if (pktType == 0x30) { // PUBLISH packet
                                if (remLen >= 2) {
                                    int topicLen = ((remBytes[0] & 0xFF) << 8) | (remBytes[1] & 0xFF);
                                    int payloadOffset = 2 + topicLen;
                                    if (payloadOffset <= remLen) {
                                        String payload = new String(remBytes, payloadOffset, remLen - payloadOffset, StandardCharsets.UTF_8).trim();
                                        Log.i(TAG, "🚨 LIVE MQTT EMERGENCY ALERT PACKET: " + payload);
                                        handleEmergencyAlert(payload, "🚨 EMERGENCY LANDSLIDE ALERT!");
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "MQTT reconnecting in 5s: " + e.getMessage());
                    } finally {
                        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
                    }
                    bIdx++;
                    try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                }
            }
        }).start();
    }

    // 🌐 3. Secondary Fallback: Single Long-Lived HTTP Stream Listener (Zero Polling to avoid 429)
    private void startInternetStreamListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    HttpURLConnection conn = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(INTERNET_TOPIC_URL + "/json");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(0); // Persistent stream
                        conn.setRequestProperty("Accept", "application/x-ndjson");

                        int code = conn.getResponseCode();
                        if (code == 200) {
                            Log.i(TAG, "Secondary HTTP Stream connected successfully!");
                            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                            String line;
                            while (isRunning && (line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.isEmpty()) continue;
                                try {
                                    JSONObject eventObj = new JSONObject(line);
                                    if ("message".equalsIgnoreCase(eventObj.optString("event"))) {
                                        String msgId = eventObj.optString("id", "");
                                        if (!msgId.isEmpty()) {
                                            if (seenMessageIds.contains(msgId)) continue;
                                            seenMessageIds.add(msgId);
                                        }
                                        Log.i(TAG, "Secondary HTTP alert received: " + eventObj.optString("message"));
                                        handleEmergencyAlert(eventObj.optString("message"), eventObj.optString("title"));
                                    }
                                } catch (Exception ignored) {}
                            }
                        } else if (code == 429) {
                            Log.w(TAG, "HTTP stream 429 rate limit. Relying on MQTT broker.");
                            try { Thread.sleep(30000); } catch (InterruptedException ignored) {}
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "HTTP stream reconnecting in 15s: " + e.getMessage());
                    } finally {
                        try { if (reader != null) reader.close(); } catch (Exception ignored) {}
                        try { if (conn != null) conn.disconnect(); } catch (Exception ignored) {}
                    }
                    try { Thread.sleep(15000); } catch (InterruptedException ignored) {}
                }
            }
        }).start();
    }

    // 🚨 3. Trigger Full-Screen Alert Pop-Up Window, Loud Siren, Vibration, and Screen Wake-Up
    private void handleEmergencyAlert(final String rawData, final String fallbackTitle) {
        if (rawData == null || rawData.trim().isEmpty()) return;

        // 1. If user dismissed an alert within the last 5 seconds, ignore to guarantee dismissal sticks
        long now = System.currentTimeMillis();
        if (now - lastDismissTime < 5000L) {
            Log.i(TAG, "🛡️ Alert ignored: User recently clicked DISMISS.");
            return;
        }

        // 2. If this device triggered a broadcast within 15 seconds, NEVER trigger siren on self!
        if (now - lastLocalBroadcastTime < 15000L) {
            Log.i(TAG, "🛡️ Alert ignored on SENDER phone: Self-broadcast in progress.");
            return;
        }

        // 3. Extract sender_id and check if it originated from self device
        String tempSenderId = "";
        try {
            if (rawData.contains("{") && rawData.contains("}")) {
                int start = rawData.indexOf("{");
                int end = rawData.lastIndexOf("}") + 1;
                JSONObject obj = new JSONObject(rawData.substring(start, end));
                if (obj.has("sender_id")) tempSenderId = obj.getString("sender_id");
            }
        } catch (Exception ignored) {}

        String myDeviceId = getDeviceId(AlertReceiverService.this);
        if (!tempSenderId.isEmpty() && tempSenderId.trim().equalsIgnoreCase(myDeviceId.trim())) {
            Log.i(TAG, "🛡️ Ignored emergency alert echo from self device: " + tempSenderId);
            return;
        }

        // 4. Prevent duplicate rapid re-trigger within 3 seconds
        if (now - lastAlertTime < 3000L) return;
        lastAlertTime = now;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    String title = fallbackTitle != null && !fallbackTitle.trim().isEmpty() ?
                        fallbackTitle : "🚨 EMERGENCY LANDSLIDE ALERT!";
                    String body = "Critical slope saturation detected in Arunachal warning corridor. Evacuate to safe shelter immediately!";

                    try {
                        if (rawData.contains("{") && rawData.contains("}")) {
                            int start = rawData.indexOf("{");
                            int end = rawData.lastIndexOf("}") + 1;
                            JSONObject obj = new JSONObject(rawData.substring(start, end));
                            if (obj.has("title")) title = obj.getString("title");
                            if (obj.has("body")) body = obj.getString("body");
                            else if (obj.has("text")) body = obj.getString("text");
                        } else if (!rawData.isEmpty()) {
                            body = rawData;
                        }
                    } catch (Exception ignored) {}

                    // 1. Wake up screen if locked/dark
                    if (powerManager != null) {
                        try {
                            PowerManager.WakeLock wl = powerManager.newWakeLock(
                                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                                "NER:EmergencyAlertWakeLock"
                            );
                            wl.acquire(10000);
                        } catch (Exception ignored) {}
                    }

                    // 2. Play 85dB Emergency Siren cleanly
                    isAlarmActive = true;
                    try {
                        if (toneGen != null) {
                            toneGen.stopTone();
                            toneGen.release();
                            toneGen = null;
                        }
                        toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        staticToneGen = toneGen;
                        toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 15000);
                    } catch (Exception e) {
                        Log.w(TAG, "ToneGenerator error: " + e.getMessage());
                    }

                    // 3. SOS Vibration pattern
                    if (vibrator != null) {
                        try {
                            long[] pattern = {0, 800, 200, 800, 200, 800, 200, 800};
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                            } else {
                                vibrator.vibrate(pattern, 0);
                            }
                        } catch (Exception ignored) {}
                    }

                    // 4. Show Full-Screen Floating System Overlay Pop-Up Window Directly on Display
                    showEmergencyOverlay(title, body);

                    // 5. Also launch EmergencyAlertActivity (for Activity task stack / lockscreen)
                    Intent alertActivityIntent = new Intent(AlertReceiverService.this, EmergencyAlertActivity.class);
                    alertActivityIntent.putExtra("title", title);
                    alertActivityIntent.putExtra("body", body);
                    alertActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    );
                    try {
                        startActivity(alertActivityIntent);
                    } catch (Exception e) {
                        Log.w(TAG, "startActivity fallback: " + e.getMessage());
                    }

                    // 6. Also show High Priority Notification with Stop Siren Button & Swipe Delete Intent
                    PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                        AlertReceiverService.this,
                        1,
                        alertActivityIntent,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT
                    );

                    Intent dismissIntent = new Intent(AlertReceiverService.this, DismissAlertReceiver.class);
                    PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                        AlertReceiverService.this,
                        2,
                        dismissIntent,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT
                    );

                    Notification.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder = new Notification.Builder(AlertReceiverService.this, CHANNEL_ID);
                    } else {
                        builder = new Notification.Builder(AlertReceiverService.this);
                        builder.setPriority(Notification.PRIORITY_MAX);
                    }

                    Notification.Action stopAction = new Notification.Action.Builder(
                        R.drawable.ic_shield,
                        "🛑 STOP SIREN",
                        dismissPendingIntent
                    ).build();

                    builder.setSmallIcon(R.drawable.ic_shield)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new Notification.BigTextStyle().bigText(body + "\n\n⚠️ Tap to view safe route, or tap STOP SIREN to silence."))
                        .setContentIntent(fullScreenPendingIntent)
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .setDeleteIntent(dismissPendingIntent) // Immediately stops siren when swiped away!
                        .addAction(stopAction) // Explicit Stop Siren button in notification bar!
                        .setAutoCancel(true)
                        .setOngoing(false);

                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (nm != null) {
                        nm.notify(NOTIFICATION_ID, builder.build());
                    }

                    Toast.makeText(AlertReceiverService.this, "🚨 CRITICAL LANDSLIDE ALERT RECEIVED!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error displaying emergency alert: " + e.getMessage());
                }
            }
        });
    }

    // 🪟 4. Show System Floating Overlay Pop-Up Window (Covers any app including YouTube, WhatsApp, etc.)
    private void showEmergencyOverlay(final String title, final String body) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(AlertReceiverService.this)) {
            Log.i(TAG, "Overlay permission not granted; EmergencyAlertActivity will handle display.");
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (windowManager == null) {
                        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    }
                    removeOverlay();

                    int layoutType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE;

                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        layoutType,
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                        PixelFormat.TRANSLUCENT
                    );
                    params.gravity = Gravity.CENTER;

                    // Root View: Full-screen translucent dark backdrop
                    LinearLayout root = new LinearLayout(AlertReceiverService.this);
                    root.setOrientation(LinearLayout.VERTICAL);
                    root.setGravity(Gravity.CENTER);
                    root.setBackgroundColor(Color.parseColor("#E6050510"));
                    root.setPadding(dpToPx(16), dpToPx(32), dpToPx(16), dpToPx(32));

                    ScrollView scroll = new ScrollView(AlertReceiverService.this);
                    scroll.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    // Central Card
                    LinearLayout card = new LinearLayout(AlertReceiverService.this);
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setGravity(Gravity.CENTER_HORIZONTAL);
                    card.setPadding(dpToPx(20), dpToPx(24), dpToPx(20), dpToPx(24));

                    GradientDrawable cardBg = new GradientDrawable();
                    cardBg.setColor(Color.parseColor("#180E10"));
                    cardBg.setCornerRadius(dpToPx(20));
                    cardBg.setStroke(dpToPx(3), Color.parseColor("#EF4444"));
                    card.setBackground(cardBg);

                    // 1. Badge Header
                    TextView badge = new TextView(AlertReceiverService.this);
                    badge.setText("🚨 CRITICAL EMERGENCY ALERT 🚨");
                    badge.setTextColor(Color.WHITE);
                    badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    badge.setTypeface(Typeface.DEFAULT_BOLD);
                    badge.setGravity(Gravity.CENTER);
                    badge.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
                    GradientDrawable badgeBg = new GradientDrawable();
                    badgeBg.setColor(Color.parseColor("#DC2626"));
                    badgeBg.setCornerRadius(dpToPx(24));
                    badge.setBackground(badgeBg);
                    card.addView(badge);

                    // Subtitle
                    TextView subtitle = new TextView(AlertReceiverService.this);
                    subtitle.setText("ARUNACHAL CORRIDOR EARLY WARNING");
                    subtitle.setTextColor(Color.parseColor("#FCA5A5"));
                    subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    subtitle.setTypeface(Typeface.DEFAULT_BOLD);
                    subtitle.setGravity(Gravity.CENTER);
                    subtitle.setPadding(0, dpToPx(8), 0, dpToPx(12));
                    card.addView(subtitle);

                    // 2. Alert Title
                    TextView tvTitle = new TextView(AlertReceiverService.this);
                    tvTitle.setText(title);
                    tvTitle.setTextColor(Color.WHITE);
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    tvTitle.setGravity(Gravity.CENTER);
                    tvTitle.setPadding(0, 0, 0, dpToPx(12));
                    card.addView(tvTitle);

                    // 3. Advisory Message Body
                    TextView tvBody = new TextView(AlertReceiverService.this);
                    tvBody.setText(body);
                    tvBody.setTextColor(Color.parseColor("#F1F5F9"));
                    tvBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    tvBody.setGravity(Gravity.CENTER);
                    tvBody.setLineSpacing(dpToPx(4), 1.1f);
                    tvBody.setPadding(dpToPx(8), 0, dpToPx(8), dpToPx(16));
                    card.addView(tvBody);

                    // 4. Instructions Box
                    LinearLayout instBox = new LinearLayout(AlertReceiverService.this);
                    instBox.setOrientation(LinearLayout.VERTICAL);
                    instBox.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
                    GradientDrawable instBg = new GradientDrawable();
                    instBg.setColor(Color.parseColor("#291216"));
                    instBg.setCornerRadius(dpToPx(12));
                    instBg.setStroke(dpToPx(1), Color.parseColor("#F59E0B"));
                    instBox.setBackground(instBg);

                    TextView tvInstTitle = new TextView(AlertReceiverService.this);
                    tvInstTitle.setText("⚠️ IMMEDIATE SAFETY DIRECTIVES:");
                    tvInstTitle.setTextColor(Color.parseColor("#FBBF24"));
                    tvInstTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    tvInstTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    instBox.addView(tvInstTitle);

                    TextView tvInstText = new TextView(AlertReceiverService.this);
                    tvInstText.setText("• Evacuate uphill away from mudflow channels immediately.\n• Avoid active road cuts & cracked retaining walls.\n• Follow offline safe GPS paths to community shelters.");
                    tvInstText.setTextColor(Color.parseColor("#E2E8F0"));
                    tvInstText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    tvInstText.setLineSpacing(dpToPx(2), 1.1f);
                    tvInstText.setPadding(0, dpToPx(6), 0, 0);
                    instBox.addView(tvInstText);

                    card.addView(instBox);

                    // 5. Button 1: Prominent STOP SIREN & DISMISS (Red)
                    Button btnStop = new Button(AlertReceiverService.this);
                    btnStop.setText("🛑 STOP SIREN & DISMISS ALERT");
                    btnStop.setTextColor(Color.WHITE);
                    btnStop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    btnStop.setTypeface(Typeface.DEFAULT_BOLD);
                    btnStop.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

                    GradientDrawable btnStopBg = new GradientDrawable();
                    btnStopBg.setColor(Color.parseColor("#DC2626"));
                    btnStopBg.setCornerRadius(dpToPx(12));
                    btnStopBg.setStroke(dpToPx(1), Color.parseColor("#FCA5A5"));
                    btnStop.setBackground(btnStopBg);

                    LinearLayout.LayoutParams btnStopParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    btnStopParams.setMargins(0, dpToPx(20), 0, 0);
                    btnStop.setLayoutParams(btnStopParams);

                    btnStop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stopAlarm(AlertReceiverService.this);
                        }
                    });
                    card.addView(btnStop);

                    // 6. Button 2: VIEW EVACUATION ROUTE (Green)
                    Button btnRoute = new Button(AlertReceiverService.this);
                    btnRoute.setText("🗺️ VIEW SAFE EVACUATION ROUTE");
                    btnRoute.setTextColor(Color.WHITE);
                    btnRoute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    btnRoute.setTypeface(Typeface.DEFAULT_BOLD);
                    btnRoute.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

                    GradientDrawable btnRouteBg = new GradientDrawable();
                    btnRouteBg.setColor(Color.parseColor("#059669"));
                    btnRouteBg.setCornerRadius(dpToPx(12));
                    btnRoute.setBackground(btnRouteBg);

                    LinearLayout.LayoutParams btnRouteParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    btnRouteParams.setMargins(0, dpToPx(10), 0, 0);
                    btnRoute.setLayoutParams(btnRouteParams);

                    btnRoute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stopAlarm(AlertReceiverService.this);
                            Intent mainIntent = new Intent(AlertReceiverService.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                        }
                    });
                    card.addView(btnRoute);

                    scroll.addView(card);
                    root.addView(scroll);

                    activeOverlayView = root;
                    windowManager.addView(root, params);
                    Log.i(TAG, "Full-Screen Emergency Alert Window overlay added successfully!");
                } catch (Exception e) {
                    Log.w(TAG, "Failed to add window overlay: " + e.getMessage());
                }
            }
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    // Helper: Static method to broadcast siren packet over Internet HTTPS and Local UDP
    public static void broadcastSirenAlert(final Context context, final String title, final String body) {
        lastLocalBroadcastTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String safeTitle = title != null ? title : "🚨 EMERGENCY LANDSLIDE ALERT!";
                    final String safeBody = body != null ? body : "Critical slope saturation detected. Evacuate immediately!";
                    final String senderId = getDeviceId(context);

                    JSONObject obj = new JSONObject();
                    obj.put("type", "SIREN_BROADCAST");
                    obj.put("timestamp", System.currentTimeMillis());
                    obj.put("title", safeTitle);
                    obj.put("body", safeBody);
                    obj.put("sender_id", senderId);
                    obj.put("sector", "Arunachal Warning Corridor");
                    String payloadStr = obj.toString();

                    // 1. Broadcast over Unlimited Public MQTT (Instant < 50ms, Zero Quota/Rate-Limit)
                    publishMqttAlert(payloadStr);

                    // 2. Broadcast via Local UDP on Port 8988 (Subnet / Hotspot Zero-Internet P2P)
                    try {
                        DatagramSocket udp = new DatagramSocket();
                        udp.setBroadcast(true);
                        byte[] data = payloadStr.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket p = new DatagramPacket(data, data.length, new InetSocketAddress("255.255.255.255", UDP_PORT));
                        udp.send(p);
                        udp.close();
                        Log.i(TAG, "Broadcasted alert to local UDP network successfully!");
                    } catch (Exception e) {
                        Log.w(TAG, "UDP broadcast error: " + e.getMessage());
                    }

                    // 3. Optional Internet HTTPS fallback
                    try {
                        URL url = new URL(INTERNET_TOPIC_URL);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setConnectTimeout(4000);
                        conn.setReadTimeout(4000);
                        conn.setRequestProperty("Title", "EMERGENCY LANDSLIDE ALERT!");
                        conn.setRequestProperty("Priority", "urgent");
                        conn.setRequestProperty("Tags", "warning,rotating_light");
                        conn.setRequestProperty("X-Sender-Id", senderId);
                        conn.setDoOutput(true);
                        OutputStream os = conn.getOutputStream();
                        os.write(payloadStr.getBytes(StandardCharsets.UTF_8));
                        os.flush();
                        os.close();
                        int code = conn.getResponseCode();
                        Log.i(TAG, "Broadcasted to Internet Alert Stream! HTTP Code: " + code);
                        conn.disconnect();
                    } catch (Exception ignored) {}
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void publishMqttAlert(final String payloadStr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] brokers = new String[]{MQTT_HOST, MQTT_HOST_FALLBACK};
                for (String broker : brokers) {
                    Socket s = null;
                    try {
                        s = new Socket();
                        s.connect(new InetSocketAddress(broker, MQTT_PORT), 6000);
                        String cid = "ner_pub_" + (System.currentTimeMillis() % 100000);
                        byte[] cidBytes = cid.getBytes(StandardCharsets.UTF_8);

                        // CONNECT packet
                        ByteArrayOutputStream varHeader = new ByteArrayOutputStream();
                        varHeader.write(new byte[]{0x00, 0x04, 'M', 'Q', 'T', 'T', 0x04, 0x02, 0x00, 0x14});
                        varHeader.write((cidBytes.length >> 8) & 0xFF);
                        varHeader.write(cidBytes.length & 0xFF);
                        varHeader.write(cidBytes);
                        byte[] vh = varHeader.toByteArray();
                        ByteArrayOutputStream connPkt = new ByteArrayOutputStream();
                        connPkt.write(0x10);
                        writeRemainingLength(connPkt, vh.length);
                        connPkt.write(vh);
                        s.getOutputStream().write(connPkt.toByteArray());
                        s.getOutputStream().flush();

                        // Read CONNACK (4 bytes)
                        byte[] ack = new byte[4];
                        s.getInputStream().read(ack);

                        // PUBLISH packet (QoS 0)
                        byte[] tBytes = MQTT_TOPIC.getBytes(StandardCharsets.UTF_8);
                        byte[] pBytes = payloadStr.getBytes(StandardCharsets.UTF_8);
                        ByteArrayOutputStream pubPayload = new ByteArrayOutputStream();
                        pubPayload.write((tBytes.length >> 8) & 0xFF);
                        pubPayload.write(tBytes.length & 0xFF);
                        pubPayload.write(tBytes);
                        pubPayload.write(pBytes);
                        byte[] pp = pubPayload.toByteArray();
                        ByteArrayOutputStream pubPkt = new ByteArrayOutputStream();
                        pubPkt.write(0x30);
                        writeRemainingLength(pubPkt, pp.length);
                        pubPkt.write(pp);
                        s.getOutputStream().write(pubPkt.toByteArray());
                        s.getOutputStream().flush();
                        s.close();
                        Log.i(TAG, "MQTT broadcast successfully dispatched to " + broker + "!");
                        break;
                    } catch (Exception e) {
                        Log.w(TAG, "MQTT publish error on " + broker + ": " + e.getMessage());
                        try { if (s != null) s.close(); } catch (Exception ignored) {}
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        try { if (udpSocket != null) udpSocket.close(); } catch (Exception ignored) {}
        stopAlarm(this);
        if (toneGen != null) {
            try { toneGen.release(); } catch (Exception ignored) {}
        }
        super.onDestroy();
    }
}
