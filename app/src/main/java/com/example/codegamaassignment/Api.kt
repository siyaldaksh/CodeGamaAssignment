package com.example.codegamaassignment

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {

    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyAxbDWh90_OnwS1LFY_y94k0VuzboAnc6U")
    fun getNearbyPlaces(@Query("type") type: String, @Query("location") location: String, @Query("radius") radius: Int): Call<PlacesDetail>

}