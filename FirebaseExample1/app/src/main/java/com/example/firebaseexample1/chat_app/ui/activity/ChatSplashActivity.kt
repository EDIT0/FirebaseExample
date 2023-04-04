package com.example.firebaseexample1.chat_app.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebaseexample1.databinding.ActivityChatSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatSplashActivity : AppCompatActivity() {

    private val TAG = ChatSplashActivity::class.java.simpleName

    private lateinit var binding: ActivityChatSplashBinding

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatSplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 로그인 인터페이스 리스너
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser
            if(user == null) {
                startActivity(Intent(binding.root.context, ChatLoginActivity::class.java))
                onBackPressed()
            }
            user?.let {
                Log.i(TAG, "Success Login Current User: ${mFirebaseAuth.currentUser?.uid}")
                startActivity(Intent(binding.root.context, ChatHomeActivity::class.java))
                onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener!!)
    }
}