package com.example.template.repository

import com.example.template.model.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationInterface {
    fun getLocationUpdates(): Flow<LocationModel>
    class LocationException(message: String) : Exception(message)
    suspend fun getCurrentLocation(location: (LocationModel) -> Unit)
}
