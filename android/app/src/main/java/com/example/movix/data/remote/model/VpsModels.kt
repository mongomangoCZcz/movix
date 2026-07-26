package com.example.movix.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VpsStream(
    val ident: String,
    val name: String,
    val size: Long
)
