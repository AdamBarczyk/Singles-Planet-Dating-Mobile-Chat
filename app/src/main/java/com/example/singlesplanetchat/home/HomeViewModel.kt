package com.example.singlesplanetchat.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singlesplanetchat.model.ChatRoom
import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.repository.AuthenticationRepository
import com.example.singlesplanetchat.repository.ChatRoomsRepository
import com.example.singlesplanetchat.repository.PairsRepository
import com.example.singlesplanetchat.repository.ProfileRepository
import com.example.singlesplanetchat.util.LoadingState
import com.example.singlesplanetchat.util.Resource
import com.example.singlesplanetchat.util.Screen
import com.example.singlesplanetchat.util.UIEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository,
    private val _profileRepository: ProfileRepository,
    private val _pairingRepository: PairsRepository,
    private val _chatRoomsRepository: ChatRoomsRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _user = mutableStateOf(User())
    val user: State<User> = _user

    private val _usersList = mutableStateOf(listOf<User>())
    val usersList: State<List<User>> = _usersList

    private val _chatRoomsList = mutableStateOf(listOf<ChatRoom>())
    val chatRoomsList: State<List<ChatRoom>> = _chatRoomsList

    private val firebaseAuthentication: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        onEvent(HomeEvent.GetUserData)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.GetUserData -> {
                viewModelScope.launch {
                    val currentUser = _authenticationRepository.getCurrentUser()
                    if (currentUser == null) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Unexpected error!"))
                    } else {
                        _profileRepository.getUserData(uid = currentUser.uid).onEach { result ->
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

                                    _user.value = result.data!!

                                    onEvent(HomeEvent.GetUsers)
                                    onEvent(HomeEvent.GetChatRooms(user.value))

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
            is HomeEvent.UpdateUserData -> {
                viewModelScope.launch {
                    _profileRepository.setOrUpdateUserData(user = event.value).onEach { result ->
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
            is HomeEvent.GetUsers -> {
                viewModelScope.launch {
                    _pairingRepository.getPairs(user = user.value).onEach { result ->
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

                                _usersList.value = result.data!!
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
            is HomeEvent.OpenChatRoom -> {
                chatRoomsList.value.forEach { chatRoom ->
                    if (event.user.uid == chatRoom.user1 || event.user.uid == chatRoom.user2) {
                        viewModelScope.launch {
                            _eventFlow.emit(UIEvent.Success(Screen.ChatRoomScreen.route + "/${chatRoom.chatId}"))
                        }
                    }
                }
            }
            is HomeEvent.GetChatRooms -> {
                viewModelScope.launch {
                    _chatRoomsRepository.getChatRooms(loggedUser = user.value).onEach { result ->
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

                                _chatRoomsList.value = result.data!!
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
            is HomeEvent.LogOut -> {
                firebaseAuthentication.signOut()
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.Success(Screen.SignInScreen.route))
                }
            }
        }
    }
}