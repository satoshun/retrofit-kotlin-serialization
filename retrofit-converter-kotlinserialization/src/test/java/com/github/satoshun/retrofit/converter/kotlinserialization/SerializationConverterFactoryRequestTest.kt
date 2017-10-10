package com.github.satoshun.retrofit.converter.kotlinserialization

import com.google.common.truth.Truth
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

class SerializationConverterFactoryRequestTest {

  interface Service {
    @GET("/")
    fun getUser(): Call<User>
  }

  @Serializable
  class User(
      val name: String
  )

  @Rule
  @JvmField
  val server = MockWebServer()

  private lateinit var service: Service

  private fun setUpPlain() {
    val retrofit = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(SerializationConverterFactory.create())
        .build()
    service = retrofit.create(Service::class.java)
  }

  private fun setUpNonStrict() {
    val retrofit = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(SerializationConverterFactory.createNonStrict())
        .build()
    service = retrofit.create(Service::class.java)
  }

  @Test
  fun plainMatchField() {
    setUpPlain()
    server.enqueue(MockResponse().setBody("{\"name\":\"tOm\"}"))

    val call = service.getUser()
    val response = call.execute()
    val user = response.body()!!
    Truth.assertThat(user.name).isEqualTo("tOm")
  }

  @Test(expected = SerializationException::class)
  fun plainNotExistsField() {
    setUpPlain()
    server.enqueue(MockResponse().setBody("{\"hoge\":\"tOm\"}"))

    val call = service.getUser()
    call.execute()
  }

  @Test(expected = SerializationException::class)
  fun plainNotMatchField() {
    setUpPlain()
    server.enqueue(MockResponse().setBody("{\"name\":\"tOm\", \"age\": 10}"))

    val call = service.getUser()
    call.execute()
  }

  @Test
  fun plainNotMatchTypeField() {
    setUpPlain()
    server.enqueue(MockResponse().setBody("{\"name\": 10}"))

    val call = service.getUser()
    val response = call.execute()
    val user = response.body()!!
    Truth.assertThat(user.name).isEqualTo("10")
  }

  @Test
  fun nonStrictMatchField() {
    setUpNonStrict()
    server.enqueue(MockResponse().setBody("{\"name\":\"tOm\"}"))

    val call = service.getUser()
    val response = call.execute()
    val user = response.body()!!
    Truth.assertThat(user.name).isEqualTo("tOm")
  }

  @Test(expected = MissingFieldException::class)
  fun nonStrictNotExistsField() {
    setUpNonStrict()
    server.enqueue(MockResponse().setBody("{\"hoge\":\"tOm\"}"))

    val call = service.getUser()
    call.execute()
  }

  @Test
  fun nonStrictNotMatchField() {
    setUpNonStrict()
    server.enqueue(MockResponse().setBody("{\"name\":\"tOm\", \"age\": 10}"))

    val call = service.getUser()
    val response = call.execute()
    val user = response.body()!!
    Truth.assertThat(user.name).isEqualTo("tOm")
  }

  @Test
  fun nonStrictNotMatchTypeField() {
    setUpNonStrict()
    server.enqueue(MockResponse().setBody("{\"name\": 10}"))

    val call = service.getUser()
    val response = call.execute()
    val user = response.body()!!
    Truth.assertThat(user.name).isEqualTo("10")
  }
}
