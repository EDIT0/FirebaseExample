package com.example.firebaseexample1.realtime_db

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseexample1.databinding.ActivityRealtimeHomeBinding
import com.example.firebaseexample1.firestore.FireStoreMemoActivity
import com.example.firebaseexample1.firestore.adapter.UserAdapter
import com.example.firebaseexample1.firestore.model.UserModel
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase


class RealtimeHomeActivity : AppCompatActivity() {

    private val TAG = RealtimeHomeActivity::class.java.simpleName

    private lateinit var binding: ActivityRealtimeHomeBinding

    private lateinit var userAdapter: UserAdapter

    private val db = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val userList = mutableListOf<UserModel>()

    private var date: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealtimeHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setRecyclerView()
        snapShotListener()
        buttonClickListener()


    }

    private fun setRecyclerView() {
        userAdapter = UserAdapter() {
            AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("삭제 or 로그인")
                .setPositiveButton("로그인", DialogInterface.OnClickListener { _, _ ->
                    val startMemoActivity = Intent(binding.root.context, RealtimeMemoActivity::class.java)
                    startMemoActivity.apply { putExtra("email", "${it.email}") }
                    startActivity(startMemoActivity)
                })
                .setNegativeButton("삭제") { _, _ ->
                    db.child("users")
                        .child(emailConverter(it.email))
                        .removeValue()
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {

                        }
//                    val document = db.collection("user").document()
//                    val documentID = document.id
//                    Log.i(TAG, "document id: ${documentID}")
//
//                    db.collection("user")
//                        .whereEqualTo("date", it.date)
//                        .get()
//                        .addOnSuccessListener { documents ->
//                            for (document in documents) {
//                                Log.d(TAG, "${document.id} => ${document.data}")
//
//                                db.collection("user")
//                                    .document(document.id)
//                                    .delete()
//                                    .addOnSuccessListener {
//                                        Log.d(TAG, "DocumentSnapshot successfully deleted! ${it}")
////                                userList.remove(
////                                    Item(
////                                        document.data["uid"].toString(),
////                                        document.data["email"].toString(),
////                                        document.data["name"].toString(),
////                                        document.data["tel"].toString().toLong(),
////                                        document.data["date"].toString().toLong()
////                                    )
////                                )
////                                adapter.updateList(userList)
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Log.w(TAG, "Error deleting document", e)
//                                    }
//                            }
//                        }
//                        .addOnFailureListener { exception ->
//                            Log.w(TAG, "Error getting documents: ", exception)
//                        }
                }
                .show()
        }

        binding.rvData.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvData.adapter = userAdapter
    }

    private fun snapShotListener() {
        db.child("users")
            .orderByChild("date")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {  // Called once with the initial value and again
                    userList.clear()
                    val value = dataSnapshot.value
                    Log.d(TAG, "Value is: $value")


                    for (snapshot1 in dataSnapshot.children) {
                        val userModel = snapshot1.getValue(UserModel::class.java)
                        userModel?.let {
                            Log.i(TAG, "${it.email}, ${it.tel}")
                            userList.add(it)
                        }
                    }
                    userList.reverse()
                    userAdapter.submitList(userList.toMutableList())
                }

                override fun onCancelled(error: DatabaseError) {  // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
    }

    private fun buttonClickListener() {
        binding.btnInputData.setOnClickListener {
            binding.apply {
                if (etEmail.text.isEmpty() || etName.text.isEmpty() || etTel.text.isEmpty()) {
                    Toast.makeText(binding.root.context, "검색어가 비었습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            date = System.currentTimeMillis()

            val user = UserModel(
                "",
                "${binding.etEmail.text}",
                "${binding.etName.text}",
                binding.etTel.text.toString().toLong(),
                date
            )

            db.child("users")
//                .child(emailConverter(binding.etEmail.text.toString()))
                .push()
                .setValue(user)
                .addOnSuccessListener {
                    Log.i(TAG, "성공")
                    binding.apply {
                        etName.setText("")
                        etEmail.setText("")
                        etTel.setText("")
                    }
                }
                .addOnFailureListener {
                    Log.i(TAG, "실패")
                }

        }
    }

    private fun emailConverter(email: String) : String {
        return email.replace("@", "_").replace(".", "_")
    }
}