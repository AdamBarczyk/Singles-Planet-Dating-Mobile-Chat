package com.example.singlesplanetchat.sign_up

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singlesplanetchat.repository.AuthenticationRepository
import com.example.singlesplanetchat.util.LoadingState
import com.example.singlesplanetchat.util.Resource
import com.example.singlesplanetchat.util.Screen
import com.example.singlesplanetchat.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository
) : ViewModel() {
    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EnteredEmail -> {
                _email.value = event.value
            }
            is SignUpEvent.EnteredPassword -> {
                _password.value = event.value
            }
            is SignUpEvent.EnteredName -> {
                _name.value = event.value
            }
            is SignUpEvent.SignUp -> {
                viewModelScope.launch {
                    if (email.value.isBlank() || password.value.isBlank() || name.value.isBlank()) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Enter email address, password and your name!"))
                    } else {
                        _authenticationRepository.signUp(
                            email = email.value,
                            password = password.value,
                            name = name.value
                        ).onEach { result ->
                            when (result) {
                                is Resource.Loading -> {
                                    _state.value = state.value.copy(
                                        isLoading = true,
                                        error = "",
                                        result = result.data
                                    )
                                }
                                is Resource.Success -> {
                                    _state.value = state.value.copy(
                                        isLoading = false,
                                        error = "",
                                        result = result.data
                                    )
                                    _eventFlow.emit(UIEvent.ShowSnackbar("Account created!"))
                                    delay(500)
                                    _eventFlow.emit(UIEvent.Success(Screen.SignInScreen.route))
                                }
                                is Resource.Error -> {
                                    _state.value = state.value.copy(
                                        isLoading = false,
                                        error = result.message!!,
                                        result = result.data
                                    )
                                    _eventFlow.emit(UIEvent.ShowSnackbar(result.message))
                                }
                            }
                        }.launchIn(this)
                    }
                }
            }
        }
    }
}