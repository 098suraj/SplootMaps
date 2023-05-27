package com.example.template.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.template.utils.checkGpsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
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