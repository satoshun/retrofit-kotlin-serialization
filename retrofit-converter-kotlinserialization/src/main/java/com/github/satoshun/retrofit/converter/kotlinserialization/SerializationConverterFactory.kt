package com.github.satoshun.retrofit.converter.kotlinserialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializerByTypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class SerializationConverterFactory(private val json: JSON) : Converter.Factory() {
  companion object {
    fun create(): SerializationConverterFactory = SerializationConverterFactory(JSON.plain)
    fun createNonStrict(): SerializationConverterFactory = SerializationConverterFactory(JSON.nonstrict)
    fun createIndented(): SerializationConverterFactory = SerializationConverterFactory(JSON.indented)
    fun createUnquoted(): SerializationConverterFactory = SerializationConverterFactory(JSON.unquoted)
  }

  override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>?,
      retrofit: Retrofit
  ): Converter<ResponseBody, *> {
    val serializer = serializerByTypeToken(type)
    return SerializationResponseBodyConverter(json, serializer)
  }

  override fun requestBodyConverter(
      type: Type,
      parameterAnnotations: Array<out Annotation>?,
      methodAnnotations: Array<out Annotation>?,
      retrofit: Retrofit
  ): Converter<*, RequestBody> {
    val serializer = serializerByTypeToken(type)
    return SerializationRequestBodyConverter(json, serializer)
  }
}


internal class SerializationRequestBodyConverter<T>(
    private val json: JSON,
    private val type: KSerializer<T>
) : Converter<T, RequestBody> {
  companion object {
    private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
  }

  @Throws(IOException::class)
  override fun convert(value: T): RequestBody {
    val string = json.stringify(type, value)
    return RequestBody.create(MEDIA_TYPE, string)
  }
}

internal class SerializationResponseBodyConverter<T>(
    private val json: JSON,
    private val type: KSerializer<T>
) : Converter<ResponseBody, T> {

  @Throws(IOException::class)
  override fun convert(body: ResponseBody): T {
    val string = body.string()
    return json.parse(type, string)
  }
}
