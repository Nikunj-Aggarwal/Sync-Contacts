package com.example.synccontacts

import com.google.gson.Gson
import retrofit2.http.GET

interface ApiInterface {
    @GET("my/api/path")
    suspend fun getContacts() : ContactList
/*
//    companion object {
//
//        var BASE_URL = "http://nikunj.free.beeceptor.com/"
//
//        fun create() : ApiInterface {
//
//            val retrofit = Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(BASE_URL)
//                .build()
//            return retrofit.create(ApiInterface::class.java)
//
//        }
//    }
 */
}


