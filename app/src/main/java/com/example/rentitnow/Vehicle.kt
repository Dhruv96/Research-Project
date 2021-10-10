package com.example.rentitnow


enum class VehicleType (val type: String){
    CAR("car"),
    TRUCK("truck")
}

enum class FuelType (val type: String){
    PETROL("Petrol"),
    DIESEL("Diesel")
}

enum class TransmissionType (val type: String){
    AUTOMATIC("Automatic"),
    MANUAL("Manual")
}

class Vehicle {
    var type: String = ""
    var costPerDay: Float = 0.0f
    var imageUrls: List<String> = listOf()
    var vendorID: String = ""
    var model: String = ""
    var manufacture: String = ""
    var transmissionType: String = ""
    var description: String = ""
    var fuelType: String = ""


    constructor(type: String, costPerDay: Float, imageUrls: List<String>, vendorID: String, model: String, manufacture: String, transmissionType: String, description: String, fuelType: String) {
        this.type = type
        this.costPerDay = costPerDay
        this.imageUrls = imageUrls
        this.vendorID = vendorID
        this.model = model
        this.manufacture = manufacture
        this.transmissionType = transmissionType
        this.description = description
        this.fuelType = fuelType
    }

    constructor() {

    }


}