package com.nerkavach.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class EmergencyAlertActivity extends Activity {
    private static final String ACTION_ALARM_STOPPED = "com.nerkavach.app.ACTION_ALARM_STOPPED";

    private BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Turn screen on and show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );

        String title = getIntent().getStringExtra("title");
        String body = getIntent().getStringExtra("body");

        if (title == null || title.trim().isEmpty()) {
            title = "🚨 CRITICAL LANDSLIDE EMERGENCY ALERT";
        }
        if (body == null || body.trim().isEmpty()) {
            body = "Critical slope saturation detected in Arunachal corridor. High debris flow probability. Evacuate to high ground immediately!";
        }

        // Register receiver to auto-close if dismissed from notification bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stopReceiver, new IntentFilter(ACTION_ALARM_STOPPED), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(stopReceiver, new IntentFilter(ACTION_ALARM_STOPPED));
        }

        // Root View: Full-screen translucent dark backdrop
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#E6050510")); // 90% opacity deep dark
        root.setPadding(dpToPx(16), dpToPx(32), dpToPx(16), dpToPx(32));

        // ScrollView to prevent overflow on small screens
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Central Emergency Alert Card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dpToPx(20), dpToPx(24), dpToPx(20), dpToPx(24));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.parseColor("#180E10")); // Deep emergency crimson black
        cardBg.setCornerRadius(dpToPx(20));
        cardBg.setStroke(dpToPx(3), Color.parseColor("#EF4444")); // Glowing red border
        card.setBackground(cardBg);

        // 1. Hazard Badge Header
        TextView badge = new TextView(this);
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
        TextView subtitle = new TextView(this);
        subtitle.setText("ARUNACHAL CORRIDOR EARLY WARNING");
        subtitle.setTextColor(Color.parseColor("#FCA5A5"));
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        subtitle.setTypeface(Typeface.DEFAULT_BOLD);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dpToPx(8), 0, dpToPx(12));
        card.addView(subtitle);

        // 2. Alert Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setPadding(0, 0, 0, dpToPx(12));
        card.addView(tvTitle);

        // 3. Advisory Message Body
        TextView tvBody = new TextView(this);
        tvBody.setText(body);
        tvBody.setTextColor(Color.parseColor("#F1F5F9"));
        tvBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvBody.setGravity(Gravity.CENTER);
        tvBody.setLineSpacing(dpToPx(4), 1.1f);
        tvBody.setPadding(dpToPx(8), 0, dpToPx(8), dpToPx(16));
        card.addView(tvBody);

        // 4. Instructions Box
        LinearLayout instBox = new LinearLayout(this);
        instBox.setOrientation(LinearLayout.VERTICAL);
        instBox.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        GradientDrawable instBg = new GradientDrawable();
        instBg.setColor(Color.parseColor("#291216"));
        instBg.setCornerRadius(dpToPx(12));
        instBg.setStroke(dpToPx(1), Color.parseColor("#F59E0B"));
        instBox.setBackground(instBg);

        TextView tvInstTitle = new TextView(this);
        tvInstTitle.setText("⚠️ IMMEDIATE SAFETY DIRECTIVES:");
        tvInstTitle.setTextColor(Color.parseColor("#FBBF24"));
        tvInstTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvInstTitle.setTypeface(Typeface.DEFAULT_BOLD);
        instBox.addView(tvInstTitle);

        TextView tvInstText = new TextView(this);
        tvInstText.setText("• Evacuate uphill away from mudflow channels immediately.\n• Avoid active road cuts & cracked retaining walls.\n• Follow offline safe GPS paths to community shelters.");
        tvInstText.setTextColor(Color.parseColor("#E2E8F0"));
        tvInstText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvInstText.setLineSpacing(dpToPx(2), 1.1f);
        tvInstText.setPadding(0, dpToPx(6), 0, 0);
        instBox.addView(tvInstText);

        card.addView(instBox);

        // 5. Button 1: Prominent STOP SIREN & DISMISS (Red)
        Button btnStop = new Button(this);
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
                // Instantly silence siren, vibrator, cancel notifications, and close
                AlertReceiverService.stopAlarm(EmergencyAlertActivity.this);
                finish();
            }
        });
        card.addView(btnStop);

        // 6. Button 2: VIEW EVACUATION ROUTE (Green)
        Button btnRoute = new Button(this);
        btnRoute.setText("🗺️ VIEW SAFE EVACUATION ROUTE");
        btnRoute.setTextColor(Color.WHITE);
        btnRoute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        btnRoute.setTypeface(Typeface.DEFAULT_BOLD);
        btnRoute.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        GradientDrawable btnRouteBg = new GradientDrawable();
        btnRouteBg.setColor(Color.parseColor("#059669")); // Emerald green
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
                // Silence alarm and open main app
                AlertReceiverService.stopAlarm(EmergencyAlertActivity.this);
                Intent mainIntent = new Intent(EmergencyAlertActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
            }
        });
        card.addView(btnRoute);

        scroll.addView(card);
        root.addView(scroll);
        setContentView(root);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onBackPressed() {
        AlertReceiverService.stopAlarm(this);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(stopReceiver);
        } catch (Exception ignored) {}
        AlertReceiverService.stopAlarm(this);
        super.onDestroy();
    }
}
