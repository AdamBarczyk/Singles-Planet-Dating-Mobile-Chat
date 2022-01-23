package com.example.singlesplanetchat.chat_room

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singlesplanetchat.model.ChatRoom
import com.example.singlesplanetchat.model.Message
import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.repository.AuthenticationRepository
import com.example.singlesplanetchat.repository.ChatRoomsRepository
import com.example.singlesplanetchat.repository.ProfileRepository
import com.example.singlesplanetchat.util.LoadingState
import com.example.singlesplanetchat.util.Resource
import com.example.singlesplanetchat.util.UIEvent
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val _profileRepository: ProfileRepository,
    private val _chatRoomsRepository: ChatRoomsRepository,
    _authenticationRepository: AuthenticationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _currentChatRoom = mutableStateOf(ChatRoom())
    val currentChatRoom: State<ChatRoom> = _currentChatRoom

    private val _messages = mutableStateOf(listOf<Message>())
    val messages: State<List<Message>> = _messages

    private val _chatRoomUsers = mutableStateOf(listOf<User>())
    val chatRoomUsers: State<List<User>> = _chatRoomUsers

    private val _oneMessage = mutableStateOf(Message())
    val oneMessage: State<Message> = _oneMessage

    var currentUser: FirebaseUser? = null

    init {
        savedStateHandle.get<String>("roomId")?.let { roomId ->
            getChatRoom(roomId)
        }
        currentUser = _authenticationRepository.getCurrentUser()
    }

    fun onEvent(event: ChatRoomEvent) {
        when (event) {
            is ChatRoomEvent.LoadMessages -> {
                viewModelScope.launch {
                    _chatRoomsRepository.loadMessages(currentChatRoom.value).onEach { result ->
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
                                _messages.value = result.data!!
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
            is ChatRoomEvent.UpdateEntryMessage -> {
                _oneMessage.value = oneMessage.value.copy(
                    message = event.messageBody
                )
                Log.e("nowa wiadomosc: ", oneMessage.value.toString())
            }
            is ChatRoomEvent.SendMessage -> {
                _oneMessage.value = oneMessage.value.copy(
                    name = if (chatRoomUsers.value[0].uid == currentUser?.uid) chatRoomUsers.value[0].name
                    else chatRoomUsers.value[1].name,
                    time = Timestamp(Date()),
                    user_id = currentUser?.uid
                )

                viewModelScope.launch {
                    _chatRoomsRepository.sendMessage(oneMessage.value, currentChatRoom.value).onEach { result ->
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
        }
    }

    private fun getChatRoom(roomId: String) {
        viewModelScope.launch {
            _chatRoomsRepository.getChatRoomById(roomId).onEach { result ->
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
                        _currentChatRoom.value = result.data!!
                        getUser(currentChatRoom.value.user1!!)
                        getUser(currentChatRoom.value.user2!!)
                        delay(500)
                        onEvent(ChatRoomEvent.LoadMessages)
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

    fun getUser(uid: String) {
        viewModelScope.launch {
            _profileRepository.getUserData(uid = uid).onEach { result ->
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

                        _chatRoomUsers.value = chatRoomUsers.value.toMutableList().also {
                            it.add(result.data!!)
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