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

class SerializationConverterFactory : Converter.Factory() {
  companion object {
    fun create(): SerializationConverterFactory {
      return SerializationConverterFactory()
    }
  }

  override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>?,
      retrofit: Retrofit
  ): Converter<ResponseBody, *> {
    return when (type) {
      is Class<*> -> SerializationConverter<Any>(type.kotlin.serializer())
      is ParameterizedType -> {
        SerializationConverter<Any>((type.rawType as Class<*>).kotlin.serializer())
      }
      else -> throw IllegalArgumentException("not support type: $type")
    }
  }
}


internal class SerializationConverter<T>(
    private val type: KSerializer<out Any>
) : Converter<ResponseBody, T> {

  @Throws(IOException::class)
  override fun convert(value: ResponseBody): T? {
    return JSON.nonstrict.parse(type as KSerializer<T>, value.string())
  }
}
