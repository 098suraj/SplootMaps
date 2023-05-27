package com.example.template.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.template.HomeScreen
import com.example.template.screen.maps.MapScreenHolder
import com.example.template.screen.permission.PermissionHandler

@Composable
fun navGraph(
    navController: NavHostController,
    startDestination: String,
    padding: Modifier,

    ) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Routes.PermissionDashboard.routes) {
            PermissionHandler(navController,padding)
        }
        composable(route = Routes.HomeScreen.routes) {
            HomeScreen(navController)
        }
        composable(route = Routes.MapScreen.routes) {
            MapScreenHolder(navController, modifier = padding)
        }
    }
}