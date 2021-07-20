package com.android.service.touchwin

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL= "https://reflexappp.herokuapp.com/"
interface interfaceAPI {
    @GET("getscore")
    fun getScore(@Header("number") phonenumber: String,
                 @Header("score") score: String): Call<response>

    @GET("getnumber")
    fun getNumber(@Header("number") number: String): Call<response>

//ez az obj közös az osztály összes példányának
    companion object {
    //több HTTP kérést tesz lehetővé egy socketen keresztül, képes szinkron s aszinkron hivásokat is létrehozni
        private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        private val okHttp = OkHttpClient.Builder().addInterceptor(logger)

        val endpoints = create(BASE_URL)
//retrfoti: Típusbiztonságos HTTP kliens, könnyen használható könyvtár csomag, mely a HTTP API-t össze köti egy Kotlin interfacel
        fun create(url: String): interfaceAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp.build())
                .build()

            return retrofit.create(interfaceAPI::class.java)
        }
    }
}