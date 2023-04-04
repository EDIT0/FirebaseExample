package com.example.firebaseexample1.chat_app.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseexample1.R
import com.example.firebaseexample1.chat_app.model.ChatMessageModel
import com.example.firebaseexample1.chat_app.model.ChatUserModel
import com.example.firebaseexample1.databinding.ChatRoomListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ChatChatRoomAdapter(
    private val clickListener:(String)->Unit
) : ListAdapter<ChatMessageModel, ChatChatRoomAdapter.ViewHolder>(diffUtil) {

    private val TAG = ChatChatRoomAdapter::class.java.simpleName

    private val database = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference

    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ChatRoomListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ChatRoomListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessageModel: ChatMessageModel) {

            var otherUid = ""

            // 일일히 유저 채팅 방에 있는 유저를 체크
            Log.i(TAG, "${chatMessageModel.users.keys} / ${FirebaseAuth.getInstance().currentUser?.uid.toString()}")
            for(user in chatMessageModel.users.keys) {
                if(user != FirebaseAuth.getInstance().currentUser?.uid.toString()) {
                    otherUid = user
//                    destinationUsers.add(destinationUid);
                }
            }

            Log.i(TAG, otherUid)

            myRef.child("chat_users")
                .child(otherUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chatUserModel = snapshot.getValue(ChatUserModel::class.java)
                        Glide.with(binding.ivProfileImage.context)
                            .load(R.drawable.ic_launcher_background)
                            .optionalCircleCrop()
                            .into(binding.ivProfileImage)

                        binding.tvChatRoomTitle.text = chatUserModel?.userName
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            // 메시지를 내림차순으로 정렬 후 마지막 메시지의 키 값을 가져옴
            var commentMap = TreeMap<String, ChatMessageModel.ChatCommentModel>(Collections.reverseOrder())
            commentMap.putAll(chatMessageModel.comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            binding.tvLastMessage.text = chatMessageModel.comments.get(lastMessageKey)?.message

            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val unixTime = chatMessageModel.comments.get(lastMessageKey)?.date as Long
            val date = Date(unixTime)
            binding.tvLastMessageDate.text = simpleDateFormat.format(date)

//            customViewHolder.itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(v.getContext(), MessageActivity.class);
//                intent.putExtra("destinationUid", destinationUsers.get(position));
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
//                    startActivity(intent, activityOptions.toBundle());
//                }
//            });

            binding.root.setOnClickListener {
                clickListener(otherUid)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.people_list_item
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ChatMessageModel>() {
            override fun areContentsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel) =
                oldItem == newItem
        }
    }
}