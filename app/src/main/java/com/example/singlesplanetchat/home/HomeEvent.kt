package com.example.singlesplanetchat.home

import com.example.singlesplanetchat.model.User

sealed class HomeEvent {
    data class SetUserLocation(val value: List<Double>): HomeEvent()
    data class SelectYes(val value: User): HomeEvent()
    data class SelectNo(val value: User): HomeEvent()
    data class UpdateUserData(val value: User): HomeEvent()
    data class OpenChatRoom(val value: User): HomeEvent()
    object NewPair: HomeEvent()
    object GetUserData: HomeEvent()
    object GetPairs: HomeEvent()
}