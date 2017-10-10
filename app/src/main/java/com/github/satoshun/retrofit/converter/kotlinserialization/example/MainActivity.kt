package com.github.satoshun.retrofit.converter.kotlinserialization.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.satoshun.retrofit.converter.kotlinserialization.SerializationConverterFactory
import com.github.satoshun.retrofit.converter.kotlinserialization.example.data.GitHub
import com.github.satoshun.retrofit.converter.kotlinserialization.example.data.User
import kotlinx.android.synthetic.main.main_act.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber

class MainActivity : AppCompatActivity() {

  private lateinit var retrofit: Retrofit

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_act)

    Timber.plant(Timber.DebugTree())

    retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(SerializationConverterFactory.createNonStrict())
        .build()

    val gitHub = retrofit.create(GitHub::class.java)
    gitHub.user("satoshun").enqueue(object : Callback<User> {
      override fun onFailure(call: Call<User>, e: Throwable) {
        Timber.e(e, "fail")
      }

      override fun onResponse(call: Call<User>, response: Response<User>) = runOnUiThread {
        val body = response.body()!!
        name.text = body.login
        Glide.with(profile)
            .load(body.avatarUrl)
            .into(profile)
      }
    })
  }
}
