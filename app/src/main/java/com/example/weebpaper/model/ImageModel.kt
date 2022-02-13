package com.example.weebpaper.model

import com.google.gson.annotations.SerializedName

data class ImageModel(
    @SerializedName("url")
    val imageUrl: String
)
