package com.example.firebaseexample1.chat_app.ui.adapter

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseexample1.R
import com.example.firebaseexample1.chat_app.model.ChatMessageModel
import com.example.firebaseexample1.chat_app.model.ChatUserModel
import com.example.firebaseexample1.databinding.MessageListItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ChatMessageAdapter(
    private val clickListener:(ChatUserModel)->Unit,
    private val myUid: String,
    private val otherUid: String
) : ListAdapter<ChatMessageModel.ChatCommentModel, ChatMessageAdapter.ViewHolder>(diffUtil) {

    private lateinit var otherUserModel: ChatUserModel
    fun setOtherUserModel(otherUserModel: ChatUserModel) {
        this.otherUserModel = otherUserModel
    }
    private lateinit var chatRoomUid: String
    fun setChatRoomUid(chatRoomUid: String) {
        this.chatRoomUid = chatRoomUid
    }

    private val TAG = ChatMessageAdapter::class.java.simpleName

    private val database = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference

    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

    private var peopleCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MessageListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: MessageListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chatCommentModel: ChatMessageModel.ChatCommentModel) {
            binding.tvMessage.text = chatCommentModel.message
            binding.tvMessageDate.text = chatCommentModel.date.toString()

            Log.i(TAG, chatCommentModel.message)

            if(chatCommentModel.uid == myUid) {
                // 내 메시지
                binding.tvMessage.text = chatCommentModel.message
                binding.tvMessage.setBackgroundResource(R.drawable.my_message_ballon)
                binding.tvMessage.textSize = 25f
                binding.otherLayout.visibility = View.INVISIBLE
                binding.rootLayout.gravity = Gravity.END
                setReadCounter(chatCommentModel, binding.tvReadCounterLeft)
            } else {
                // 상대 메시지
                Glide.with(binding.ivOtherProfile.context)
                    .load(R.drawable.ic_launcher_background)
                    .optionalCircleCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.ivOtherProfile)
                binding.tvOtherName.text = otherUserModel.userName

                binding.tvMessage.text = chatCommentModel.message
                binding.tvMessage.setBackgroundResource(R.drawable.other_message_ballon)
                binding.tvMessage.textSize = 25f
                binding.otherLayout.visibility = View.VISIBLE
                binding.rootLayout.gravity = Gravity.START
                setReadCounter(chatCommentModel, binding.tvReadCounterRight)
            }

            val unixTime: Long = chatCommentModel.date.toString().toLong()
            val date = Date(unixTime)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val time = simpleDateFormat.format(date)
            binding.tvMessageDate.text = time
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.message_list_item
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ChatMessageModel.ChatCommentModel>() {
            override fun areContentsTheSame(oldItem: ChatMessageModel.ChatCommentModel, newItem: ChatMessageModel.ChatCommentModel) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ChatMessageModel.ChatCommentModel, newItem: ChatMessageModel.ChatCommentModel) =
                oldItem.date == newItem.date
        }
    }

    private fun setReadCounter(chatCommentModel: ChatMessageModel.ChatCommentModel, tvReadCounter: TextView) {
        if(peopleCount == 0) {
            myRef.child("chat_rooms")
                .child(chatRoomUid)
                .child("users")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val users = snapshot.getValue() as HashMap<String, Boolean>

                        peopleCount = users.size

                        val count = peopleCount - chatCommentModel.readUsers.size

                        if (count > 0) {
                            tvReadCounter.visibility = View.VISIBLE
                            tvReadCounter.text = count.toString()
                        } else {
                            tvReadCounter.visibility = View.INVISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        } else {
            val count = peopleCount - chatCommentModel.readUsers.size

            if (count > 0) {
                tvReadCounter.visibility = View.VISIBLE
                tvReadCounter.text = count.toString()
            } else {
                tvReadCounter.visibility = View.INVISIBLE
            }
        }
    }
}