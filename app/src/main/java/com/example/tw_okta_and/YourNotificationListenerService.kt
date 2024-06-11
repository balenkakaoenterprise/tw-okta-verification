package com.example.tw_okta_and

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle

class YourNotificationListenerService : NotificationListenerService() {

    private val TAG = "NotificationListener"
    private val CHANNEL_ID = "YOUR_CHANNEL_ID"
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        private const val EVENT_SERVICE_CREATED = "service_created"
        private const val EVENT_NOTIFICATION_POSTED = "notification_posted"
        private const val EVENT_OKTA_NOTIFICATION_DETECTED = "okta_notification_detected"
        private const val EVENT_CLICK_PERFORMED = "click_performed"
        private const val PARAM_DESCRIPTION = "description"
        const val ACTION_PERFORM_CLICK = "com.example.tw_okta_and.ACTION_PERFORM_CLICK"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotificationListenerService created")

        // FirebaseAnalytics 초기화
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        createNotificationChannel()

        // Foreground 서비스 설정
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service is running")
            .setContentText("Listening for notifications")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)

        // Firebase 로그 이벤트 수집
        logFirebaseEvent(EVENT_SERVICE_CREATED, "NotificationListenerService created")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        if (notification != null && notification.extras != null) {
            val notificationTitle = notification.extras.getString(Notification.EXTRA_TITLE)
            val notificationText = notification.extras.getString(Notification.EXTRA_TEXT)

            Log.d(TAG, "Notification posted: $notificationTitle : $notificationText")

            // Firebase 로그 이벤트 수집
            logFirebaseEvent(EVENT_NOTIFICATION_POSTED, "Notification posted: $notificationTitle : $notificationText")

            // Okta Verify 알림인지 확인
            if ("Okta Verify" == notificationTitle && notificationText?.contains("본인 확인") == true) {
                Log.d(TAG, "Okta Verify notification detected")

                // Firebase 로그 이벤트 수집
                logFirebaseEvent(EVENT_OKTA_NOTIFICATION_DETECTED, "Okta Verify notification detected")

                // "예, 본인입니다" 버튼을 자동으로 클릭
                performClickOnOktaNotification()
            }
        }
    }

    private fun performClickOnOktaNotification() {
        // AccessibilityService로 브로드캐스트 전송
        val intent = Intent(ACTION_PERFORM_CLICK)
        sendBroadcast(intent)

        // Firebase 로그 이벤트 수집
        logFirebaseEvent(EVENT_CLICK_PERFORMED, "Performed click on Okta notification")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // Firebase 이벤트 로그 메서드 추가
    private fun logFirebaseEvent(event: String, description: String) {
        val bundle = Bundle()
        bundle.putString(PARAM_DESCRIPTION, description)
        firebaseAnalytics.logEvent(event, bundle)
    }
}