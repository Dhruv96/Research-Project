package com.example.rentitnow.Data


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


enum class PaymentStatus (val type: String){
        PENDING("pending"),
        PAID("paid")
}

enum class BookingStatus (val type: String){
        UPCOMING("upcoming"),
        IN_PROGRESS("in_progress"),
        COMPLETED("completed")
}


@Parcelize
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
        var userId: String = ""
): java.io.Serializable, Parcelable {

    fun finalPrice(format: String) {

    }

}
