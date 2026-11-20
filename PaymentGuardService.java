package com.maya.assistant;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class PaymentGuardService extends AccessibilityService {
    private TextToSpeech tts;
    private long lastReminder = 0;

    @Override public void onServiceConnected() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) tts.setLanguage(new Locale("hi", "IN"));
        });
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;
        String pkg = event.getPackageName().toString();
        boolean paymentApp = pkg.equals("com.phonepe.app") ||
            pkg.equals("com.google.android.apps.nbu.paisa.user");
        SharedPreferences prefs = getSharedPreferences("maya", MODE_PRIVATE);
        boolean paused = prefs.getBoolean("paused", false);
        long now = System.currentTimeMillis();
        if (paymentApp && !paused && now - lastReminder > 15000) {
            lastReminder = now;
            if (tts != null) tts.speak(
                "Aap payment karne ja rahe hain. Kripya pehle MAYA AI ko pause karein. Payment aap khud karein.",
                TextToSpeech.QUEUE_FLUSH, null, "payment_reminder");
        }
    }

    @Override public void onInterrupt() {}
    @Override public void onDestroy() {
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}
