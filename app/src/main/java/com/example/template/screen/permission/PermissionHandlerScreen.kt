package com.example.template.screen.permission

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(navController: NavHostController, modifier: Modifier = Modifier) {
    val state = rememberPermissionHandlerState(navController = navController)
    val isGpsEnabled by state.isGpsEnabled.collectAsStateWithLifecycle()
    val isLocationEnabled by state.locationStatus().collectAsStateWithLifecycle()
    PermissionScreen(state, isGpsEnabled, isLocationEnabled, onClick = { if (isGpsEnabled && isLocationEnabled) state.onClickNavigate() else state.onClickIntent()  })
}

@Composable
fun PermissionScreen(
    state: PermissionHandlerScreenState,
    isGpsEnabled: Boolean,
    isLocationEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissionCardDetails = state.permissionDataList
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = modifier.height(20.dp))
            PermissionCard(
                icon = permissionCardDetails[0].icon,
                title = permissionCardDetails[0].title,
                permissionStatus = "${permissionCardDetails[0].permissionStatus} ${isLocationEnabled}",
                description = permissionCardDetails[0].description,
                onClick = { state.invokeLocationPermission() }
            )
            Spacer(modifier = modifier.height(20.dp))

            AnimatedVisibility(visible = isLocationEnabled) {
                PermissionCard(
                    icon = permissionCardDetails[1].icon,
                    title = permissionCardDetails[1].title,
                    permissionStatus = "${permissionCardDetails[1].permissionStatus} $isGpsEnabled",
                    description = permissionCardDetails[1].description,
                    onClick = { state.invokeGpsLocation() }
                )
            }
        }
        Button(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            onClick = onClick
        ) {
            Text(if(!isGpsEnabled || !isLocationEnabled )"Go to settings" else "Open Map", color = MaterialTheme.colorScheme.onPrimary)
        }
    }

}

@Composable
fun PermissionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    permissionStatus: String,
    description: String,
    onClick: () -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = modifier.padding(15.dp)) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            permissionStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    content = {
                        Icon(
                            imageVector =
                            if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                )
                Spacer(Modifier.width(2.dp))
                Button(
                    modifier = modifier
                        .wrapContentWidth()
                        .height(70.dp)
                        .padding(16.dp),
                    onClick = onClick
                ) {
                    Text(
                        "Grant",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            AnimatedVisibility(visible = isExpanded) {
                Spacer(Modifier.height(4.dp))
                Text(
                    description,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }

}