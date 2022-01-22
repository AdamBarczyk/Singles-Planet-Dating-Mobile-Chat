package com.example.singlesplanetchat.home

import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singlesplanetchat.model.User
import com.example.singlesplanetchat.repository.AuthenticationRepository
import com.example.singlesplanetchat.repository.PairsRepository
import com.example.singlesplanetchat.repository.ProfileRepository
import com.example.singlesplanetchat.util.LoadingState
import com.example.singlesplanetchat.util.Resource
import com.example.singlesplanetchat.util.Screen
import com.example.singlesplanetchat.util.UIEvent
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
    private val _pairingRepository: PairsRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _user = mutableStateOf(User())
    val user: State<User> = _user

    private val _selectedUser = mutableStateOf(User())
    private val selectedUser: State<User> = _selectedUser

    private val _usersList = mutableStateOf(listOf<User>())
    val usersList: State<List<User>> = _usersList

    private var _userSelectedProfiles = mutableListOf<String>()

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

                                    _userSelectedProfiles =
                                        user.value.selectedProfiles.toMutableList()

                                    if (!user.value.location.isNullOrEmpty()) {
                                        onEvent(HomeEvent.GetPairs)
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
            is HomeEvent.SetUserLocation -> {
                _user.value = user.value.copy(
                    location = event.value
                )

                if (user.value.uid != null) {
                    onEvent(HomeEvent.UpdateUserData(user.value))
                }
            }
            is HomeEvent.GetPairs -> {
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
            is HomeEvent.SelectNo -> {
                _selectedUser.value = event.value
                _usersList.value = usersList.value.toMutableList().also { list ->
                    list.remove(selectedUser.value)
                }
            }
            is HomeEvent.SelectYes -> {
                _selectedUser.value = event.value
                if (!_user.value.selectedProfiles.contains(selectedUser.value.uid)) {

                    if (selectedUser.value.selectedProfiles.contains(user.value.uid)) {
                        onEvent(HomeEvent.NewPair)
                    } else {
                        _userSelectedProfiles.add(selectedUser.value.uid!!)

                        // Delete selected profile from list
                        onEvent(HomeEvent.SelectNo(selectedUser.value))

                        _user.value = user.value.copy(
                            selectedProfiles = _userSelectedProfiles
                        )

                        onEvent(HomeEvent.UpdateUserData(user.value))
                    }
                }
            }
            is HomeEvent.OpenChatRoom -> {
                viewModelScope.launch {
                    _selectedUser.value = event.value
                    _eventFlow.emit(UIEvent.Success(Screen.ChatRoomScreen.route))
                }
            }
            is HomeEvent.NewPair -> {
                onEvent(HomeEvent.SelectNo(selectedUser.value))

                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.Success(Screen.HomeScreen.route))
                }

                _selectedUser.value = selectedUser.value.copy(
                    pairs = selectedUser.value.pairs.toMutableList()
                        .also { it.add(user.value.uid!!) }
                )
                _selectedUser.value = selectedUser.value.copy(
                    selectedProfiles = selectedUser.value.selectedProfiles.toMutableList()
                        .also { it.remove(user.value.uid) }
                )
                onEvent(HomeEvent.UpdateUserData(selectedUser.value))

                _user.value = user.value.copy(
                    pairs = user.value.pairs.toMutableList().also { it.add(selectedUser.value.uid!!) }
                )
                onEvent(HomeEvent.UpdateUserData(user.value))
            }
        }
    }
}