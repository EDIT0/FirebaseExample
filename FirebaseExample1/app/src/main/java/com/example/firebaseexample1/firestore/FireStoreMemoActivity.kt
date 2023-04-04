package com.example.firebaseexample1.firestore

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseexample1.databinding.ActivityFireStoreMemoBinding
import com.example.firebaseexample1.firestore.adapter.MemoAdapter
import com.example.firebaseexample1.firestore.model.MemoModel
import com.google.firebase.firestore.*
import java.util.*


class FireStoreMemoActivity : AppCompatActivity() {
    private val TAG = FireStoreMemoActivity::class.java.simpleName

    private lateinit var binding: ActivityFireStoreMemoBinding

    private lateinit var memoAdapter: MemoAdapter

    val db = FirebaseFirestore.getInstance()
    private var memoList = mutableListOf<MemoModel>()

    private var date: Long = 0
    private lateinit var uid : String
    val textMemoMap = HashMap<String, MutableList<MemoModel>>()

//    private val docRef by lazy {
//        Log.i(TAG, "유저 고유 아이디: ${uid}")
//        db.collection("memo").orderBy("date", Query.Direction.ASCENDING)
//    }
//    private lateinit var docRef: Task<DocumentSnapshot>
    private lateinit var docRef : Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFireStoreMemoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        uid = intent.getStringExtra("uid")?:""
        if(uid == "") {
            Toast.makeText(binding.root.context, "로그인 오류", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        setRecyclerView()
        snapShotListener()
        buttonClickListener()
    }

    private fun setRecyclerView() {
        docRef = db.collection("memo").whereEqualTo("uid", uid).orderBy("date", Query.Direction.DESCENDING)
            .limit(10)

        memoAdapter = MemoAdapter() {
            db.collection("memo")
                .whereEqualTo("date", it.date)
                .whereEqualTo("uid", it.uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        db.collection("memo")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully deleted! ${it}")
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

        binding.rvMemo.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvMemo.adapter = memoAdapter
        binding.rvMemo.addOnScrollListener(onScrollListener)

    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            val lastVisibleItemPosition =
                (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
            val itemTotalCount = recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

            // 스크롤이 끝에 도달했는지 확인
            if (lastVisibleItemPosition == itemTotalCount) {
                getData()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    private fun getData() {
        docRef.get()
            .addOnSuccessListener { result ->
//                for(document in result) {
//                    Log.i(TAG, "${document.data}")
//                }

                // Get the last visible document
                val lastVisible = result.documents[result.size() - 1]
                Log.i(TAG, "What is the LastVisible? ${lastVisible}")

                // Construct a new query starting at this document,
                // get the next 25 cities.
                val next = db.collection("memo").whereEqualTo("uid", uid).orderBy("date", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(10)

                // Use the query for pagination
                next.get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.i(TAG, "123 123 ${document.data}")
                            memoList.addAll(memoList.size,
                                listOf(
                                    MemoModel(
                                        document.data["uid"].toString(),
                                        document.data["textMemo"].toString(),
                                        document.data["date"].toString().toLong()
                                    )
                                )
                            )
                            memoAdapter.submitList(memoList.toMutableList())
                            docRef = next
                        }
                    }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun snapShotListener() {
        docRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }

            if (value?.isEmpty == false) {
                Log.i(TAG, "documents: ${value.documents}\n" +
                        "documents size: ${value.documents.size}\n" +
                        "documentChanges: ${value.documentChanges}\n" +
                        "documentChanges size: ${value.documentChanges.size}")
                for (dc in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "New Memo: ${dc.document.data}")
                        if(value.documents.size == 10) {
                            memoList.addAll(memoList.size,
                                listOf(
                                    MemoModel(
                                        dc.document.data["uid"].toString(),
                                        dc.document.data["textMemo"].toString(),
                                        dc.document.data["date"].toString().toLong()
                                    )
                                )
                            )
                        } else {
                            memoList.addAll(0,
                                listOf(
                                    MemoModel(
                                        dc.document.data["uid"].toString(),
                                        dc.document.data["textMemo"].toString(),
                                        dc.document.data["date"].toString().toLong()
                                    )
                                )
                            )
                        }

                        memoAdapter.submitList(memoList.toMutableList())
                    } else if (dc.type == DocumentChange.Type.REMOVED) {
                        Log.d(TAG, "Remove Memo: ${dc.document.data}")
                        memoList.remove(
                            MemoModel(
                                dc.document.data["uid"].toString(),
                                dc.document.data["textMemo"].toString(),
                                dc.document.data["date"].toString().toLong()
                            )
                        )
                        memoAdapter.submitList(memoList.toMutableList())
                    } else if (dc.type == DocumentChange.Type.MODIFIED) {
                        Log.d(TAG, "Modified Memo: ${dc.document.data}")
                        for(i in 0 until memoList.size) {
                            if(memoList[i].date == dc.document.data["date"].toString().toLong()) {
                                memoList[i] = MemoModel(
                                    dc.document.data["uid"].toString(),
                                    dc.document.data["textMemo"].toString(),
                                    dc.document.data["date"].toString().toLong()
                                )
                                memoAdapter.submitList(memoList.toMutableList())
                            }
                        }
                    }
                }
            } else {
                memoList.clear()
                memoAdapter.submitList(memoList.toMutableList())
            }
        }
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
                    val memo = hashMapOf(
                        "uid" to "${uid}",
                        "textMemo" to "${editText.text}",
                        "date" to date
                    )

//                    memoList.add(MemoModel("${uid}", "${editText.text}", date))
//                    textMemoMap.put("textMemo", memoList)

                    db.collection("memo")
                        .add(memo)
                        .addOnSuccessListener { documentReference ->
                            Log.i(TAG, "Saved Memo ${documentReference.id}")

                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                })
            builder.show()
        }
    }

//    private fun buttonClickListener() {
//        binding.fabAddMemo.setOnClickListener {
//            val editText = EditText(this)
//            editText.gravity = Gravity.START
//            val builder = AlertDialog.Builder(this)
//                .setTitle("메모")
//                .setView(editText)
//                .setPositiveButton("저장", DialogInterface.OnClickListener { dialog, which ->
//                    Log.i(TAG, "${editText.text}")
//                    binding.apply {
//                        if (editText.text.isEmpty()) {
//                            Toast.makeText(binding.root.context, "내용이 비었습니다.", Toast.LENGTH_SHORT).show()
//                            return@OnClickListener
//                        }
//                    }
//                    date = System.currentTimeMillis()
//                    val memo = hashMapOf(
//                        "uid" to "${uid}",
//                        "textMemo" to "${editText.text}",
//                        "date" to date
//                    )
//
//                    memoList.add(MemoModel("${uid}", "${editText.text}", date))
//                    textMemoMap.put("textMemo", memoList)
//
//                    db.collection("memo")
//                        .document(uid)
//                        .set(textMemoMap)
//                        .addOnSuccessListener { documentReference ->
//                            Log.i(TAG, "Saved Memo")
//                        }
//                        .addOnFailureListener { e ->
//                            Log.w(TAG, "Error adding document", e)
//                        }
//                })
//            builder.show()
//        }
//    }
//
//    private fun snapShotListener() {
//        docRef = db.collection("memo").document(uid).get()
//        docRef
//            .addOnSuccessListener {
//                val document: DocumentSnapshot = it
//                val a = document.get("textMemo") as ArrayList<MemoModel>
//                Log.i(TAG, "어레이1: ${a}")
////                val entries = document.toObject<MemoModel>()
////                Log.i(TAG, "어레이2: ${entries}")
//            }
//            .addOnFailureListener {
//
//            }
//            .addOnCompleteListener {
//
//            }
//            .addOnCanceledListener {
//
//            }
//
//        val docRef = db.collection("memo").document(uid)
//        docRef.addSnapshotListener { value, error ->
//            if (error != null) {
//                Log.w(TAG, "Listen failed.", error)
//                return@addSnapshotListener
//            }
//
//            if(value?.exists() == true) {
//                Log.i(TAG, "${value.data}\n${value.data?.size}\n${value.id}")
////                Log.i(TAG, "${value.data?.get("textMemo")}")
//                val array = value.data?.get("textMemo") as ArrayList<MemoModel>
//                Log.i(TAG, "${array::class.simpleName} 메모 리스트 리스너: ${array}")
//                Log.i(TAG, "무엇인가 ${array.get(0)::class.java.simpleName}")
//                val a = MemoModel(array.get(0).uid, array.get(0).textMemo, array.get(0).date.toLong())
//                Log.i(TAG, "무엇인가 ${a}")
////                for(i in 0 until array.size) {
////                    memoList.add(
////                        array.get(i) as MemoModel
////                    )
////                }
////                memoList.clear()
////                for(i in 0 until array.size) {
////                    memoList.add(
////                        MemoModel(
////                            array.get(i).uid,
////                            array.get(i).textMemo,
////                            array.get(i).date
////                        )
////                    )
////                }
////
////                memoAdapter.updateList(memoList.toMutableList())
//            }
//
////            if (value?.isEmpty == false) {
////                Log.i(TAG, "${value.documents}\n${value.documents.size}\n${value.documentChanges}\n${value.documentChanges.size}")
////                for (dc in value.documentChanges) {
////                    if (dc.type == DocumentChange.Type.ADDED) {
////                        Log.d(TAG, "New Memo: ${dc.document.data}")
////                        memoList.add(
////                            MemoModel(
////                                dc.document.data["uid"].toString(),
////                                dc.document.data["textMemo"].toString(),
////                                dc.document.data["date"].toString().toLong()
////                            )
////                        )
////                        memoAdapter.updateList(memoList)
////                    }
////                    if (dc.type == DocumentChange.Type.REMOVED) {
////                        Log.d(TAG, "Remove Memo: ${dc.document.data}")
////                        memoList.remove(
////                            MemoModel(
////                                dc.document.data["uid"].toString(),
////                                dc.document.data["textMemo"].toString(),
////                                dc.document.data["date"].toString().toLong()
////                            )
////                        )
////                        memoAdapter.updateList(memoList)
////                    }
////                    if (dc.type == DocumentChange.Type.MODIFIED) {
////                        Log.d(TAG, "Modified Memo: ${dc.document.data}")
////                        for (i in 0 until memoList.size) {
////                            if (memoList[i].date == dc.document.data["date"].toString().toLong()) {
////                                memoList[i] = MemoModel(
////                                    dc.document.data["uid"].toString(),
////                                    dc.document.data["textMemo"].toString(),
////                                    dc.document.data["date"].toString().toLong()
////                                )
////                                memoAdapter.updateList(memoList)
////                            }
////                        }
////                    }
////                }
////            } else {
////                memoList.clear()
////                memoAdapter.updateList(memoList)
////            }
//        }
//    }
}