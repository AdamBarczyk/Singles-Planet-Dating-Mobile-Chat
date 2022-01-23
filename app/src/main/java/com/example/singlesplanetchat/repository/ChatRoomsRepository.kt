package com.example.singlesplanetchat.repository

import com.example.singlesplanetchat.model.ChatRoom
import com.example.singlesplanetchat.model.Message
import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRoomsRepository {
    fun getChatRooms(loggedUser: User): Flow<Resource<List<ChatRoom>>>
    fun getChatRoomById(chatRoomId: String): Flow<Resource<ChatRoom>>
    fun loadMessages(chatRoom: ChatRoom): Flow<Resource<List<Message>>>
    fun sendMessage(message: Message, chatRoom: ChatRoom): Flow<Resource<Void>>
}