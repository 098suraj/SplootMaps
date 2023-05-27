package com.example.template.screen.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template.BuildConfig
import com.example.template.model.LocationModel
import com.example.template.model.PlacesModel
import com.example.template.remote.ApiServiceImp
import com.example.template.repository.LocationImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val locationRepo: LocationImp,
    private val apiServiceImp: ApiServiceImp
) : ViewModel() {
    init {
        getCurrentLocation()
        getLocationUpdates()
    }

    private val _mapUiState = MutableStateFlow(MapsScreenUiState())
    val mapUiState = _mapUiState.asStateFlow()
    private val _placeModelSheetUiState = MutableStateFlow(PlaceModelSheetUi())
    val placeModelSheetUiState = _placeModelSheetUiState.asStateFlow()
    fun getNearByPlaces(location: LocationModel, radius: Int) {
        viewModelScope.launch {
            apiServiceImp.getNearByPlaces(location, radius).collect {
                _mapUiState.update { mapsScreenUiState ->
                    mapsScreenUiState.copy(
                        placesLoading = false,
                        places = it
                    )
                }
                Timber.tag("NearByPlaces-VM").d(_mapUiState.value.toString())
            }
        }
    }

    fun getPlaceDetails(place_id: PlacesModel.Result) {
        viewModelScope.launch {
            apiServiceImp.getPlaceDetail(place_id.placeId).catch {
                Timber.tag("PlaceDetails-VM").e(it)
            }.collect {
                Timber.tag("PlaceDetails.VM").d(it.toString())
                _placeModelSheetUiState.update { placeModelSheetUi ->
                    placeModelSheetUi.copy(
                        markerPlacesModel = place_id,
                        placeModelSheetState = PlaceModelSheetState.Loaded,
                        imageList = it.result.photos.map {
                            "https://maps.googleapis.com/maps/api/place/photo?key=${BuildConfig.MAPS_API_KEY}&maxwidth=400&maxheight=600&photo_reference=${it.photoReference}"
                        },
                        placeDetails = it
                    )
                }
            }
        }
    }

    private fun getLocationUpdates() {
        viewModelScope.launch {
            locationRepo.getLocationUpdates()
                .catch {
                    _mapUiState.update { mapsScreenUiState ->
                        mapsScreenUiState.copy(
                            error = LoadingLocation.ERROR,
                            errorMessage = it.localizedMessage
                        )

                    }
                    Timber.d(it.localizedMessage)
                }
                .collect {
                    Timber.tag("LocationDetails-VM").d(it.toString())
                    _mapUiState.update { mapsScreenUiState ->
                        mapsScreenUiState.copy(
                            loadingLocation = LoadingLocation.Loaded,
                            location = it
                        )
                    }
                }
        }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            locationRepo.getCurrentLocation {
                _mapUiState.update { mapsScreenUiState ->
                    mapsScreenUiState.copy(
                        loadingLocation = LoadingLocation.Loaded,
                        location = it
                    )

                }
            }
        }

    }
}



