package com.example.tw_okta_and

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.PowerManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle

class YourAccessibilityService : AccessibilityService() {
    private val TAG = "AccessibilityService"
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AccessibilityService created")

        // FirebaseAnalytics 초기화
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // WakeLock 설정
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)

        // Firebase 이벤트 로깅
        logFirebaseEvent("service_created", "AccessibilityService created")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent received: ${event.toString()}")

        // Firebase 이벤트 로깅
        logFirebaseEvent("accessibility_event", "Event received: ${event.toString()}")

        // Okta Verify 알림 처리
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            val rootNode = rootInActiveWindow
            rootNode?.let {
                handleOktaNotification(rootNode)
            }
        }
    }

    private fun handleOktaNotification(rootNode: AccessibilityNodeInfo) {
        // '예, 본인입니다' 버튼 찾기
        val confirmNodes = rootNode.findAccessibilityNodeInfosByText("예, 본인입니다.")
        if (confirmNodes.isEmpty()) {
            Log.d(TAG, "No nodes found with text '예, 본인입니다.'")
        } else {
            for (confirmNode in confirmNodes) {
                val confirmClicked = confirmNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d(TAG, "Clicked on Okta notification button: $confirmClicked")

                // Firebase 이벤트 로깅
                logFirebaseEvent("button_clicked", "Clicked on Okta notification button: $confirmClicked")
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted")

        // Firebase 이벤트 로깅
        logFirebaseEvent("service_interrupted", "AccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        Log.d(TAG, "AccessibilityService destroyed")

        // Firebase 이벤트 로깅
        logFirebaseEvent("service_destroyed", "AccessibilityService destroyed")
    }

    // Firebase 이벤트 로깅 메소드
    private fun logFirebaseEvent(event: String, description: String) {
        val bundle = Bundle()
        bundle.putString("description", description)
        firebaseAnalytics.logEvent(event, bundle)
    }
}
