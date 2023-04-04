package com.example.firebaseexample1.firestore.model

data class UserModel (
    val uid: String = "",
    val email : String = "",
    val name: String = "",
    val tel: Long = 0L,
    val date: Long = 0L
) {
}