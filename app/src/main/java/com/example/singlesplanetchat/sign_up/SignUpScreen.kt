package com.example.singlesplanetchat.sign_up

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.singlesplanetchat.util.Constants
import com.example.singlesplanetchat.util.UIEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val emailState = viewModel.email.value
    val passwordState = viewModel.password.value
    val nameState = viewModel.name.value
    val scaffoldState = rememberScaffoldState()
    var passwordVisibility by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UIEvent.Success -> {
                    navController.popBackStack()
                }
                else -> {
                    Log.d(Constants.LOG_TAG, "Something went wrong!")
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState, modifier = Modifier.padding(4.dp)) {
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(25.dp),
                tint = MaterialTheme.colors.secondary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = com.example.singlesplanetchat.R.drawable.singles_planet_logo_main),
                contentDescription = "Logo",
                modifier = Modifier.scale(1.5f)
            )
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colors.primary, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )
                        OutlinedTextField(
                            value = nameState,
                            onValueChange = { viewModel.onEvent(SignUpEvent.EnteredName(it)) },
                            placeholder = { Text(text = "First name") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                cursorColor = MaterialTheme.colors.primaryVariant,
                                unfocusedBorderColor = MaterialTheme.colors.primary,
                                textColor = MaterialTheme.colors.primaryVariant,
                                placeholderColor = MaterialTheme.colors.primaryVariant,
                            ),
                            shape = RoundedCornerShape(5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )
                        OutlinedTextField(
                            value = emailState,
                            onValueChange = { viewModel.onEvent(SignUpEvent.EnteredEmail(it)) },
                            placeholder = { Text(text = "Email address") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                cursorColor = MaterialTheme.colors.primaryVariant,
                                unfocusedBorderColor = MaterialTheme.colors.primary,
                                textColor = MaterialTheme.colors.primaryVariant,
                                placeholderColor = MaterialTheme.colors.primaryVariant,
                            ),
                            shape = RoundedCornerShape(5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )
                        OutlinedTextField(
                            value = passwordState,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val icon =
                                    if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                                IconButton(
                                    onClick = { passwordVisibility = !passwordVisibility }
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Show/hide password",
                                        tint = MaterialTheme.colors.primaryVariant
                                    )
                                }
                            },
                            onValueChange = { viewModel.onEvent(SignUpEvent.EnteredPassword(it)) },
                            placeholder = { Text(text = "Password") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                cursorColor = MaterialTheme.colors.primaryVariant,
                                unfocusedBorderColor = MaterialTheme.colors.primary,
                                textColor = MaterialTheme.colors.primaryVariant,
                                placeholderColor = MaterialTheme.colors.primaryVariant,
                            ),
                            shape = RoundedCornerShape(5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.onEvent(SignUpEvent.SignUp)
                        },
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.background,
                            backgroundColor = MaterialTheme.colors.secondary
                        ),
                        modifier = Modifier.width(280.dp).height(50.dp)
                    ) {
                        if (state.isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    color = MaterialTheme.colors.background
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Sign Up", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}