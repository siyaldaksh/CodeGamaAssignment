package com.example.codegamaassignment

class ApiUtils() {

    companion object{
        private val BASE_URL = "https://maps.googleapis.com/maps/"

        fun getAPIService(): Api {
            return RetrofitClient.getClient(BASE_URL).create(Api::class.java)
        }
    }


}