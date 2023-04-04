package com.example.firebaseexample1.firestore

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseexample1.databinding.ActivityFireStoreHomeBinding
import com.example.firebaseexample1.firestore.adapter.UserAdapter
import com.example.firebaseexample1.firestore.model.UserModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class FireStoreHomeActivity : AppCompatActivity() {

    private val TAG = FireStoreHomeActivity::class.java.simpleName

    private lateinit var binding: ActivityFireStoreHomeBinding

    private lateinit var userAdapter: UserAdapter

    val db = FirebaseFirestore.getInstance()
    private val userList = mutableListOf<UserModel>()

    private var date: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFireStoreHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setRecyclerView()
        getData()
        snapShotListener()
        buttonClickListener()


    }

    private fun setRecyclerView() {
        userAdapter = UserAdapter() {
            AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("삭제 or 로그인")
                .setPositiveButton("로그인", DialogInterface.OnClickListener { _, _ ->
                    val startMemoActivity = Intent(binding.root.context, FireStoreMemoActivity::class.java)
                    startMemoActivity.apply {
                        putExtra("uid", "${it.uid}")
                    }
                    startActivity(startMemoActivity)
                })
                .setNegativeButton("삭제") { _, _ ->
                    val document = db.collection("user").document()
                    val documentID = document.id
                    Log.i(TAG, "document id: ${documentID}")

                    db.collection("user")
                        .whereEqualTo("date", it.date)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Log.d(TAG, "${document.id} => ${document.data}")

                                db.collection("user")
                                    .document(document.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted! ${it}")
//                                userList.remove(
//                                    Item(
//                                        document.data["uid"].toString(),
//                                        document.data["email"].toString(),
//                                        document.data["name"].toString(),
//                                        document.data["tel"].toString().toLong(),
//                                        document.data["date"].toString().toLong()
//                                    )
//                                )
//                                adapter.updateList(userList)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error deleting document", e)
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }
                }
                .show()
        }

        binding.rvData.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvData.adapter = userAdapter
    }

    private fun getData() {
//        db.collection("user").get()
//            .addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
//                if (task.isSuccessful) {
//                    val list: MutableList<String> = ArrayList()
//                    for (document in task.result) {
//                        list.add(document.id)
//                        Log.d(TAG, list.toString())
//
//                        db.collection("user")
//                            .document(document.id)
//                            .collection("info")
//                            .orderBy("date", Query.Direction.ASCENDING)
//                            .get()
//                            .addOnSuccessListener { result ->
//                                for (document in result) {
//                                    Log.i(TAG, "${document.data}")
//                                }
//                            }
//                            .addOnFailureListener { exception ->
//                                Log.w(TAG, "Error getting documents.", exception)
//                            }
//                    }
//
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.exception)
//                }
//            })

        db.collection("test_db")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener  { result ->
//                binding.tvData.text = ""
                for(document in result) {
                    Log.i(TAG, "${document.data}")
//                    binding.tvData.append(document.data.toString() + "\n")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    val docRef = db.collection("user").orderBy("date", Query.Direction.ASCENDING)
    private fun snapShotListener() {
//        db.collection("user")
//            .get()
//            .addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
//                if (task.isSuccessful) {
//                    val list: MutableList<String> = ArrayList()
//                    for (document in task.result) {
//                        list.add(document.id)
//                        Log.d(TAG, list.toString())
//
//                        db.collection("user")
//                            .document(document.id)
//                            .collection("info")
//                            .orderBy("date", Query.Direction.ASCENDING)
//                            .addSnapshotListener { value, error ->
//                                if (error != null) {
//                                    Log.w(TAG, "Listen failed.", error)
//                                    return@addSnapshotListener
//                                }
//
//                                if (value?.isEmpty == false) {
//                                    for (dc in value.documentChanges) {
//                                        if (dc.type == DocumentChange.Type.ADDED) {
//                                            Log.d(TAG, "New User: ${dc.document.data}")
//                                            userList.add(
//                                                Item(
//                                                    dc.document.data["email"].toString(),
//                                                    dc.document.data["name"].toString(),
//                                                    dc.document.data["tel"].toString().toLong(),
//                                                    dc.document.data["date"].toString().toLong()
//                                                )
//                                            )
//                                            adapter.updateList(userList)
//                                        }
//                                        if (dc.type == DocumentChange.Type.REMOVED) {
//                                            Log.d(TAG, "Remove User: ${dc.document.data}")
//                                            userList.remove(
//                                                Item(
//                                                    dc.document.data["email"].toString(),
//                                                    dc.document.data["name"].toString(),
//                                                    dc.document.data["tel"].toString().toLong(),
//                                                    dc.document.data["date"].toString().toLong()
//                                                )
//                                            )
//                                            adapter.updateList(userList)
//                                        }
//                                        if (dc.type == DocumentChange.Type.MODIFIED) {
//                                            Log.d(TAG, "Modified User: ${dc.document.data}")
//                                        }
//                                    }
//                                }
//                            }
//                    }
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.exception)
//                }
//            })

        docRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }

            if (value?.isEmpty == false) {
                Log.i(TAG, "${value.documents}\n${value.documents.size}\n${value.documentChanges}\n${value.documentChanges.size}")
                for (dc in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "New User: ${dc.document.data}")
                        userList.addAll(0,
                            listOf(
                                UserModel(
                                    dc.document.data["uid"].toString(),
                                    dc.document.data["email"].toString(),
                                    dc.document.data["name"].toString(),
                                    dc.document.data["tel"].toString().toLong(),
                                    dc.document.data["date"].toString().toLong()
                                )
                            )
                        )
                        userAdapter.submitList(userList.toMutableList())
                    }
                    if (dc.type == DocumentChange.Type.REMOVED) {
                        Log.d(TAG, "Remove User: ${dc.document.data}")
                        userList.remove(
                            UserModel(
                                dc.document.data["uid"].toString(),
                                dc.document.data["email"].toString(),
                                dc.document.data["name"].toString(),
                                dc.document.data["tel"].toString().toLong(),
                                dc.document.data["date"].toString().toLong()
                            )
                        )
                        userAdapter.submitList(userList.toMutableList())
                    }
                    if (dc.type == DocumentChange.Type.MODIFIED) {
                        Log.d(TAG, "Modified User: ${dc.document.data}")
                        for(i in 0 until userList.size) {
                            if(userList[i].date == dc.document.data["date"].toString().toLong()) {
                                userList[i] = UserModel(
                                    dc.document.data["uid"].toString(),
                                    dc.document.data["email"].toString(),
                                    dc.document.data["name"].toString(),
                                    dc.document.data["tel"].toString().toLong(),
                                    dc.document.data["date"].toString().toLong()
                                )
                                userAdapter.submitList(userList.toMutableList())
                            }
                        }
                    }
                }
            } else {
                userList.clear()
                userAdapter.submitList(userList.toMutableList())
            }
        }
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
            val user = hashMapOf(
                "uid" to "",
                "email" to "${binding.etEmail.text}",
                "name" to "${binding.etName.text}",
                "tel" to binding.etTel.text.toString().toLong(),
                "date" to date
            )

