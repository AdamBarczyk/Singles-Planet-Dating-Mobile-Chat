package com.example.singlesplanetchat.sign_in

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
class SignInViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.EnteredEmail -> {
                _email.value = event.value
            }
            is SignInEvent.EnteredPassword -> {
                _password.value = event.value
            }
            is SignInEvent.SignIn -> {
                viewModelScope.launch {
                    if (email.value.isBlank() || password.value.isBlank()) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Enter email address and password!"))
                    } else {
                        _authenticationRepository.signIn(
                            email = email.value,
                            password = password.value
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

                                    _eventFlow.emit(UIEvent.ShowSnackbar("Signed in!"))
                                    delay(500)

                                    if (_authenticationRepository.isUserFirstLogin()) {
                                        _eventFlow.emit(UIEvent.Success(Screen.StartupScreen.route))
                                    } else {
                                        _eventFlow.emit(UIEvent.Success(Screen.HomeScreen.route))
                                    }

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
            is SignInEvent.ResetPassword -> {
                viewModelScope.launch {
                    if (email.value.isBlank()) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Enter email address!"))
                    } else {
                        _authenticationRepository.resetPassword(email = email.value)
                            .onEach { result ->
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
                                        _eventFlow.emit(UIEvent.ShowSnackbar("Email with resetting password link was sent to given email address."))
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