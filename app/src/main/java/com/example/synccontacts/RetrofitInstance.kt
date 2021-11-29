package com.example.synccontacts

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        var BASE_URL = "http://nikunj.free.beeceptor.com/"

        fun getRetrofitInstance() : Retrofit {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
//            return retrofit.create(ApiInterface::class.java)
        }
    }
}