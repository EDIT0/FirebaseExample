package com.example.firebaseexample1.chat_app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample1.R
import com.example.firebaseexample1.chat_app.ui.fragment.ChatAccountFragment
import com.example.firebaseexample1.chat_app.ui.fragment.ChatChatFragment
import com.example.firebaseexample1.chat_app.ui.fragment.ChatPeopleFragment
import com.example.firebaseexample1.databinding.ActivityChatHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatHomeActivity : AppCompatActivity() {

    private val TAG = ChatHomeActivity::class.java.simpleName

    private lateinit var binding : ActivityChatHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, ChatPeopleFragment())
            .commitAllowingStateLoss()


        binding.bnvHome.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.actionPeople -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ChatPeopleFragment())
                        .commitAllowingStateLoss()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.actionChat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ChatChatFragment())
                        .commitAllowingStateLoss()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.actionAccount -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ChatAccountFragment())
                        .commitAllowingStateLoss()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}