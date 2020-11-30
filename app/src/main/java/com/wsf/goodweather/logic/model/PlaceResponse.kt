package com.wsf.goodweather.logic.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(
    val name: String, val location: Location,
    // 接口返回的字段名为formatted_address，与自己的字段名address做映射
    @SerializedName("formatted_address") val address: String
)

data class Location(val lng: String, val lat: String)