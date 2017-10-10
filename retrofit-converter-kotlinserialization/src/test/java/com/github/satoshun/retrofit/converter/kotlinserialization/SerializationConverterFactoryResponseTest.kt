package com.github.satoshun.retrofit.converter.kotlinserialization

import com.google.common.truth.Truth
import kotlinx.serialization.Serializable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST


class SerializationConverterFactoryResponseTest {

  interface Service {
    @POST("/")
    fun postUser(@Body user: User): Call<Void>
  }

  @Serializable
  class User(
      val firstName: String,
      val lastName: String?,
      val age: Int
  )

  @Rule
  @JvmField
  val server = MockWebServer()

  private lateinit var service: Service

  @Before
  fun setUp() {
    val retrofit = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(SerializationConverterFactory.createNonStrict())
        .build()
    service = retrofit.create(Service::class.java)
  }

  @Test
  fun postBody() {
    server.enqueue(MockResponse().setBody(""))

    val call = service.postUser(User("tOm", null, 10))
    call.execute()

    val request = server.takeRequest()
    Truth.assertThat(request.body.readUtf8()).isEqualTo("{\"firstName\":\"tOm\",\"lastName\":null,\"age\":10}")
    Truth.assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
  }
}
