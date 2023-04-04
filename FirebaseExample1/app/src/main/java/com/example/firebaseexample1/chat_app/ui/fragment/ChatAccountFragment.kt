package com.example.firebaseexample1.chat_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.firebaseexample1.chat_app.ui.activity.ChatLoginActivity
import com.example.firebaseexample1.databinding.FragmentChatAccountBinding
import com.google.firebase.auth.FirebaseAuth

class ChatAccountFragment : Fragment() {

    private var _binding: FragmentChatAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
//        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
            startActivity(Intent(requireContext(), ChatLoginActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}