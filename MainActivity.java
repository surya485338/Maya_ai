package com.maya.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.provider.Settings;
import android.widget.*;
import android.graphics.Color;
import android.view.Gravity;
import java.util.*;

public class MainActivity extends Activity {
    private SharedPreferences prefs;
    private TextToSpeech tts;
    private TextView status;
    private SpeechRecognizer recognizer;
    private static final int AUDIO_REQUEST = 41;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("maya", MODE_PRIVATE);
        tts = new TextToSpeech(this, result -> {});
        buildUI();
    }

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 48, 28, 24);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.rgb(9,11,22));

        TextView title = new TextView(this);
        title.setText("MAYA AI");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Your voice-controlled phone assistant");
        sub.setTextColor(Color.LTGRAY);
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);
        root.addView(sub);

        status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setTextSize(20);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 40, 0, 24);
        root.addView(status);
        refreshStatus();

        Button pause = button("PAUSE MAYA");
        pause.setOnClickListener(v -> setPaused(true));
        root.addView(pause);

        Button resume = button("RESUME MAYA");
        resume.setOnClickListener(v -> setPaused(false));
        root.addView(resume);

        Button voice = button("TAP TO SPEAK");
        voice.setOnClickListener(v -> listen());
        root.addView(voice);

        Button accessibility = button("ENABLE PAYMENT REMINDER");
        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(accessibility);

        TextView note = new TextView(this);
        note.setText("\nPayment reminder: when Google Pay or PhonePe opens, MAYA can speak a reminder to pause. It never enters PIN/OTP or presses payment buttons. Enable the accessibility service in Android Settings.");
        note.setTextColor(Color.LTGRAY);
        note.setTextSize(13);
        root.addView(note);

        setContentView(root);
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(72,61,139)));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 12, 0, 4);
        b.setLayoutParams(p);
        return b;
    }

    private void setPaused(boolean paused) {
        prefs.edit().putBoolean("paused", paused).apply();
        refreshStatus();
        say(paused ? "MAYA paused. You can make your payment yourself." : "MAYA is active again.");
    }

    private void refreshStatus() {
        boolean paused = prefs.getBoolean("paused", false);
        status.setText(paused ? "● PAUSED" : "● ACTIVE");
        status.setTextColor(paused ? Color.rgb(255,190,70) : Color.rgb(100,230,170));
    }

    private void say(String s) {
        if (tts != null) tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, "maya_msg");
    }

    private void listen() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST);
            say("Voice command use karne ke liye microphone permission dein.");
            return;
        }
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            say("Speech recognition is not available on this phone.");
            return;
        }
        if (recognizer != null) recognizer.destroy();
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle p) {}
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() {}
            public void onError(int e) { say("I did not hear that. Please try again."); }
            public void onResults(Bundle b) {
                ArrayList<String> results = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (results == null || results.isEmpty()) return;
                String text = results.get(0).toLowerCase(Locale.ROOT);
                if (text.contains("band") || text.contains("pause") || text.contains("रुक")) setPaused(true);
                else if (text.contains("chalu") || text.contains("resume") || text.contains("शुरू")) setPaused(false);
                else if (text.contains("phonepe")) launch("com.phonepe.app");
                else if (text.contains("google pay") || text.contains("gpay")) launch("com.google.android.apps.nbu.paisa.user");
                else say("Abhi main pause, resume aur selected apps khol sakti hoon.");
            }
            public void onPartialResults(Bundle b) {}
            public void onEvent(int t, Bundle p) {}
        });
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "MAYA ko command dein");
        recognizer.startListening(i);
    }

    private void launch(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i != null) startActivity(i);
        else say("Yeh app phone mein install nahi hai.");
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUDIO_REQUEST && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            say("Microphone permission mil gayi. Ab Tap to Speak dabayein.");
        }
    }

    @Override protected void onDestroy() {
        if (recognizer != null) recognizer.destroy();
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}
