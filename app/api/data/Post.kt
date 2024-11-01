package com.example.arthub.api.data

import kotlinx.serialization.Serializable


@Serializable
data class Post(
    val id: Long,
    val title: String,
    val description: String,
    val tags: List<Tag>? = null,
    val image: Image? = null,
    val patron: User? = null
)

