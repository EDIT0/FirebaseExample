package com.example.firebaseexample1.chat_app.model

data class ChatUserModel(
    var userName: String = "",
    var profileImageUri: String = "",
    var uid: String = "",
    var pushToken: String = "",
    var comment: String = ""
)