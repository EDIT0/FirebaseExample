package com.example.firebaseexample1.firestore.model

data class MemoModel(
    val uid: String = "",
    val textMemo: String = "",
    val date: Long = 0L
) {
}