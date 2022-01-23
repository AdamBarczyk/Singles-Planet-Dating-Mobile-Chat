package com.example.singlesplanetchat.home

import com.example.singlesplanetchat.model.User

sealed class HomeEvent {
    data class UpdateUserData(val value: User): HomeEvent()
    data class OpenChatRoom(val user: User): HomeEvent()
    data class GetChatRooms(val value: User): HomeEvent()
    object GetUserData: HomeEvent()
    object GetUsers: HomeEvent()
    object LogOut: HomeEvent()
}