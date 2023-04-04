package com.example.firebasecloudmessaging1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasecloudmessaging1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var activityMainBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        Log.i(TAG, "onCreate()")

        updateResult()

        activityMainBinding.btnAlarmMessage.setOnClickListener {
            startActivity(Intent(this, AlarmMessageActivity::class.java))
        }

        activityMainBinding.btnDataMessage.setOnClickListener {
            startActivity(Intent(this, DataMessageActivity::class.java))
        }
    }

    private fun updateResult(isNewIntent: Boolean = false) {
        if(isNewIntent) {
            Toast.makeText(this, "갱신 ${intent.getStringExtra("notificationType")?:"앱 런처"}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "실행 ${intent.getStringExtra("notificationType") ?: "앱 런처"}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i(TAG, "onNewIntent()")
        setIntent(intent)
        updateResult(true)
    }
}