//            val id = db.collection("user").document().id
//            // document id 직접 지정할 경우
//            db.collection("user")
//                .document(id)
//                .collection("info")
//                .add(user)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
////                    userList.add(
////                        Item(
////                            binding.etEmail.text.toString(),
////                            binding.etName.text.toString(),
////                            binding.etTel.text.toString().toLong(),
////                            date
////                        )
////                    )
////                    adapter.updateList(userList)
//                    binding.apply {
//                        etName.setText("")
//                        etEmail.setText("")
//                        etTel.setText("")
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error adding document", e)
//                }



//            // document id 직접 지정할 경우
//            db.collection("test_db")
//                .document("user")
//                .collection()
//                .document("info")
//                .set(user)
//                .addOnSuccessListener {
//                    Log.d(TAG, "DocumentSnapshot added `user`")
//                    userList.add(
//                        Item(
//                            binding.etEmail.text.toString(),
//                            binding.etName.text.toString(),
//                            binding.etTel.text.toString().toLong(),
//                            date
//                        )
//                    )
//                    adapter.updateList(userList)
//                    binding.apply {
//                        etName.setText("")
//                        etEmail.setText("")
//                        etTel.setText("")
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error adding document", e)
//                }

            // document id 직접 지정하지 않을 경우
            // Add a new document with a generated ID
            db.collection("user")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    binding.apply {
                        etName.setText("")
                        etEmail.setText("")
                        etTel.setText("")
                    }

                    db.collection("user")
                        .document(documentReference.id)
                        .update(
                            hashMapOf("uid" to documentReference.id) as Map<String, String>
                        )
                        .addOnSuccessListener {
                            Log.i(TAG, "update success")
                        }
                        .addOnFailureListener {
                            Log.i(TAG, "update failed")
                        }

                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
}