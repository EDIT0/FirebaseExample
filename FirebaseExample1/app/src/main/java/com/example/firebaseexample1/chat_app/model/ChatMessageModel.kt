package com.example.firebaseexample1.chat_app.model

import java.util.Objects

data class ChatMessageModel(
    var users: HashMap<String, Boolean> = HashMap<String, Boolean>(),
    var comments : HashMap<String, ChatCommentModel> = HashMap<String, ChatCommentModel>()
) {
    data class ChatCommentModel(
        var uid: String = "",
        var message: String = "",
        var date: Any? = null,
        var readUsers: HashMap<String, Any> = HashMap<String, Any>()
    )
}