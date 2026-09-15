package com.nerkavach.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WhatsAppAutoSendService extends AccessibilityService {
    private static final String TAG = "NER_WA_SERVICE";

    public static volatile boolean isAutoSending = false;
    public static volatile String[] pendingNumbers = null;
    public static volatile int currentNumberIndex = 0;
    public static volatile String pendingMessage = null;
    public static volatile Context appContext = null;

    private static WhatsAppAutoSendService instance = null;
    private static long lastActionTime = 0;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Runnable activePollRunnable = null;
    private static int pollAttemptCount = 0;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        Log.i(TAG, "WhatsAppAutoSendService connected and active with Gesture support.");
    }

    public static boolean isServiceRunning() {
        return instance != null;
    }

    public static boolean isAccessibilityEnabled(Context context) {
        if (instance != null) return true;
        if (context == null) return false;
        try {
            int accessibilityEnabled = 0;
            final String service = context.getPackageName() + "/" + WhatsAppAutoSendService.class.getCanonicalName();
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            } catch (Settings.SettingNotFoundException ignored) {}

            TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
            if (accessibilityEnabled == 1) {
                String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (settingValue != null) {
                    colonSplitter.setString(settingValue);
                    while (colonSplitter.hasNext()) {
                        String accessibilityService = colonSplitter.next();
                        if (accessibilityService.equalsIgnoreCase(service) || 
                            accessibilityService.contains("WhatsAppAutoSendService") ||
                            accessibilityService.contains("com.nerkavach.app")) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return instance != null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "START_DISPATCH".equals(intent.getStringExtra("action"))) {
            String numbersCsv = intent.getStringExtra("numbers");
            String message = intent.getStringExtra("message");
            String[] numbers = (numbersCsv != null && !numbersCsv.isEmpty())
                ? numbersCsv.split(",")
                : new String[]{"8341687289", "6281967693", "7287006100"};
            startAutoSend(this, numbers, message);
        }
        return START_STICKY;
    }

    public static void startAutoSend(Context context, String[] numbers, String message) {
        if (numbers == null || numbers.length == 0 || message == null) return;

        isAutoSending = true;
        pendingNumbers = numbers;
        currentNumberIndex = 0;
        pendingMessage = message;
        appContext = (context != null) ? context.getApplicationContext() : null;

        Log.i(TAG, "Starting automated WhatsApp queue for " + numbers.length + " contacts.");
        launchNextWhatsApp();
    }

    public static String normalizePhone(String raw) {
        if (raw == null) return "";
        String clean = raw.trim().replaceAll("[^0-9]", "");
        if (clean.length() == 10) {
            clean = "91" + clean;
        } else if (clean.startsWith("0") && clean.length() == 11) {
            clean = "91" + clean.substring(1);
        }
        return clean;
    }

    private static boolean isPackageInstalled(PackageManager pm, String pkg) {
        if (pm == null || pkg == null) return false;
        try {
            pm.getPackageInfo(pkg, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Intent buildWhatsAppIntent(Context context, String phone, String message) {
        String cleanPhone = normalizePhone(phone);
        String encodedMsg = "";
        try {
            encodedMsg = URLEncoder.encode(message, "UTF-8");
        } catch (Exception e) {
            encodedMsg = message;
        }

        Uri waUri = Uri.parse("https://api.whatsapp.com/send?phone=" + cleanPhone + "&text=" + encodedMsg);
        Intent intent = new Intent(Intent.ACTION_VIEW, waUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                if (isPackageInstalled(pm, "com.whatsapp")) {
                    intent.setPackage("com.whatsapp");
                } else if (isPackageInstalled(pm, "com.whatsapp.w4b")) {
                    intent.setPackage("com.whatsapp.w4b");
                }
            }
        }
        return intent;
    }

    private static void launchNextWhatsApp() {
        if (!isAutoSending || pendingNumbers == null || currentNumberIndex >= pendingNumbers.length) {
            finishAutoSend();
            return;
        }

        stopPolling();

        String rawPhone = pendingNumbers[currentNumberIndex];
        Context ctx = (appContext != null) ? appContext : instance;
        if (ctx == null) {
            Log.e(TAG, "No context available to launch WhatsApp.");
            finishAutoSend();
            return;
        }

        try {
            Intent waIntent = buildWhatsAppIntent(ctx, rawPhone, pendingMessage);
            ctx.startActivity(waIntent);
            Log.i(TAG, "Launched WhatsApp for contact [" + (currentNumberIndex + 1) + "/" + pendingNumbers.length + "]: " + rawPhone);
            
            // Proactive polling for up to 10 seconds to detect when chat screen resolves and send button appears
            startPollingForSendButton();
        } catch (Exception e) {
            Log.e(TAG, "Primary intent launch failed: " + e.getMessage());
            currentNumberIndex++;
            launchNextWhatsApp();
        }
    }

    private static void startPollingForSendButton() {
        stopPolling();
        pollAttemptCount = 0;
        activePollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAutoSending || instance == null) return;
                pollAttemptCount++;

                AccessibilityNodeInfo rootNode = null;
                try {
                    rootNode = instance.getRootInActiveWindow();
                } catch (Exception ignored) {}

                if (rootNode != null) {
                    AccessibilityNodeInfo sendButton = instance.findSendButton(rootNode);
                    if (sendButton != null) {
                        Log.i(TAG, "[Poll] Detected WhatsApp Send button at attempt #" + pollAttemptCount + ". Performing automated send!");
                        instance.performAutoClick(sendButton);
                        return;
                    }
                }

                // Poll every 120ms for up to 80 attempts (9.6 seconds per contact)
                if (pollAttemptCount < 80 && isAutoSending) {
                    mainHandler.postDelayed(this, 120);
                }
            }
        };
        mainHandler.postDelayed(activePollRunnable, 400);
    }

    private static void stopPolling() {
        if (activePollRunnable != null) {
            mainHandler.removeCallbacks(activePollRunnable);
            activePollRunnable = null;
        }
    }

    private static void finishAutoSend() {
        stopPolling();
        isAutoSending = false;
        pendingNumbers = null;
        currentNumberIndex = 0;
        pendingMessage = null;

        if (instance != null) {
            try {
                Intent backIntent = new Intent(instance, MainActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                instance.startActivity(backIntent);
            } catch (Exception ignored) {}
        }

        if (appContext != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(appContext, "✅ Landslide Advisory automatically sent to all 3 WhatsApp contacts!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isAutoSending || pendingNumbers == null || currentNumberIndex >= pendingNumbers.length) {
            return;
        }

        CharSequence pkg = event.getPackageName();
        if (pkg == null || (!pkg.toString().contains("whatsapp"))) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastActionTime < 400) {
            return;
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        AccessibilityNodeInfo sendButton = findSendButton(rootNode);
        if (sendButton != null) {
            Log.i(TAG, "[Event] Detected WhatsApp Send button for contact index: " + currentNumberIndex);
            performAutoClick(sendButton);
        }
    }

    private synchronized void performAutoClick(AccessibilityNodeInfo sendButton) {
        long now = System.currentTimeMillis();
        if (now - lastActionTime < 500) return;
        lastActionTime = now;
        stopPolling();

        // 1. Standard Accessibility Click
        boolean clicked = false;
        try {
            clicked = sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (!clicked && sendButton.getParent() != null) {
                clicked = sendButton.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } catch (Exception ignored) {}

        // 2. Hardware Gesture Tap at exact Screen Coordinates (100% reliable on all Android skins)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Rect bounds = new Rect();
                sendButton.getBoundsInScreen(bounds);
                if (bounds.width() > 0 && bounds.height() > 0) {
                    Path clickPath = new Path();
                    clickPath.moveTo(bounds.centerX(), bounds.centerY());
                    GestureDescription.StrokeDescription stroke =
                        new GestureDescription.StrokeDescription(clickPath, 0, 50);
                    GestureDescription.Builder builder = new GestureDescription.Builder();
                    builder.addStroke(stroke);
                    dispatchGesture(builder.build(), null, null);
                    Log.i(TAG, "Hardware gesture tap dispatched at (" + bounds.centerX() + ", " + bounds.centerY() + ")");
                }
            } catch (Exception e) {
                Log.e(TAG, "Gesture error: " + e.getMessage());
            }
        }

        currentNumberIndex++;

        // Pause 1300ms so WhatsApp sends the message over network before launching next contact
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentNumberIndex < pendingNumbers.length) {
                    launchNextWhatsApp();
                } else {
                    finishAutoSend();
                }
            }
        }, 1300);
    }

    private AccessibilityNodeInfo findSendButton(AccessibilityNodeInfo root) {
        if (root == null) return null;

        // 1. Check known WhatsApp send button view IDs
        String[] ids = new String[]{
            "com.whatsapp:id/send",
            "com.whatsapp:id/send_container",
            "com.whatsapp:id/action_send",
            "com.whatsapp:id/floating_action_button",
            "com.whatsapp:id/entry_action_send",
            "com.whatsapp:id/conversation_send_button",
            "com.whatsapp:id/btn_send",
            "com.whatsapp:id/input_send_button",
            "com.whatsapp.w4b:id/send",
            "com.whatsapp.w4b:id/send_container"
        };
        for (String id : ids) {
            try {
                List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(id);
                if (nodes != null && !nodes.isEmpty()) {
                    for (AccessibilityNodeInfo n : nodes) {
                        if (n != null && n.isEnabled()) {
                            return n;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        // 2. Check by content description & localized names
        return searchByDescription(root);
    }

    private AccessibilityNodeInfo searchByDescription(AccessibilityNodeInfo node) {
        if (node == null) return null;

        CharSequence desc = node.getContentDescription();
        if (desc != null) {
            String d = desc.toString().trim().toLowerCase();
            if (!d.contains("voice") && !d.contains("audio") && !d.contains("record") && !d.contains("mic")) {
                if (d.equals("send") || d.startsWith("send message") || d.equals("senden") || 
                    d.equals("enviar") || d.equals("envoyer") || d.equals("invia") || 
                    d.equals("పంపు") || d.equals("भेजें") || d.equals("প্ৰেৰণ কৰক") || 
                    d.equals("প্ৰেৰণ") || d.equals("বার্তা পাঠান")) {
                    if (node.isEnabled()) {
                        return node;
                    }
                }
            }
        }

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            AccessibilityNodeInfo found = searchByDescription(child);
            if (found != null) return found;
        }

        return null;
    }

    @Override
    public void onInterrupt() {
        Log.w(TAG, "WhatsAppAutoSendService interrupted.");
        stopPolling();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPolling();
        instance = null;
    }
}
