package com.example.rentitnow


enum class VehicleType (val type: String){
    CAR("car"),
    TRUCK("truck")
}

class Vehicle (
        var type: String,
        var costPerDay: Float,
        var imageUrls: List<String>,
        var color: String,
        var vendorID: String,
        var model: String,
        var manufacture: String
        )