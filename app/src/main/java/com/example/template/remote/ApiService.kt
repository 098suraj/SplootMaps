package com.example.template.remote

import android.location.Location
import android.net.Uri
import com.example.template.BuildConfig.MAPS_API_KEY
import com.example.template.model.LocationModel
import com.example.template.model.PlaceDetails
import com.example.template.model.PlacesModel
import com.google.android.libraries.places.api.model.Place
import retrofit2.Call
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @GET("place/nearbysearch/json?")
    fun getNearByPlaces(
        @Query("key") key:String=MAPS_API_KEY,
        @Query("location") location: LocationModel,
        @Query("radius") radius: Int
    ): Call<PlacesModel>


    @GET("place/details/json")
    fun getPlaceDetails(
        @Query("key") key:String=MAPS_API_KEY,
        @Query("place_id") place_id:String,
    ):Call<PlaceDetails>

    @Headers("Content-Type: application/json")
    @GET("place/photo")
    fun getPhotos(
        @Query("key") key:String=MAPS_API_KEY,
        @Query("photo_reference") photo_reference:String,
        @Query("maxHeight")  maxHeight:Int,
        @Query("maxWidth") maxWidth:Int
    ):Call<Uri>

}