package com.gun.test

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.text.SimpleDateFormat
import java.util.Date


class MyNotificationService : NotificationListenerService() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val r = super.onStartCommand(intent, flags, startId)
        Log.e(TAG, "MyNotificationService 서비스 시작됨")
        return r
    }

    // 알림이 표시되면 내용을 읽어온다
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // 패키지 체크
        val packageName = sbn.packageName
        if (packageName != TARGET_APP_PACKAGE) return

        // 알림 정보 가져오기
        val extras = sbn.notification.extras
        val name = extras.getString(Notification.EXTRA_TITLE)
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)
        var room = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)
        if (name == null || text == null) return
        if (room == null) room = "(개인 채팅)" // room은 그룹/오픈채팅만 있음

        // 토스트 띄우기


        // 메인 엑티비티로 전달
        @SuppressLint("SimpleDateFormat") val fullDateFormat =
            SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a")
        val intent = Intent(ACTION_NOTIFICATION_BROADCAST)
        intent.putExtra(EXTRA_TIME, fullDateFormat.format(Date()).toString())
        intent.putExtra(EXTRA_NAME, name)
        intent.putExtra(EXTRA_TEXT, text.toString())
        intent.putExtra(EXTRA_ROOM, room.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Toast.makeText(this, "알림 제거됨", Toast.LENGTH_SHORT).show();
    }

    companion object {
        private const val TAG = "NotificationService"
        const val ACTION_NOTIFICATION_BROADCAST = "MyNotificationService_LocalBroadcast"
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_TEXT = "extra_text"
        const val EXTRA_ROOM = "extra_room"
        const val TARGET_APP_PACKAGE = "com.kakao.talk" // 알림을 읽어올 앱
    }
}