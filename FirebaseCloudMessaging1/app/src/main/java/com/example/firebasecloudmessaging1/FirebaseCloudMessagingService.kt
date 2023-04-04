package com.example.firebasecloudmessaging1

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseCloudMessagingService : FirebaseMessagingService() {

    private val TAG = FirebaseCloudMessagingService::class.java.simpleName

    /**
     * 새 토큰이 생성 될 때 마다 onNewToken() 콜백 호출
     * */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.i(TAG, "Execute onNewToken()")

//        sendRegistrationToServer(token)
    }

    /**
     * 메시지를 수신 할 때 마다 onMessageReceived() 호출
     * */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.data.isEmpty()) {
            // Notification 알림
            Log.i(TAG, "Execute onMessageReceived() notification \n${message.notification?.title}")

            createNotificationChannel() // 채널 만들기

            val title = message.notification?.title
            val message = message.notification?.body


            NotificationManagerCompat.from(this)
                .notify(1, createNotification(NotificationType.valueOf("NORMAL"), title, message))
        } else {
            // Data 알림
            Log.i(TAG, "Execute onMessageReceived() data \n${message.data}")

            createNotificationChannel() // 채널 만들기

            val type = message.data.get("type")?.let {
                NotificationType.valueOf(it)
            }
            type ?: return

            val title = message.data.get("title")
            val message = message.data.get("message")


            NotificationManagerCompat.from(this)
                .notify(2, createNotification(type, title, message))
        }
    }

    /**
     * 채널 만들기 (안드로이드 8.0 이상)
     * */
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            channel.description = CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    /**
     * 알림 만들기
     * */
    private fun createNotification(type: NotificationType, title: String?, message: String?): Notification {
        Log.i("MainActivity", "${type.id}")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("notificationType", "${type.title} 타입")
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        /**
         * PendingIntent.getActivity의 requestCode의 숫자를 랜덤으로 하니까 onNewIntent를 탄다.
         * */
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 안드로이드 8.0 이상에서는 채널을 만들 때 중요도를 셋팅하지만 그 이하 버전에서는 직접 넣어줘야한다.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        when(type) {
            NotificationType.NORMAL -> {
                Unit
            }
            NotificationType.EXPANDABLE -> {
                notificationBuilder
                    .setStyle(NotificationCompat.BigTextStyle())
            }
            NotificationType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(packageName, R.layout.view_custom_notification).apply {
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )
            }
        }

        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = "Emoji Party를 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }

    enum class NotificationType(
        val title: String,
        val id: Int
    ) {
        NORMAL("일반 알림", 123450),
        EXPANDABLE("확장형 알림", 123451),
        CUSTOM("커스텀 알림",123452)
    }
}