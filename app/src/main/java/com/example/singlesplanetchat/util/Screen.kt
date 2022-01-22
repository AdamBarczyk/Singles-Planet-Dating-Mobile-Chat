package com.example.singlesplanetchat.util

sealed class Screen(val route: String) {
    object SignInScreen: Screen("sign_in_screen")
    object SignUpScreen: Screen("sign_up_screen")
    object StartupScreen: Screen("startup_screen")
    object HomeScreen: Screen("home_screen")
    object PairsScreen: Screen("pairs_screen")
    object ProfileScreen: Screen("profile_screen")
    object DetailsScreen: Screen("details_screen")
    object ChatRoomScreen: Screen("chat_room_screen")
}
