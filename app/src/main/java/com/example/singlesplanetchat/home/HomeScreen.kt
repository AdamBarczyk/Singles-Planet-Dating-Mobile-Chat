package com.example.singlesplanetchat.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.example.singlesplanetchat.util.Constants
import com.example.singlesplanetchat.util.UIEvent
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("MissingPermission")
@ExperimentalPermissionsApi
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val usersList = viewModel.usersList.value
    val scaffoldState = rememberScaffoldState()





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
                    Log.e("DUPA JASU: ", event.route)
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
            Column() {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Czaty",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier = Modifier
                                .padding(start = 7.5.dp)
                        )

                        IconButton(
                            onClick = {
                                viewModel.onEvent(HomeEvent.LogOut)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout button"
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
//                    .background(Color.White)
                        .wrapContentHeight()
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    itemsIndexed(usersList) { index, user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                .background(Color.Transparent)
                                .clickable { viewModel.onEvent(HomeEvent.OpenChatRoom(user)) },
                            elevation = 10.dp,
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    //.padding(5.dp)
                                    .background(MaterialTheme.colors.primary),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Row(
                                    modifier = Modifier
                                        //.background(Color.Green),
                                        .padding(start = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberImagePainter(user.photoURL),
                                        contentDescription = "Item Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                    )

                                    Spacer(modifier = Modifier.padding(5.dp))

                                    Column {
                                        Text(
                                            text = user.name.toString(),
                                            color = Color.Black,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )

//                                        Spacer(modifier = Modifier.padding(2.dp))
//
//                                        Text(
//                                            text = "Lorem Ipsum is simply Item ${index + 1}",
//                                            color = Color.Gray,
//                                            fontSize = 14.sp,
//                                            fontWeight = FontWeight.Normal
//                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            LazyColumn {
//                itemsIndexed(usersList) { index, user ->
//                    Text(
//                        text = user.name.toString(),
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