package com.example.firebasecloudmessaging1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasecloudmessaging1.databinding.ActivityAlarmMessageBinding
import com.example.firebasecloudmessaging1.databinding.ActivityDataMessageBinding
import com.google.firebase.messaging.FirebaseMessaging

class DataMessageActivity : AppCompatActivity() {

    private val TAG = DataMessageActivity::class.java.simpleName

    private lateinit var activityDataMessageBinding: ActivityDataMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDataMessageBinding = ActivityDataMessageBinding.inflate(layoutInflater)
        setContentView(activityDataMessageBinding.root)

        getCurrentToken()

        activityDataMessageBinding.btnSendAlarmMessage.setOnClickListener {
            FCMSender(this.applicationContext)
                .sendDataMessage(
                "input your token",
                "EXPANDABLE",
                "Data Title",
                "Data Message\nData Message\nData Message\n" +
                        "Data Message\nData Message\n" +
                        "Data Message\nData Message\n" +
                        "Data Message\nData Message\n" +
                        "Data Message\nData Message\n" +
                        "Data Message\nData Message\n" +
                        "Data Message\n"
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

                activityDataMessageBinding.tvToken.text = token
                Log.i(TAG, "Token: ${token}")
            }
    }
}