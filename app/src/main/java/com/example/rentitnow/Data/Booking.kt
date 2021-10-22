package com.example.rentitnow.Data

data class Booking(
        var addOnsString: String= "",
        var addOnsPrice: Float=0.0f,
        var pickUpLocation: String= "",
        var paymentStatus: String= "",
        var pickUpDate: String= "",
        var returnDate: String= "",
        var noOfDays: Int=0,
        var finalPrice: Float= 0.0f,
        var bookingStatus: String= "",
        var vehicleId:String="",
        var vendorId:String="",
): java.io.Serializable {

    fun finalPrice(format: String) {

    }

}
