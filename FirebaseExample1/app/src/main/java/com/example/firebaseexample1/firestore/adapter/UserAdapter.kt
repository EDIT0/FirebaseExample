package com.example.firebaseexample1.firestore.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseexample1.R
import com.example.firebaseexample1.databinding.MemoItemBinding
import com.example.firebaseexample1.databinding.UserItemBinding
import com.example.firebaseexample1.firestore.model.MemoModel
import com.example.firebaseexample1.firestore.model.UserModel

class UserAdapter(
    private val clickListener:(UserModel)->Unit
) : ListAdapter<UserModel, UserAdapter.ViewHolder>(diffUtil) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserItemBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userModel: UserModel) {
            binding.textView.text = "${userModel.email}\n${userModel.name}\n${userModel.tel}\n${userModel.date}"
            binding.root.setOnClickListener {
                clickListener(userModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.memo_item
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<UserModel>() {
            override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel) =
                oldItem.date == newItem.date
        }
    }

}

//class UserAdapter(
//    private val clickListener:(UserModel)->Unit
//) : RecyclerView.Adapter<UserViewHolder>() {
//
//    private var list : ArrayList<UserModel> = ArrayList<UserModel>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val item = layoutInflater.inflate(R.layout.user_item, parent, false)
//
//        return UserViewHolder(item)
//    }
//
//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        val item = list[position]
//        holder.bind(item, clickListener)
//    }
//
//    override fun getItemCount(): Int = list.size
//
//    fun updateList(userModels: List<UserModel>) {
//        if(userModels.isEmpty()) {
//            Log.i("MYTAG", "empty")
//            this.notifyDataSetChanged()
//        }
//        userModels?.let {
//            val diffCallback = ItemDiffUtilCallback(this.list, userModels)
//            val diffResult = DiffUtil.calculateDiff(diffCallback)
//
//            this.list.run {
//                clear()
//                addAll(userModels)
//                diffResult.dispatchUpdatesTo(this@UserAdapter)
//            }
//        }
//    }
//
//
//}
//
//class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//
//    fun bind(userModel : UserModel, clickListener:(UserModel)->Unit) {
//        val textView = view.findViewById<TextView>(R.id.textView)
//
//        val name = userModel.name
//        val email = userModel.email
//        val tel = userModel.tel
//        val date = userModel.date
//        textView.text = "${email}\n${name}\n${tel}\n${date}"
//
//        view.setOnClickListener {
//            clickListener(userModel)
//        }
//    }
//
//}