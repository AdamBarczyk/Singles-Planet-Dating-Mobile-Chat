package com.example.singlesplanetchat.chat_room

import android.util.Log
import android.widget.LinearLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.singlesplanetchat.composables.LeftMessageBox
import com.example.singlesplanetchat.composables.RightMessageBox
import com.example.singlesplanetchat.util.Constants
import com.example.singlesplanetchat.util.UIEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChatRoomScreen(
    navController: NavController,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()
    val messages = viewModel.messages.value
    val currentUserId = viewModel.currentUser?.uid
    val chatRoomUsers = viewModel.chatRoomUsers.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UIEvent.Success -> {
                    navController.navigate(event.route)
                }
                else -> {
                    Log.e(Constants.LOG_TAG, "Something went wrong!")
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = MaterialTheme.colors.secondary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                //verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background
                ) {
                    Text(
//                        text = "Rozmowca",
                        text = if (chatRoomUsers[0].uid == currentUserId) chatRoomUsers[0].name!!
                        else chatRoomUsers[1].name!!,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 0.85f, fill = true),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        if (message.user_id == currentUserId) {
                            RightMessageBox(
                                msg = message,
//                                photoURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQDhQugX44XTnGgOHCvN--nqcEZ6tM4uEy28w&usqp=CAU"
                                photoURL = if (chatRoomUsers[0].uid == currentUserId) chatRoomUsers[0].photoURL!!
                            else chatRoomUsers[1].photoURL!!
                            )
                        } else {
                            LeftMessageBox(
                                msg = message,
//                                photoURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQDhQugX44XTnGgOHCvN--nqcEZ6tM4uEy28w&usqp=CAU"
                                photoURL = if (chatRoomUsers[0].uid == currentUserId) chatRoomUsers[1].photoURL!!
                                else chatRoomUsers[0].photoURL!!
                            )
                        }
                    }
                }

                var text by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = text, //viewModel.oneMessage.value.toString(),
                    onValueChange = { newText ->
                        viewModel.onEvent(ChatRoomEvent.UpdateEntryMessage(newText))
                        text = newText
                    },
                    label = {
                        Text("Type Your Message")
                    },
                    maxLines = 7,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 1.dp)
                        .fillMaxWidth()
                        .heightIn(0.dp, 200.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    //singleLine = false,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(ChatRoomEvent.SendMessage)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Button"
                            )
                        }
                    }
                )
            }

//            LazyColumn() {
//                items(20) { index ->
//                    Text(
//                        text = index.toString(),
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold,
//                        textAlign = TextAlign.Start,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 24.dp)
//                    )
//                }
//            }
        }
    }
}