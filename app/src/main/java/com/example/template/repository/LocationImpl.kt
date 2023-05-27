package com.example.template.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.template.model.LocationModel
import com.example.template.utils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LocationImp @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val context: Context
) : LocationInterface {
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<LocationModel> {
        return callbackFlow {
            if (!context.hasLocationPermission()) {
                throw LocationInterface.LocationException("Missing location permission")
            }


            val locationRequest =
                LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, 600000)
                    .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let {
                        launch { send(LocationModel(it.latitude, it.longitude)) }
                    }
                }
            }


            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )


            awaitClose {
                Timber.d("Location updates removed")
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }

        }

    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(location: (LocationModel) -> Unit) {
        if (!context.hasLocationPermission()) {
            throw LocationInterface.LocationException("Missing location permission")
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation: Location? ->

            // Got last known location. In some rare situations this can be null.
            lastLocation?.let {
                Timber.d("fetched current location $it")
                location(LocationModel(it.latitude,it.longitude))
            }
        }.addOnFailureListener { exception ->
            Timber.d("fetch current location failed : ${exception.message}")
        }

    }


}

