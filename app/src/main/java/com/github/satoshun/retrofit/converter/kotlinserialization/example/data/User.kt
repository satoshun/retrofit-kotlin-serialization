package com.github.satoshun.retrofit.converter.kotlinserialization.example.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("name") val name: String,
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)
