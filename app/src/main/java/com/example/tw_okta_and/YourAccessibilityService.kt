package com.example.tw_okta_and;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.firebase.analytics.FirebaseAnalytics;
import android.os.Bundle;
import java.util.List;

public class YourAccessibilityService extends AccessibilityService {
    private static final String TAG = "AccessibilityService";
    private PowerManager.WakeLock wakeLock;
    private FirebaseAnalytics firebaseAnalytics; // FirebaseAnalytics 변수 추가

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AccessibilityService created");

        // FirebaseAnalytics 초기화
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);

        // Firebase 로그 이벤트 수집
        logFirebaseEvent("service_created", "AccessibilityService created");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent received: " + event.toString());

        // Firebase 로그 이벤트 수집
        logFirebaseEvent("accessibility_event", "Event received: " + event.toString());

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("예, 본인입니다.");
                for (AccessibilityNodeInfo node : nodes) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(TAG, "Clicked on Okta notification button");

                    // Firebase 로그 이벤트 수집
                    logFirebaseEvent("button_clicked", "Clicked on Okta notification button");
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted");

        // Firebase 로그 이벤트 수집
        logFirebaseEvent("service_interrupted", "AccessibilityService interrupted");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        Log.d(TAG, "AccessibilityService destroyed");

        // Firebase 로그 이벤트 수집
        logFirebaseEvent("service_destroyed", "AccessibilityService destroyed");
    }

    // Firebase 이벤트 로그 메서드 추가
    private void logFirebaseEvent(String event, String description) {
        Bundle bundle = new Bundle();
        bundle.putString("description", description);
        firebaseAnalytics.logEvent(event, bundle);
    }
}
