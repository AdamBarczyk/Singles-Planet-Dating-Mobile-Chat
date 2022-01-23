package com.example.singlesplanetchat.model

import com.google.firebase.Timestamp

data class Message(
    val message: String? = null,
    val name: String? = null,
    val time: Timestamp? = null,
    val user_id: String? = null
)