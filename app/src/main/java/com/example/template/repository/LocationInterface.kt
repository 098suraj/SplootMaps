package com.example.template.repository

import android.location.Location
import com.example.template.model.LocationModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationInterface {
    fun getLocationUpdates(): Flow<LocationModel>
    class LocationException(message: String) : Exception(message)
    suspend fun getCurrentLocation(location: (LocationModel) -> Unit)

}