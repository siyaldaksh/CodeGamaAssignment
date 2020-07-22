package com.example.codegamaassignment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Result {
    @SerializedName("geometry")
    @Expose
    private var geometry: Geometry? = null

    @SerializedName("name")
    @Expose
    private var name: String? = null

    fun getGeometry(): Geometry? {
        return geometry
    }

    fun getName() : String? {
        return name
    }

}