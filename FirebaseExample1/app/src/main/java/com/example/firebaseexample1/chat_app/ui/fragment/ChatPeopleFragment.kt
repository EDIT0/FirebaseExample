package com.example.firebaseexample1.chat_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseexample1.chat_app.model.ChatUserModel
import com.example.firebaseexample1.chat_app.ui.activity.ChatMessageRoomActivity
import com.example.firebaseexample1.chat_app.ui.adapter.ChatPeopleAdapter
import com.example.firebaseexample1.databinding.FragmentChatPeopleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatPeopleFragment : Fragment() {

    private val TAG = ChatPeopleFragment::class.java.simpleName

    private var _binding: FragmentChatPeopleBinding? = null
    private val binding get() = _binding!!

    private val database = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference

    private lateinit var chatPeopleAdapter: ChatPeopleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatPeopleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
//        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatPeopleAdapter = ChatPeopleAdapter {
            val intent = Intent(binding.root.context, ChatMessageRoomActivity::class.java)
            intent.putExtra("otherUid", it.uid)
            startActivity(intent)
        }

        binding.rvPeople.layoutManager = LinearLayoutManager(layoutInflater.context)
        binding.rvPeople.adapter = chatPeopleAdapter

        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        Log.i(TAG, "유저 ID: ${userUid}")
        myRef.child("chat_users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatUserModelList = ArrayList<ChatUserModel>()
                    for(data in snapshot.children) {
                        val chatUserModel : ChatUserModel? = data.getValue(ChatUserModel::class.java)

                        Log.i(TAG, "내 ID: ${userUid} other: ${chatUserModel?.uid}")
                        if(chatUserModel?.uid == userUid) {
                            continue
                        }
                        if (chatUserModel != null) {
                            chatUserModelList.add(chatUserModel)
                        }
                    }
                    chatPeopleAdapter.submitList(chatUserModelList.toMutableList())
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "유저 리스트 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}