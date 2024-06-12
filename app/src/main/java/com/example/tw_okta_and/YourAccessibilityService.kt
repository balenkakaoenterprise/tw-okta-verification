package com.example.tw_okta_and

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class YourAccessibilityService : AccessibilityService() {
    private val TAG = "AccessibilityService"
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var receiver: BroadcastReceiver

    companion object {
        private const val WAKE_LOCK_TIMEOUT = 5 * 60 * 1000L // 5 minutes
        private const val EVENT_SERVICE_CREATED = "service_created"
        private const val EVENT_ACCESSIBILITY_EVENT = "accessibility_event"
        private const val EVENT_BUTTON_CLICKED = "button_clicked"
        private const val EVENT_SERVICE_INTERRUPTED = "service_interrupted"
        private const val EVENT_SERVICE_DESTROYED = "service_destroyed"
        private const val PARAM_DESCRIPTION = "description"
        fun startOktaAuthentication(context: Context) {
            val intent = Intent(context, YourAccessibilityService::class.java)
            intent.action = "START_OKTA_AUTHENTICATION"
            context.startService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START_OKTA_AUTHENTICATION") {
            // Okta 인증 화면 감지 및 자동 승인 로직 추가
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                val rootNode = rootInActiveWindow
                rootNode?.let {
                    OktaAuthenticator.handleOktaNotification(it, this)
                }
            }, 2000) // 2초 후에 Okta 인증 화면 검사 및 자동 승인 시도
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AccessibilityService created")

        // FirebaseAnalytics 초기화
        firebaseAnalytics = Firebase.analytics

        // WakeLock 설정
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")
        wakeLock.acquire(WAKE_LOCK_TIMEOUT)

        // Firebase 이벤트 로깅
        logFirebaseEvent(EVENT_SERVICE_CREATED, "AccessibilityService created")

        // 브로드캐스트 수신기 등록
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == YourNotificationListenerService.ACTION_PERFORM_CLICK) {
                    context?.let {
                        // Okta Verify 알림 버튼 클릭 수행
                        val rootNode = rootInActiveWindow
                        rootNode?.let { node ->
                            OktaAuthenticator.handleOktaNotification(node, it)
                        }
                    }
                }
            }
        }
        val filter = IntentFilter(YourNotificationListenerService.ACTION_PERFORM_CLICK)
        registerReceiver(receiver, filter)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent received: ${event.toString()}")

        // Firebase 이벤트 로깅
        logFirebaseEvent(EVENT_ACCESSIBILITY_EVENT, "Event received: ${event.toString()}")

        val rootNode = rootInActiveWindow
        rootNode?.let {
            OktaAuthenticator.handleOktaNotification(rootNode, this)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted")

        // Firebase 이벤트 로깅
        logFirebaseEvent(EVENT_SERVICE_INTERRUPTED, "AccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        Log.d(TAG, "AccessibilityService destroyed")

        // 브로드캐스트 수신기 해제
        unregisterReceiver(receiver)

        // Firebase 이벤트 로깅
        logFirebaseEvent(EVENT_SERVICE_DESTROYED, "AccessibilityService destroyed")
    }

    // Firebase 이벤트 로깅 메소드
    private fun logFirebaseEvent(event: String, description: String) {
        val bundle = Bundle().apply {
            putString(PARAM_DESCRIPTION, description)
        }
        firebaseAnalytics.logEvent(event, bundle)
    }
}

object OktaAuthenticator {
    fun handleOktaNotification(rootNode: AccessibilityNodeInfo, context: Context) {
        // '예, 본인입니다' 버튼 찾기
        val confirmNodes = rootNode.findAccessibilityNodeInfosByViewId("com.okta.android.auth:id/approve_button")
        if (confirmNodes.isEmpty()) {
            Log.d("OktaAuthenticator", "No nodes found with ID 'com.okta.android.auth:id/approve_button'")
        } else {
            for (confirmNode in confirmNodes) {
                val confirmClicked = confirmNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("OktaAuthenticator", "Clicked on Okta notification button: $confirmClicked")

                // Firebase 이벤트 로깅
                val bundle = Bundle().apply {
                    putString("description", "Clicked on Okta notification button: $confirmClicked")
                }
                Firebase.analytics.logEvent("button_clicked", bundle)
            }
        }
    }
}


