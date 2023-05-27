package com.example.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template.navigation.NavGraph
import com.example.template.navigation.Routes
import com.example.template.services.GpsServices
import com.example.template.ui.theme.SplootMapsTheme
import com.example.template.utils.permissionList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplootMapsTheme {
                Scaffold() { innerPadding ->
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        startDestination = Routes.HomeScreen.routes,
                        Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
) {
    val state = rememberMultiplePermissionsState(permissionList)
    val isGpsEnabled by GpsServices.isGpsEnabled.collectAsStateWithLifecycle()
    when {
        state.allPermissionsGranted && isGpsEnabled -> {
            navController.navigate(Routes.MapScreen.routes)
        }

        else -> {
            navController.navigate(Routes.PermissionDashboard.routes)
        }
    }
}
