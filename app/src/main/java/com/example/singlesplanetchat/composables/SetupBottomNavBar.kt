package com.example.singlesplanetchat.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.singlesplanetchat.util.BottomNavItem
import com.example.singlesplanetchat.util.Screen

@Composable
fun SetupBottomNavBar(
    navController: NavController
) {
    BottomNavBar(
        items = listOf(
            BottomNavItem(
                name = "Home",
                route = Screen.HomeScreen.route,
                icon = Icons.Filled.Home
            ),
            BottomNavItem(
                name = "Pairs",
                route = Screen.PairsScreen.route,
                icon = Icons.Filled.Groups
            ),
            BottomNavItem(
                name = "Profile",
                route = Screen.ProfileScreen.route,
                icon = Icons.Filled.Person
            ),
        ),
        navController = navController,
        onItemClick = {
            navController.navigate(it.route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}