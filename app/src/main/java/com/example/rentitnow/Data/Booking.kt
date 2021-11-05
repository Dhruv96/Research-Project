package com.example.rentitnow.Data


enum class PaymentStatus (val type: String){
        PENDING("pending"),
        PAID("paid")
}

enum class BookingStatus (val type: String){
        UPCOMING("upcoming"),
        IN_PROGRESS("in_progress"),
        COMPLETED("completed")
}


data class Booking(
        var addOnsString: String= "",
        var addOnsPrice: Double=0.0,
        var pickUpLocation: String= "",
        var paymentStatus: String= "",
        var pickUpDate: String= "",
        var returnDate: String= "",
        var noOfDays: Int=0,
        var finalPrice: Double= 0.0,
        var bookingStatus: String= "",
        var vehicleId:String="",
        var vendorId:String="",

        var model: String = "" ,
        var manufacture: String = "" ,
        var imageUrls: List<String> = mutableListOf(),
        var userId: String = "",
        var userFname: String = ""


): java.io.Serializable {

    fun finalPrice(format: String) {

    }

}
