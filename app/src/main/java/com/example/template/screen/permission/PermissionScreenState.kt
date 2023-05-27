package com.example.template.screen.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template.navigation.Routes
import com.example.template.services.GpsServices
import com.example.template.utils.permissionList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

@Immutable
data class PermissionData(
    var icon: ImageVector,
    var title: String = "",
    var permissionStatus: String = "",
    var description: String = "",
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberPermissionHandlerState(
    rememberMultiplePermissionsState: MultiplePermissionsState = rememberMultiplePermissionsState(
        permissions = permissionList,
    ),
    context: Context = LocalContext.current,
    navController: NavController = rememberNavController(),

) = remember(rememberMultiplePermissionsState, context) {
    PermissionHandlerScreenState(
        permissionsState = rememberMultiplePermissionsState,
        context = context,
        navController = navController,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Stable
class PermissionHandlerScreenState(
    val permissionsState: MultiplePermissionsState,
    val context: Context,
    val navController: NavController,
) {
    val isGpsEnabled = GpsServices.isGpsEnabled
    private val timber = Timber.tag("PermissionHandlerScreenState")
    private val isLocationEnabled = MutableStateFlow(permissionsState.allPermissionsGranted)
    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
        .build()
    private val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    private val client = LocationServices.getSettingsClient(context)

    val permissionDataList = listOf(
        PermissionData(
            icon = Icons.Filled.Lock,
            "Location permission required",
            "Location permission : ",
            "This is required in order for the app to take Location",
        ),
        PermissionData(
            icon = Icons.Filled.LocationOn,
            "GPS needs to be turned On",
            "GPS status : ",
            "This is required to fetch the location",
        ),
    )

    fun locationStatus(): StateFlow<Boolean> {
        isLocationEnabled.value = permissionsState.allPermissionsGranted
        return isLocationEnabled.asStateFlow()
    }

    fun invokeLocationPermission() {
        permissionsState.launchMultiplePermissionRequest()
    }

    fun invokeGpsLocation() {
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        context as Activity,
                        REQUEST_CHECK_SETTINGS,
                    )
                } catch (sendx: IntentSender.SendIntentException) {
                    timber.e(sendx)
                }
            }
        }
    }

    fun onClickIntent() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun onClickNavigate() {
        navController.navigate(Routes.MapScreen.routes)
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 199
    }
}
