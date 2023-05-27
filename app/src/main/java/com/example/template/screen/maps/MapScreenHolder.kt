package com.example.template.screen.maps

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.template.model.LocationModel
import com.example.template.model.PlacesModel
import com.example.template.screen.components.ModalSheet
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MapScreenHolder(
    navHostController: NavHostController,
    mapsViewModel: MapsViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val screenUiState by mapsViewModel.mapUiState.collectAsStateWithLifecycle()
    val screenState = rememberMapsScreenState(state = screenUiState)
    val modelSheetUiState by mapsViewModel.placeModelSheetUiState.collectAsStateWithLifecycle()
    val currentLocation = screenState.currentLocation
    MapScreen(
        cameraPositionState = screenState.cameraPositionState,
        onTap = screenState.onTap,
        modifier = modifier,
        onMyLocationButtonClick = screenState.onMyLocationButtonClick,
        onMapLoaded = screenState.onMapLoaded,
        screenUiState = screenUiState,
        markerOnClick = {
            Timber.tag("MapsScreen").d(it.toString())
            mapsViewModel.getPlaceDetails(it)
            screenState.coroutineScope.launch { screenState.sheetState.partialExpand() }
        },
        screenState = screenState,
    )

    AnimatedVisibility(
        visible = screenState.sheetState.isVisible,
        enter = scaleIn() + expandIn(),
        exit = scaleOut() + fadeOut()
    ) {
        ModalSheet(
            modifier = modifier,
            onDismissRequest = screenState.onDismiss,
            sheetState = screenState.sheetState,
            state = modelSheetUiState,
        )

    }

    when (screenUiState.loadingLocation) {
        LoadingLocation.Loading -> {}

        LoadingLocation.Loaded -> {
            LaunchedEffect(key1 = currentLocation) {
                mapsViewModel.getNearByPlaces(
                    LocationModel(
                        latitude = currentLocation.value.latitude,
                        longitude = currentLocation.value.longitude
                    ), 5000
                )
            }
            currentLocation.value = screenUiState.location
        }

        LoadingLocation.ERROR -> {
            Box(modifier.fillMaxSize()) {
                Text(
                    text = screenUiState.errorMessage,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = modifier.align(
                        Alignment.Center
                    )
                )
            }
        }
    }
}


@Composable
private fun MapScreen(
    cameraPositionState: CameraPositionState,
    onTap: (LatLng) -> Unit,
    modifier: Modifier,
    onMyLocationButtonClick: () -> Boolean,
    onMapLoaded: () -> Unit,
    screenUiState: MapsScreenUiState,
    markerOnClick: (PlacesModel.Result) -> Unit,
    screenState: MapsScreenState,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        GoogleMap(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { onTap(it) },
            onMyLocationButtonClick = onMyLocationButtonClick,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                mapToolbarEnabled = true,
            ),
            onMapLoaded = onMapLoaded,

            ) {
            when (screenUiState.placesLoading) {
                false -> {
                    screenUiState.places?.results?.forEach { placeModel ->
                        val icon = remember {
                            mutableStateOf<BitmapDescriptor?>(null)
                        }
                        screenState.urlToBitmap(imageURL = placeModel.icon, onSuccess = {
//                            icon.value=BitmapDescriptorFactory.fromBitmap(it)
                        })

                        MarkerItem(
                            placeModel = placeModel,
                            onClick = markerOnClick,
                            icon = icon.value
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun MarkerItem(
    placeModel: PlacesModel.Result,
    icon: BitmapDescriptor? = null,
    onClick: (placeModel: PlacesModel.Result) -> Unit,
) {
    val position = LatLng(
        placeModel.geometry.location.lat, placeModel.geometry.location.lng
    )
    Marker(
        state = rememberMarkerState(position = position),
        icon = icon,

        onClick = {
            onClick(placeModel)
            true
        },
        title = placeModel.name
    )


}
