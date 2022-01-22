package com.example.singlesplanetchat.chat_room

sealed class ChatRoomEvent {
    object LoadMessages: ChatRoomEvent()
}