package com.example.template.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

@AndroidEntryPoint
class GpsServices() : BroadcastReceiver() {
    companion object {
        val _isGpsEnabled = MutableStateFlow(false)
        val isGpsEnabled = _isGpsEnabled.asStateFlow()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val locationManager = context?.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        _isGpsEnabled.value = gpsStatus
        Timber.tag("GpsServices").d(gpsStatus.toString())
    }
}
