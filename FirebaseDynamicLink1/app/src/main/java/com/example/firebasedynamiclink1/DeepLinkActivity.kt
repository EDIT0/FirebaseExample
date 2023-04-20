package com.example.firebasedynamiclink1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasedynamiclink1.databinding.ActivityDeepLinkBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class DeepLinkActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDeepLinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.data != null) {
            Log.i("MYTAG", "not null ${intent.data}")
            handleDeepLink()
        } else {
            Log.i("MYTAG", "null")
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
                    onBackPressed()
                })
            .addOnFailureListener(
                this
            ) { e -> Log.w("MYTAG", "getDynamicLink:onFailure", e) }
    }
}