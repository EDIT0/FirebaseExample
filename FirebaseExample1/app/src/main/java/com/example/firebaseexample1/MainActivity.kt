package com.example.firebaseexample1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseexample1.chat_app.ui.activity.ChatSplashActivity
import com.example.firebaseexample1.databinding.ActivityMainBinding
import com.example.firebaseexample1.dynamic_links.DynamicLinksHomeActivity
import com.example.firebaseexample1.firestore.FireStoreHomeActivity
import com.example.firebaseexample1.realtime_db.RealtimeHomeActivity

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnFireStore.setOnClickListener {
            startActivity(Intent(binding.root.context, FireStoreHomeActivity::class.java))
        }

        binding.btnRealtimeDB.setOnClickListener {
            startActivity(Intent(binding.root.context, RealtimeHomeActivity::class.java))
        }

        binding.btnChatApp.setOnClickListener {
            startActivity(Intent(binding.root.context, ChatSplashActivity::class.java))
        }

        binding.btnDeepLinks.setOnClickListener {
            startActivity(Intent(binding.root.context, DynamicLinksHomeActivity::class.java))
        }

    }
}