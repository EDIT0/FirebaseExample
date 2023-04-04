package com.example.firebaseexample1.chat_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseexample1.chat_app.model.ChatMessageModel
import com.example.firebaseexample1.chat_app.ui.activity.ChatMessageRoomActivity
import com.example.firebaseexample1.chat_app.ui.adapter.ChatChatRoomAdapter
import com.example.firebaseexample1.chat_app.ui.adapter.ChatPeopleAdapter
import com.example.firebaseexample1.databinding.FragmentChatChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatChatFragment : Fragment() {

    private val TAG = ChatChatFragment::class.java.simpleName

    private var _binding: FragmentChatChatBinding? = null
    private val binding get() = _binding!!

    private val database = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference

    private var myUid = ""

    private lateinit var chatChatRoomAdapter: ChatChatRoomAdapter
    private var chatModels: ArrayList<ChatMessageModel> = ArrayList<ChatMessageModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatChatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
//        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        Log.i(TAG, "myUid: ${myUid}")

        chatChatRoomAdapter = ChatChatRoomAdapter {
            val intent = Intent(binding.root.context, ChatMessageRoomActivity::class.java)
            intent.putExtra("otherUid", it)
            startActivity(intent)
        }

        binding.rvChatRoom.layoutManager = LinearLayoutManager(layoutInflater.context)
        binding.rvChatRoom.adapter = chatChatRoomAdapter

        /*
        * orderByChild는 key값, equalTo는 그 key값의 value 값이다.
        * 해당 key값의 value가 같은 값을 모두 찾아 나열할 수 있다.
        * */
        myRef.child("chat_rooms")
            .orderByChild("users/${myUid}")
            .equalTo(true)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    chatModels.clear()
//                    Log.i(TAG, "onDataChange")
//                    for(item in snapshot.children) {
//                        Log.i(TAG, "방: ${item}")
//                        item.getValue(ChatMessageModel::class.java)?.let {
//                            chatModels.add(it)
//                        }
//                    }
//                    chatChatRoomAdapter.submitList(chatModels.toMutableList())
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModels.clear()
                    for(item in snapshot.children) {
                        Log.i(TAG, "방 종류: ${item}")
                        item.getValue(ChatMessageModel::class.java)?.let {
                            Log.i(TAG, "방: ${it}")
                            chatModels.add(it)
                        }
                    }
                    chatChatRoomAdapter.submitList(chatModels.toMutableList())
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}