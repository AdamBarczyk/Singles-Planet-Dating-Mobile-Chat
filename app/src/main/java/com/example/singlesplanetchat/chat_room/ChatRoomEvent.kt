package com.example.singlesplanetchat.chat_room

sealed class ChatRoomEvent {
    data class UpdateEntryMessage(val messageBody: String): ChatRoomEvent()
    object SendMessage: ChatRoomEvent()
    object LoadMessages: ChatRoomEvent()
}