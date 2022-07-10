package com.example.chat_app.model

import java.sql.Timestamp

class Message(
    val message: String,
    val senderId: String
) {
    constructor() : this("", "")
}