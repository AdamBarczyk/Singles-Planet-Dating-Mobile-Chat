package com.example.singlesplanetchat.util

sealed class UIEvent {
    data class ShowSnackbar(val message: String): UIEvent()
    data class Success(val route: String): UIEvent()
    object PhotoUploaded: UIEvent()
}
