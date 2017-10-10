package com.github.satoshun.retrofit.converter.kotlinserialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
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
    return when (type) {
      is Class<*> -> SerializationResponseBodyConverter(json, type.kotlin.serializer())
      is ParameterizedType -> {
        SerializationResponseBodyConverter(json, (type.rawType as Class<*>).kotlin.serializer())
      }
      else -> throw IllegalArgumentException("not support type: $type")
    }
  }

  override fun requestBodyConverter(
      type: Type,
      parameterAnnotations: Array<out Annotation>?,
      methodAnnotations: Array<out Annotation>?,
      retrofit: Retrofit
  ): Converter<*, RequestBody>? {
    return when (type) {
      is Class<*> -> SerializationRequestBodyConverter(json, type.kotlin.serializer())
      is ParameterizedType -> {
        SerializationRequestBodyConverter(json, (type.rawType as Class<*>).kotlin.serializer())
      }
      else -> throw IllegalArgumentException("not support type: $type")
    }
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
  override fun convert(value: T): RequestBody? {
    val str = json.stringify(type, value)
    return RequestBody.create(MEDIA_TYPE, str)
  }
}

internal class SerializationResponseBodyConverter<T>(
    private val json: JSON,
    private val type: KSerializer<T>
) : Converter<ResponseBody, T> {

  @Throws(IOException::class)
  override fun convert(value: ResponseBody): T? {
    return json.parse(type, value.string())
  }
}
