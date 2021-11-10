package com.example.rentitnow.Data

import java.io.Serializable

data class UserRating(
    val userId: String="",
    val vendorId:String="",
    val bookingId:String="",
    val rating:Float=0.0f,
    val feedback: String=""
):Serializable {

}