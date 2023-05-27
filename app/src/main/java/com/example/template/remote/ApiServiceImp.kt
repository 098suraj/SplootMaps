package com.example.template.remote

import com.example.template.model.LocationModel
import com.example.template.model.PlaceDetails
import com.example.template.model.PlacesModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class ApiServiceImp @Inject constructor(private val apiService: ApiService) {

    fun getNearByPlaces(location: LocationModel, radius: Int) = callbackFlow {
        val apiCallback = apiService.getNearByPlaces(location = location, radius = radius)
        apiCallback.enqueue(object : Callback<PlacesModel> {
            override fun onResponse(call: Call<PlacesModel>, response: Response<PlacesModel>) {
//                Timber.tag("ApiServiceImp").d(response.body().toString())
                launch {
                    response.body()?.let {
                        send(it)
                    }
                }
            }

            override fun onFailure(call: Call<PlacesModel>, t: Throwable) {
                Timber.tag("ApiServiceImp").d(t)
            }
        })
        awaitClose { apiCallback.cancel() }
    }

    fun getPlaceDetail(place_Id: String) = callbackFlow {
//        Timber.tag("ApiServiceImp").d(place_Id)
        val apiCallBack = apiService.getPlaceDetails(place_id = place_Id)
        apiCallBack.enqueue(object : Callback<PlaceDetails> {
            override fun onResponse(call: Call<PlaceDetails>, response: Response<PlaceDetails>) {
                Timber.tag("ApiServiceImp").d(response.body().toString())
                launch {
                    response.body()?.let {
                        send(it)
                    }
                }
            }

            override fun onFailure(call: Call<PlaceDetails>, t: Throwable) {
                Timber.tag("ApiServiceImp").d(t)
            }
        })
        awaitClose { apiCallBack.cancel() }
    }
}
