package com.example.firebaseexample1.realtime_db

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseexample1.databinding.ActivityRealtimeMemoBinding
import com.example.firebaseexample1.firestore.adapter.MemoAdapter
import com.example.firebaseexample1.firestore.model.MemoModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RealtimeMemoActivity : AppCompatActivity() {
    private val TAG = RealtimeMemoActivity::class.java.simpleName

    private lateinit var binding: ActivityRealtimeMemoBinding

    private lateinit var memoAdapter: MemoAdapter

    private val db = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private var memoList = mutableListOf<MemoModel>()

    private var date: Long = 0
    private lateinit var email : String
    val textMemoMap = HashMap<String, MutableList<MemoModel>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealtimeMemoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        email = intent.getStringExtra("email")?:""
        if(email == "") {
            Toast.makeText(binding.root.context, "로그인 오류", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        setRecyclerView()
        snapShotListener()
        buttonClickListener()
    }

    private fun setRecyclerView() {
        memoAdapter = MemoAdapter() {
//            db.collection("memo")
//                .whereEqualTo("date", it.date)
//                .whereEqualTo("uid", it.uid)
//                .get()
//                .addOnSuccessListener { documents ->
//                    for (document in documents) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
//
//                        db.collection("memo")
//                            .document(document.id)
//                            .delete()
//                            .addOnSuccessListener {
//                                Log.d(TAG, "DocumentSnapshot successfully deleted! ${it}")
//                            }
//                            .addOnFailureListener { e ->
//                                Log.w(TAG, "Error deleting document", e)
//                            }
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.w(TAG, "Error getting documents: ", exception)
//                }
        }

        binding.rvMemo.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvMemo.adapter = memoAdapter
//        binding.rvMemo.addOnScrollListener(onScrollListener)

    }

    private fun snapShotListener() {
        db.child("memo")
            .child("text")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG, "onDataChange(): ${snapshot}")
                    memoList.clear()
                    val value = snapshot.value
                    Log.d(TAG, "Value is: $value")


                    for (snapshot1 in snapshot.children) {
                        Log.d(TAG, "snapshot1.value : ${snapshot1.value} ${email} ${snapshot1.getValue(MemoModel::class.java)?.uid}")
                        if(snapshot1.getValue(MemoModel::class.java)?.uid == email) {
                            Log.i(TAG, "통과..?")
                            val memoModel = snapshot1.getValue(MemoModel::class.java)
                            Log.d(TAG, "값 : $memoModel")
                            if(memoModel?.uid == email) {
                                memoModel?.let {
                                    Log.i(TAG, "${it.uid}, ${it.textMemo}")
                                    memoList.add(it)
                                }
                            }
                        }
                    }
                    memoList.reverse()
                    memoAdapter.submitList(memoList.toMutableList())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

//        FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("memo/text").orderByChild("date")
//            .addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    Log.i(TAG, "onChildAdded(): \n${snapshot}\n${previousChildName}")
////                    memoList.clear()
//                    val value = snapshot.value
//                    Log.d(TAG, "Value is: $value")
//
////                    val memoModel = value as MemoModel
////                    Log.i(TAG, "${memoModel.uid} ${memoModel.textMemo}")
//                    Log.i(TAG, "snapshot children : ${snapshot.children}")
////                    for (snapshot1 in snapshot.children) {
//                        if(snapshot.getValue(MemoModel::class.java)?.uid == email) {
////                            Log.i(TAG, "Snapshot1: ${snapshot1}")
//                            val memoModel = MemoModel(
//                                snapshot.getValue(MemoModel::class.java)!!.uid,
//                                snapshot.getValue(MemoModel::class.java)!!.textMemo,
//                                snapshot.getValue(MemoModel::class.java)!!.date
//                            )
//                            Log.d(TAG, "값 : $memoModel")
//                            if(memoModel?.uid == email) {
//                                memoModel?.let {
//                                    Log.i(TAG, "${it.uid}, ${it.textMemo}")
//                                    memoList.add(it)
//                                }
//                                memoList.reverse()
//                                memoAdapter.submitList(memoList.toMutableList())
//                            }
////                        }
//                    }
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onChildRemoved(snapshot: DataSnapshot) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//        db.child("memo")
//            .child("text")
//            //                                    .child(snapshot1.key!!)
//            .orderByValue("date")
//            .addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    Log.i(TAG, "onChildAdded(): \n${snapshot}\n${previousChildName}")
//                    memoList.clear()
//                    val value = snapshot.value
//                    Log.d(TAG, "Value is: $value")
//
//
//                    for (snapshot1 in snapshot.children) {
//                        if(snapshot.getValue(MemoModel::class.java)?.uid == email) {
//                            val memoModel = snapshot1.getValue(MemoModel::class.java)
//                            Log.d(TAG, "값 : $memoModel")
//                            if(memoModel?.uid == email) {
//                                memoModel?.let {
//                                    Log.i(TAG, "${it.uid}, ${it.textMemo}")
//                                    memoList.add(it)
//                                }
//                                memoList.reverse()
//                                memoAdapter.submitList(memoList.toMutableList())
//                            }
//                        }
//                    }
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                    Log.i(TAG, "onChildChanged(): \n${snapshot}\n${previousChildName}")
//                }
//
//                override fun onChildRemoved(snapshot: DataSnapshot) {
//                    Log.i(TAG, "onChildRemoved(): \n${snapshot}")
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                    Log.i(TAG, "onChildMoved(): \n${snapshot}\n${previousChildName}")
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.i(TAG, "onCancelled(): ${error}")
//                }
//
//            })
    }

    private fun buttonClickListener() {
        binding.fabAddMemo.setOnClickListener {
            val editText = EditText(this)
            editText.gravity = Gravity.START
            val builder = AlertDialog.Builder(this)
                .setTitle("메모")
                .setView(editText)
                .setPositiveButton("저장", DialogInterface.OnClickListener { dialog, which ->
                    Log.i(TAG, "${editText.text}")
                    binding.apply {
                        if (editText.text.isEmpty()) {
                            Toast.makeText(binding.root.context, "내용이 비었습니다.", Toast.LENGTH_SHORT).show()
                            return@OnClickListener
                        }
                    }
                    date = System.currentTimeMillis()

                    val memo = MemoModel(
                        "${email}",
                        "${editText.text}",
                        date
                    )

                    db.child("memo")
                        .child("text")
                        .push()
                        .setValue(memo)
                        .addOnSuccessListener {
                            Log.i(TAG, "성공")
                        }
                        .addOnFailureListener {
                            Log.i(TAG, "실패")
                        }
                })
            builder.show()
        }
    }

    private fun emailConverter(email: String) : String {
        return email.replace("@", "_").replace(".", "_")
    }
}