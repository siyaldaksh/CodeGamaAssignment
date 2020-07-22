package com.example.codegamaassignment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class PlacesDetail{

    @SerializedName("results")
    @Expose
    private var results: List<Result> = ArrayList()


    fun getResults(): List<Result> {
        return results
    }



}