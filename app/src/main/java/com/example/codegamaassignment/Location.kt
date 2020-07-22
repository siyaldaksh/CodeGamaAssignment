package com.example.codegamaassignment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Location {

    @SerializedName("lat")
    @Expose
    private var lat: Double? = null
    @SerializedName("lng")
    @Expose
    private var lng: Double? = null

    fun getLat(): Double? {
        return lat
    }

    fun getLng(): Double? {
        return lng
    }


}