package com.example.rentitnow

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


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

@Parcelize
data class Vehicle (
    var type: String="" ,
    var costPerDay: Float=0.0f ,
    var imageUrls: List<String> = mutableListOf(),
    var vendorID: String="" ,
    var model: String = "" ,
    var manufacture: String = "" ,
    var transmissionType: String = "" ,
    var description: String = "" ,
    var fuelType: String = ""):Parcelable
