# Retrofit Converter of Kotlin serialization

A `Converter` which uses [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization) for [Retrofit](https://github.com/square/retrofit) for serialization to and from JSON.


## usage

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(SerializationConverterFactory.create())
    .build()
```


## notes

experimental
