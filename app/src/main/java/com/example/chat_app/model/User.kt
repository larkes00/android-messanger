package com.example.chat_app.model

data class User(val uid: String, val username: String, val email: String, val profileImageUrl: String) {
    constructor() : this("", "", "", "")
}