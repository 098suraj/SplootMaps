package com.example.template.di

import android.content.Context
import com.example.template.remote.ApiService
import com.example.template.remote.ApiServiceImp
import com.example.template.repository.LocationImp
import com.example.template.repository.LocationInterface
import com.example.template.services.GpsServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    fun provideContext(
        @ApplicationContext context: Context,
    ): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun providesLocationImplementation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context,
    ): LocationInterface = LocationImp(fusedLocationProviderClient, context)

    @Provides
    @Singleton
    fun provideGpsBroadCast(): GpsServices = GpsServices()

    @Provides
    @Singleton
    fun apiService(apiService: ApiService) = ApiServiceImp(apiService)
}
