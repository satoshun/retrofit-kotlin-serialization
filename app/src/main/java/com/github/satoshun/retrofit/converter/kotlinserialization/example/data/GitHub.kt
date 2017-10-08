package com.github.satoshun.retrofit.converter.kotlinserialization.example.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHub {
  @GET("users/{username}")
  fun user(@Path("username") username: String): Call<User>
}
