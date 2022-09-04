package com.example.myapplicationw.Service

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val URL = "http://api.weatherapi.com/v1/"
    private const val KEY = "874348d8dfc64eeaafa71050223108"

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val myInterceptor = Interceptor { chain ->
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("key", KEY)
            .build()
        val response = chain.proceed(request)
        response
    }

    private val okHttpClient = OkHttpClient.Builder().addInterceptor(myInterceptor).addInterceptor(
        logger)

    private val builder = Retrofit
        .Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient.build())

    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}