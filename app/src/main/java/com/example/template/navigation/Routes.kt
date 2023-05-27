package com.example.template.navigation

sealed class Routes(val routes:String){
    object PermissionDashboard:Routes("PermissionDashBoard")
    object MapScreen: Routes("MapScreen")
    object HomeScreen: Routes("HomeScreen")
}
