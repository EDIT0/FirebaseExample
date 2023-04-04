package com.example.firebaseexample1.chat_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample1.databinding.ActivityChatLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatLoginActivity : AppCompatActivity() {

    private val TAG = ChatLoginActivity::class.java.simpleName

    private lateinit var binding : ActivityChatLoginBinding

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        buttonClickListener()


        // 로그인 인터페이스 리스너
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser
            user?.let {
                startActivity(Intent(binding.root.context, ChatHomeActivity::class.java))
                onBackPressed()
            }
        }
    }

    private fun buttonClickListener() {
        binding.btnLogin.setOnClickListener {
            requestLogin()
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(binding.root.context, ChatSignUpActivity::class.java))
        }
    }

    private fun requestLogin() {
        mFirebaseAuth.signInWithEmailAndPassword(binding.etId.text.toString(), binding.etPw.text.toString())
            .addOnCompleteListener {
                if(!it.isSuccessful) {
                    // 로그인 실패
                    Toast.makeText(binding.root.context, "로그인 실패", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "Success Login Current User: ${mFirebaseAuth.currentUser?.uid}")
                    startActivity(Intent(binding.root.context, ChatHomeActivity::class.java))
                    onBackPressed()
                }
            }
    }

//    override fun onStart() {
//        super.onStart()
//        mFirebaseAuth.addAuthStateListener(mAuthStateListener!!)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mFirebaseAuth.removeAuthStateListener(mAuthStateListener!!)
//    }
}