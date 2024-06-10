package com.example.tw_okta_and;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class YourAccessibilityService extends AccessibilityService {
    private static final String TAG = "AccessibilityService";
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AccessibilityService created");

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent received: " + event.toString());
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("예, 본인입니다.");
                for (AccessibilityNodeInfo node : nodes) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(TAG, "Clicked on Okta notification button");
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        Log.d(TAG, "AccessibilityService destroyed");
    }
}
