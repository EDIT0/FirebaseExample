package com.example.firebaseexample1.chat_app.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample1.chat_app.model.ChatUserModel
import com.example.firebaseexample1.databinding.ActivityChatSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class ChatSignUpActivity : AppCompatActivity() {

    companion object {
        const val PICK_FROM_ALBUM = 10
    }

    private val TAG = ChatSignUpActivity::class.java.simpleName

    private lateinit var binding: ActivityChatSignUpBinding

    private val database = FirebaseDatabase.getInstance("https://fir-example1-e865b-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference
    private var mFirebaseStorage: FirebaseStorage = Firebase.storage

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatSignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }

        binding.btnSignUp.setOnClickListener {
            binding.apply {
                if (etEmail.text.isEmpty() || etName.text.isEmpty() || etPw.text.isEmpty() || etPw.text.length < 6) {
                    Toast.makeText(binding.root.context, "가입 조건이 맞지 않습니다.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(etEmail.text.toString(), etPw.text.toString())
                    .addOnCompleteListener {
                        if (!it.isSuccessful && it.exception is FirebaseAuthUserCollisionException) {
                            val exception = it.exception as FirebaseAuthUserCollisionException
                            Log.i(TAG, "아이디 중복: ${exception}")
                            return@addOnCompleteListener
                        }

                        if (it.isSuccessful) {
                            val uid = it.result.user?.uid!!

                            val chatUserModel = ChatUserModel(
                                binding.etName.text.toString(),
                                imageUri.toString(),
                                uid,
                                "",
                                "기본 메시지"
                            )

                            myRef.child("chat_users")
                                .child(uid)
                                .setValue(chatUserModel)
                                .addOnSuccessListener {
                                    onBackPressed()
                                    val intent = Intent(binding.root.context, ChatSplashActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    onBackPressed()
                                }


//                            val userProfileChangeRequest: UserProfileChangeRequest =
//                                UserProfileChangeRequest.Builder()
//                                    .setDisplayName(etName.text.toString()).build()
//                            it.result.user?.updateProfile(userProfileChangeRequest)


//                            try {
//                                mFirebaseStorage.reference
//                                    .child("userImages")
//                                    .child(uid)
//                                    .putFile(imageUri!!)
//                                    .addOnCompleteListener {
//                                        Log.i(TAG, "사진 성공")
//                                        var imageUrl = "";
//
//                                        val chatUserModel = ChatUserModel(
//                                            etName.text.toString(),
//                                            imageUrl,
//                                            FirebaseAuth.getInstance().currentUser?.uid!!,
//                                            "",
//                                            ""
//                                        )
//
//                                        myRef.child("users")
//                                            .child(uid)
//                                            .setValue(chatUserModel)
//                                            .addOnSuccessListener {
//                                                onBackPressed()
//                                            }
//                                    }
//                                    .addOnFailureListener {
//                                        Log.i(TAG, "사진 실패 ${it.message}")
//                                    }
//                            } catch (e: Exception) {
//                                Log.i(TAG, "이미지 넣기 에러: ${e.message}")
//                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(binding.root.context, "${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            binding.ivProfile.setImageURI(data?.data)
//            imageUri = data?.data //이미지 경로 원본
//            imageUri = Uri.parse("android.resource://com.example.project/" + R.drawable.ic_launcher_background)

        }
    }
}