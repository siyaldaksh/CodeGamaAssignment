package com.example.codegamaassignment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Geometry {

    @SerializedName("location")
    @Expose
    private var location: Location? = null

    fun getLocation(): Location? {
        return location
    }

}