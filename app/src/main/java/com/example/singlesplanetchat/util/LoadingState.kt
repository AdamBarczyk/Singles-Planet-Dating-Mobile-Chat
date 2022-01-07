package com.example.singlesplanetchat.util

data class LoadingState(
    val isLoading: Boolean = false,
    val result: Any? = null,
    val error: String = ""
)
