package com.example.template.screen.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.template.model.LocationModel
import com.example.template.model.PlaceDetails
import com.example.template.model.PlacesModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMapsScreenState(
    state: MapsScreenUiState,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    rememberBottomSheetState: SheetState = rememberModalBottomSheetState(),
    context: Context = LocalContext.current

) = remember(cameraPositionState, coroutineScope, context) {
    return@remember MapsScreenState(
        cameraPositionState = cameraPositionState,
        coroutineScope = coroutineScope,
        state = state,
        rememberBottomSheetState = rememberBottomSheetState,
        context = context,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
class MapsScreenState(
    val cameraPositionState: CameraPositionState,
    val coroutineScope: CoroutineScope,
    val state: MapsScreenUiState,
    val rememberBottomSheetState: SheetState,
    val context: Context,
) {

    val sheetState = rememberBottomSheetState
    val onDismiss: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
        }
    }
    val currentLocation = mutableStateOf(state.location)
    val onTap: (LatLng) -> Unit = {
        cameraPositionState.move(CameraUpdateFactory.newLatLng(it))
    }
    val onMyLocationButtonClick: () -> Boolean = {
        val latLng = LatLng(currentLocation.value.latitude, currentLocation.value.longitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        true
    }
    val onMapLoaded: () -> Unit = {
        val latLng = LatLng(currentLocation.value.latitude, currentLocation.value.longitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }

    fun urlToBitmap(
        imageURL: String,
        onSuccess: (bitmap: Bitmap) -> Unit,
    ) {
        var bitmap: Bitmap? = null
        val loadBitmap = coroutineScope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageURL)
                .allowHardware(false)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                bitmap = (result.drawable as BitmapDrawable).bitmap
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
            }
        }
        loadBitmap.invokeOnCompletion { throwable ->
            bitmap?.let {
                onSuccess(it)
            } ?: throwable?.let {
            }
        }
    }
}

data class MapsScreenUiState(
    var loadingLocation: LoadingLocation = LoadingLocation.Loading,
    var error: LoadingLocation = LoadingLocation.ERROR,
    var placesLoading: Boolean = false,
    var errorMessage: String = "",
    var places: PlacesModel? = null,
    var location: LocationModel = LocationModel(0.toDouble(), 0.toDouble()),
    var bitmap: Bitmap? = null,
    var placeDetails: PlaceDetails? = null,
)

data class PlaceModelSheetUi(
    var placeModelSheetState: PlaceModelSheetState = PlaceModelSheetState.Loading,
    var errorMessage: String = "",
    var placeDetails: PlaceDetails? = null,
    var markerPlacesModel: PlacesModel.Result? = null,
    var imageList: List<String> = emptyList(),
)

enum class LoadingLocation {
    Loading, ERROR, Loaded
}

enum class PlaceModelSheetState {
    Loading, ERROR, Loaded
}
