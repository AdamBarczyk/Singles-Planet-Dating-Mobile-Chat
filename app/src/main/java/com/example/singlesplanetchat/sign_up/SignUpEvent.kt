package com.example.singlesplanetchat.sign_up

sealed class SignUpEvent {
    data class EnteredEmail(val value: String): SignUpEvent()
    data class EnteredPassword(val value: String): SignUpEvent()
    data class EnteredName(val value: String): SignUpEvent()
    object SignUp: SignUpEvent()
}