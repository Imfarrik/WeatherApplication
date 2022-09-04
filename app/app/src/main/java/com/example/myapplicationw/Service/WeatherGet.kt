package com.example.myapplicationw.Service


import com.example.myapplicationw.data.Data
import com.example.myapplicationw.data.WeatherModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherGet {

    @GET("forecast.json?")
    fun getLocation(
        @Query("q") country: String = "Tashkent",
        @Query("days") days: String = "3",
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no",

        ): Call<Data>
}