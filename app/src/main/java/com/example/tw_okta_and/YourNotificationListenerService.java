package com.example.tw_okta_and;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class YourNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "NotificationListener";
    private static final String CHANNEL_ID = "YOUR_CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotificationListenerService created");

        createNotificationChannel();

        // Foreground service 설정
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service is running")
                .setContentText("Listening for notifications")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        if (notification != null && notification.extras != null) {
            String notificationTitle = notification.extras.getString(Notification.EXTRA_TITLE);
            String notificationText = notification.extras.getString(Notification.EXTRA_TEXT);

            Log.d(TAG, "Notification posted: " + notificationTitle + " : " + notificationText);

            // Okta Verify 알림인지 확인
            if ("Okta Verify".equals(notificationTitle) && notificationText.contains("본인 확인")) {
                Log.d(TAG, "Okta Verify notification detected");
                // "예, 본인입니다" 버튼을 자동으로 클릭
                performClickOnOktaNotification();
            }
        }
    }

    private void performClickOnOktaNotification() {
        // 여기에 자동 클릭 로직을 구현합니다.
        // AccessibilityService를 사용하여 화면의 특정 버튼을 클릭할 수 있습니다.
        Log.d(TAG, "Performing click on Okta notification");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
