package com.github.satoshun.retrofit.converter.kotlinserialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
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
      is Class<*> -> SerializationConverter<Any>(json, type.kotlin.serializer())
      is ParameterizedType -> {
        SerializationConverter<Any>(json, (type.rawType as Class<*>).kotlin.serializer())
      }
      else -> throw IllegalArgumentException("not support type: $type")
    }
  }
}


internal class SerializationConverter<T>(
    private val json: JSON,
    private val type: KSerializer<out Any>
) : Converter<ResponseBody, T> {

  @Throws(IOException::class)
  override fun convert(value: ResponseBody): T? {
    return json.parse(type as KSerializer<T>, value.string())
  }
}
