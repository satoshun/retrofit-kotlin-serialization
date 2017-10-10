# Retrofit Converter of Kotlin serialization

[![CircleCI](https://circleci.com/gh/satoshun/RetrofitKotlinSerialization.svg?style=svg)](https://circleci.com/gh/satoshun/RetrofitKotlinSerialization)

A `Converter` which uses [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization) for [Retrofit](https://github.com/square/retrofit) for serialization to and from JSON.


## usage

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(SerializationConverterFactory.create())
    .build()
```

We can use a nonstrict mode

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(SerializationConverterFactory.createNonStrict())
    .build()
```

or indented, unquoted mode

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(SerializationConverterFactory.createIndented())
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(SerializationConverterFactory.createUnquoted())
    .build()
```


## notes

experimental
