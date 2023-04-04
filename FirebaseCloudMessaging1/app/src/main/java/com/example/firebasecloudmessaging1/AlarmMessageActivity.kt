package com.example.firebasecloudmessaging1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasecloudmessaging1.databinding.ActivityAlarmMessageBinding
import com.google.firebase.messaging.FirebaseMessaging

class AlarmMessageActivity : AppCompatActivity() {

    private val TAG = AlarmMessageActivity::class.java.simpleName

    private lateinit var activityAlarmMessageBinding: ActivityAlarmMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAlarmMessageBinding = ActivityAlarmMessageBinding.inflate(layoutInflater)
        setContentView(activityAlarmMessageBinding.root)

        getCurrentToken()

        activityAlarmMessageBinding.btnSendAlarmMessage.setOnClickListener {
            FCMSender(this)
                .sendAlarmMessage(
                "input your token",
                "EXPANDABLE",
                "Alarm Title",
                "Alarm Message\nAlarm Message\nAlarm Message"
            )
        }
    }

    private fun getCurrentToken() {
        /**
         * 현재 토큰 가져오기
         * */
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", it.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = it.result

                activityAlarmMessageBinding.tvToken.text = token
                Log.i(TAG, "Token: ${token}")
            }
    }
}