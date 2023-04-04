package com.example.firebaseexample1.chat_app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseexample1.R
import com.example.firebaseexample1.chat_app.model.ChatUserModel
import com.example.firebaseexample1.databinding.PeopleListItemBinding

class ChatPeopleAdapter(
    private val clickListener:(ChatUserModel)->Unit
) : ListAdapter<ChatUserModel, ChatPeopleAdapter.ViewHolder>(diffUtil) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PeopleListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: PeopleListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chatUserModel: ChatUserModel) {
            Glide.with(binding.ivUserProfile.context)
                .load(R.drawable.ic_launcher_background)
                .optionalCircleCrop()
                .into(binding.ivUserProfile)

            binding.tvUserEmail.text = chatUserModel.userName
            binding.tvProfileMessage.text = chatUserModel.comment

            binding.root.setOnClickListener {
                clickListener(chatUserModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.people_list_item
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ChatUserModel>() {
            override fun areContentsTheSame(oldItem: ChatUserModel, newItem: ChatUserModel) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ChatUserModel, newItem: ChatUserModel) =
                oldItem.uid == newItem.uid
        }
    }
}