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

        if(intent.data != null) {
            Log.i("MYTAG", "not null ${intent.data}")
            handleDeepLink()
        } else {
            Log.i("MYTAG", "null")
        }


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

    private fun handleDeepLink() {
        Log.i("MYTAG", "Call handleDeepLink")
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this,
                OnSuccessListener { pendingDynamicLinkData ->
                    if (pendingDynamicLinkData == null) {
                        Log.d("MYTAG", "No have dynamic link")
                        return@OnSuccessListener
                    }
                    val deepLink = pendingDynamicLinkData.link
                    Log.d("MYTAG", "deepLink: $deepLink")
                    val segment = deepLink!!.lastPathSegment
                    Log.d("MYTAG", "segment: $segment")
                    when (segment) {
                        EVENT_FLAG -> {
                            val code = deepLink.getQueryParameter("code")
                            Toast.makeText(
                                binding.root.context,
                                "Event Code: ${code}",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, EventActivity::class.java))
                        }
                        AD1_FLAG -> {
                            val code = deepLink.getQueryParameter("code")
                            val publisher = deepLink.getQueryParameter("publisher")
                            Toast.makeText(
                                binding.root.context,
                                "Ad1 Code: ${code}\nPublisher: ${publisher}",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, AD1Activity::class.java))
                        }
                        AD2_FLAG -> {
                            val code = deepLink.getQueryParameter("code")
                            val publisher = deepLink.getQueryParameter("publisher")
                            Toast.makeText(
                                binding.root.context,
                                "Ad2 Code: ${code}\nPublisher: ${publisher}",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, AD2Activity::class.java))
                        }
                    }
                })
            .addOnFailureListener(
                this
            ) { e -> Log.w("MYTAG", "getDynamicLink:onFailure", e) }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.i("MYTAG", "onNewIntent()")
//        Log.i("MYTAG", "Intent Action: ${intent?.action}")
//        handleDeepLink()
//    }
}