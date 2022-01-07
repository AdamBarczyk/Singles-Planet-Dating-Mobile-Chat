package com.example.singlesplanetchat.composables

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.example.singlesplanetchat.details.DetailsScreen
//import com.example.singlesplanetchat.home.HomeScreen
//import com.example.singlesplanetchat.pairs.PairsScreen
//import com.example.singlesplanetchat.profile.ProfileScreen
import com.example.singlesplanetchat.sign_in.SignInScreen
import com.example.singlesplanetchat.sign_up.SignUpScreen
import com.example.singlesplanetchat.startup.StartupScreen
import com.example.singlesplanetchat.util.Constants
import com.example.singlesplanetchat.util.Screen

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalPermissionsApi
@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Constants.AUTH_GRAPH_ROUTE,
        route = Constants.ROOT_GRAPH_ROUTE
    ) {
        authenticationNavGraph(navController = navController)
//        homeNavGraph(navController = navController)
    }
}

fun NavGraphBuilder.authenticationNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.SignInScreen.route,
        route = Constants.AUTH_GRAPH_ROUTE
    ) {
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = Screen.StartupScreen.route) {
            StartupScreen(navController = navController)
        }
    }
}

//@ExperimentalMaterialApi
//@ExperimentalPermissionsApi
//@ExperimentalCoilApi
//fun NavGraphBuilder.homeNavGraph(
//    navController: NavHostController
//) {
//    navigation(
//        startDestination = Screen.HomeScreen.route,
//        route = Constants.HOME_GRAPH_ROUTE
//    ) {
//        composable(route = Screen.HomeScreen.route) {
//            HomeScreen(navController = navController)
//        }
//        composable(route = Screen.PairsScreen.route) {
//            PairsScreen(navController = navController)
//        }
//        composable(route = Screen.ProfileScreen.route) {
//            ProfileScreen(navController = navController)
//        }
//        composable(route = Screen.DetailsScreen.route + "/{uid}") {
//            DetailsScreen(navController = navController)
//        }
//    }
//}