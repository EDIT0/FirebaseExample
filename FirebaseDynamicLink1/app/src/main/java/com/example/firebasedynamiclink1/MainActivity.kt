package com.example.firebasedynamiclink1

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasedynamiclink1.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /**
         * 현재 액티비티가 Root인지 아닌지 확인
         * Root가 아니면 메인 재실행 -> 공유한 페이지가 쌓이지 않는다.
         * Root가 아니면 MainActivity가 2개인 상황이 만들어지므로 1개 finish() -> 공유한 페이지가 쌓인다.
         * */
        Log.i("MYTAG", "isTaskRoot: ${isTaskRoot}")
        if(!isTaskRoot) {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
            finish()
        }

        binding.btnEvent.setOnClickListener {
            startActivity(Intent(this, EventActivity::class.java))
        }

        binding.btnAd1.setOnClickListener {
            startActivity(Intent(this, AD1Activity::class.java))
        }

        binding.btnAd2.setOnClickListener {
            startActivity(Intent(this, AD2Activity::class.java))
        }

    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.i("MYTAG", "onNewIntent()")
//        Log.i("MYTAG", "Intent Action: ${intent?.action}")
//        handleDeepLink()
//    }
}