package com.example.firebaseexample1.dynamic_links

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseexample1.databinding.ActivityDynamicLinksHomeBinding
import com.example.firebaseexample1.databinding.ActivityDynamicLinksValuesBinding

class DynamicLinksValuesActivity : AppCompatActivity() {

    private val TAG = DynamicLinksValuesActivity::class.java.simpleName
    private lateinit var binding : ActivityDynamicLinksValuesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicLinksValuesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvAnswer.text = "${intent.getIntExtra("ID", -1)} / ${intent.getStringExtra("PASS")}"
    }
}