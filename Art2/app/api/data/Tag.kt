package com.example.arthub.api.data

import kotlinx.serialization.Serializable


@Serializable
data class Tag(
    val id: Long,
    val name: String
)
