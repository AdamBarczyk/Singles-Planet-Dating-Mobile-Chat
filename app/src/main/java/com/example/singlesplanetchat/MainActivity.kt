package com.example.singlesplanetchat

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.example.singlesplanetchat.composables.SetupNavGraph
import com.example.singlesplanetchat.ui.theme.SinglesPlanetChatTheme
import com.example.singlesplanetchat.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val firebaseAuthentication = FirebaseAuth.getInstance()
        //todo: signOut()
        firebaseAuthentication.signOut()

        setContent {
            SinglesPlanetChatTheme {

                val focusManager = LocalFocusManager.current
                val systemUIController = rememberSystemUiController()
                systemUIController.setSystemBarsColor(MaterialTheme.colors.background)

                Surface(color = MaterialTheme.colors.background,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
                ) {
                    val navController = rememberNavController()
                    SetupNavGraph(navController = navController)

                    firebaseAuthentication.currentUser?.let {
                        navController.navigate(Screen.HomeScreen.route)
                    }
                }
            }
        }
    }
}