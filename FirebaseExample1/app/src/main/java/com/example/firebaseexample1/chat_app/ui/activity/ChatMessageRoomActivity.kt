package com.example.firebaseexample1.chat_app.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseexample1.chat_app.model.ChatMessageModel
import com.example.firebaseexample1.chat_app.model.ChatUserModel
import com.example.firebaseexample1.chat_app.ui.adapter.ChatMessageAdapter
import com.example.firebaseexample1.databinding.ActivityChatMessageRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatMessageRoomActivity : AppCompatActivity() {
    private val TAG = ChatMessageRoomActivity::class.java.simpleName

    private lateinit var binding : ActivityChatMessageRoomBinding

    private val database = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference

    private lateinit var otherUserModel : ChatUserModel

    private var myUid = ""
    private var otherUid = ""
    private var chatRoomUid = ""

    private lateinit var chatMessageAdapter: ChatMessageAdapter

    private lateinit var databaseReference : DatabaseReference
    private lateinit var valueEventListener : ValueEventListener

    private var firstExecute = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageRoomBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initData()

        chatMessageAdapter = ChatMessageAdapter({

        }, myUid, otherUid)

        checkChatRoom()
        buttonClickListener()

//        getOtherUserModelAndGetMessageList()
    }

    private fun initData() {
        myUid = FirebaseAuth.getInstance().currentUser?.uid.toString() // 채팅을 요구하는 id, 나
        otherUid = intent.getStringExtra("otherUid").toString() // 채팅을 당하는 id
    }

    private fun checkChatRoom() {
        myRef.child("chat_rooms")
            .orderByChild("users/${myUid}")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG, "기존 방이 있는지 확인")
                    var count = 0
                    for(item in snapshot.children) {
                        Log.i(TAG, "데이터" + ++count);

                        val chatMessageModel = item.getValue(ChatMessageModel::class.java) // 데이터베이스에 저장된 모델 자체
                        if(chatMessageModel?.users?.containsKey(otherUid)!!) {
                            Log.i(TAG, "채팅방 이름: ${item.key.toString()}")
                            chatRoomUid = item.key.toString() // 방 이름
                            chatMessageAdapter.setChatRoomUid(chatRoomUid)
//                            button.setClickable(true);
                            binding.rvMessage.layoutManager = LinearLayoutManager(binding.root.context)
                            binding.rvMessage.adapter = chatMessageAdapter
                        }
                    }

                    if(firstExecute) {
                        firstExecute = false
                        getOtherUserModelAndGetMessageList()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun buttonClickListener() {
        binding.btnSend.setOnClickListener {
            var chatMessageModel = ChatMessageModel()
            chatMessageModel.users.put(myUid, true)
            chatMessageModel.users.put(otherUid, true)

            if(chatRoomUid == "") {
                Log.i(TAG, "방 만들기")
                myRef.child("chat_rooms")
                    .push()
                    .setValue(chatMessageModel)
                    .addOnSuccessListener {
                        checkChatRoom()
                    }
            } else {
                Log.i(TAG, "방 이미 있고 메시지 보내기")
                var chatCommentModel = ChatMessageModel.ChatCommentModel()
                chatCommentModel.uid = myUid
                chatCommentModel.message = binding.etInputMessage.text.toString()
                chatCommentModel.date = ServerValue.TIMESTAMP

                myRef.child("chat_rooms")
                    .child(chatRoomUid)
                    .child("comments")
                    .push()
                    .setValue(chatCommentModel)
                    .addOnCompleteListener {
                        // Send FCM
                        binding.etInputMessage.setText("")
                    }
            }
        }
    }

    private fun getOtherUserModelAndGetMessageList() {
        myRef.child("chat_users")
            .child(otherUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG, "상대 유저 정보 받기")
                    otherUserModel = snapshot.getValue(ChatUserModel::class.java)!!
                    Log.i(TAG, "상대 유저 정보: ${otherUserModel}")
                    chatMessageAdapter.setOtherUserModel(otherUserModel)
                    getMessageList()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    val chatCommentModelList = ArrayList<ChatMessageModel.ChatCommentModel>()
    private fun getMessageList() {
        Log.i(TAG, "메시지 가져올 채팅방 이름: ${chatRoomUid}")
        databaseReference = myRef.child("chat_rooms")
            .child(chatRoomUid)
            .child("comments")

        valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i(TAG, "대화 내용 가져오기")
                chatCommentModelList.clear()

                var readUsersMap: HashMap<String, Any> = HashMap<String, Any>()
                Log.i(TAG, "대화 내용 스냅샷: ${snapshot}")
                for(item in snapshot.children) {
//                    Log.i(TAG, "메시지 키: ${item.key.toString()}")
                    val key = item.key.toString()

                    var commentOrigin : ChatMessageModel.ChatCommentModel = item.getValue(ChatMessageModel.ChatCommentModel::class.java)!!
                    var commentModify : ChatMessageModel.ChatCommentModel = item.getValue(ChatMessageModel.ChatCommentModel::class.java)!!

                    commentModify.readUsers.put(myUid, true)

                    readUsersMap.put(key, commentModify)

                    chatCommentModelList.add(commentOrigin)
//                    Log.i(TAG, "대화 내용: ${commentOrigin.message}")
                }

                // 내 아이디가 읽은 것들 중에 없으면? 서버에 보고
                if(chatCommentModelList.isEmpty()) {
                    return
                }

                if(!chatCommentModelList.get(chatCommentModelList.size - 1).readUsers.containsKey(myUid)) {
                    Log.i(TAG, "루루루루루루루루루루루루루루루루루루1")
                    myRef.child("chat_rooms")
                        .child(chatRoomUid)
                        .child("comments")
                        .updateChildren(readUsersMap)
                        .addOnCompleteListener {
                            val list = chatMessageAdapter.currentList
                            if(list != chatCommentModelList) {
                                chatMessageAdapter.submitList(chatCommentModelList.toMutableList())
                                binding.rvMessage.scrollToPosition(chatCommentModelList.size - 1)
                                Log.i(TAG, "리스트에 메시지 넣기 성공1")
                            }
                        }
                } else {
                    Log.i(TAG, "루루루루루루루루루루루루루루루루루루2")
                    val list = chatMessageAdapter.currentList
                    if(list != chatCommentModelList) {
                        chatMessageAdapter.submitList(chatCommentModelList.toMutableList())
                        binding.rvMessage.scrollToPosition(chatCommentModelList.size - 1)
                        Log.i(TAG, "리스트에 메시지 넣기 성공2")
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onBackPressed() {

        databaseReference.removeEventListener(valueEventListener)
        super.onBackPressed()
    }
}