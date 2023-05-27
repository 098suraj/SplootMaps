package com.example.template

import android.app.Application
import android.content.IntentFilter
import android.location.LocationManager
import com.example.template.services.GpsServices
import com.example.template.utils.checkGpsState
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import javax.inject.Inject

@HiltAndroidApp
class Application : Application() {
    @Inject
    lateinit var gpsService: GpsServices
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
        GpsServices._isGpsEnabled.value = applicationContext.checkGpsState()
        registerGpsService()
    }

    private fun registerGpsService() {
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(gpsService, intentFilter)
    }

    override fun onTerminate() {
        unregisterReceiver(gpsService)
        super.onTerminate()
    }
}
