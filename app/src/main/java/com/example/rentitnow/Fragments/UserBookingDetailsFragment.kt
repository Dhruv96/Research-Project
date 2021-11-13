package com.example.rentitnow.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.Data.BookingStatus
import com.example.rentitnow.Data.Rating
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.example.rentitnow.Vendor
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_user_booking_details.*
import java.text.SimpleDateFormat
import java.util.*


class UserBookingDetailsFragment : Fragment() {

    lateinit var booking: Booking
    lateinit var bookingId: String
    var userRating: Rating? = null
    val database = FirebaseDatabase.getInstance()

    companion object {
        val BOOKING = "booking"
        val BOOKING_ID = "booking_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_booking_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            booking = requireArguments().getSerializable(BOOKING) as Booking
            bookingId = requireArguments().getString(BOOKING_ID) as String
        }
        shorOrHideDeleteBookingButton()
        showOrHideRateBookingButton()
        addonsString.text = booking.addOnsString
        pickupdate.text = booking.pickUpDate
        returndate.text = booking.returnDate
        finalPrice.text = booking.finalPrice.toString()
        fetchVehicleDetails(booking.vehicleId)
        fetchVendorDetails(booking.vendorId)

        cancelBookingBtn.setOnClickListener{
            // Delete booking

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Action")
            builder.setMessage("Are you sure, you want to delete this booking ?")
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                database.getReference("bookings").child(bookingId).removeValue().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Booking deleted successfully", Toast.LENGTH_SHORT).show()
                    val booking_history = UserBookingHistoryFragment()
                    (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_user, booking_history, "findThisFragment")
                        .addToBackStack(null)
                        .commit()
                }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        rateBookingBtn.setOnClickListener {
            val userRatingFragment = RatingFragment()
            val bundle = Bundle()
            bundle.putSerializable(RatingFragment.BOOKING, booking)
            bundle.putString(RatingFragment.BOOKING_ID, bookingId)
            bundle.putSerializable(RatingFragment.USER_RATING, userRating)
            userRatingFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_user, userRatingFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showOrHideRateBookingButton() {
        if(booking.bookingStatus == BookingStatus.COMPLETED.type) {
            rateBookingBtn.visibility = View.VISIBLE
            database.getReference("UserRatings").child(bookingId).get().addOnSuccessListener {
                if(it.exists()) {
                    rateBookingBtn.text = "EDIT Rating"
                    userRating = it.getValue(Rating::class.java)!!
                }
                else {
                    rateBookingBtn.text = "Rate this Booking"
                }
            }
        }
        else {
            rateBookingBtn.visibility = View.INVISIBLE
        }
    }

    private fun shorOrHideDeleteBookingButton() {
        val currentDate = Date()
        val pickupDateString = booking.pickUpDate
        val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
        val pickupDate = dateFormatter.parse(pickupDateString)
        val diff: Long = pickupDate.getTime() - currentDate.getTime()
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        cancelBookingBtn.isEnabled = hours >=24
    }

    private fun fetchVehicleDetails(vehicleId: String) {
        database.getReference("vehicles").child(vehicleId).get().addOnSuccessListener {
            val vehicle = it.getValue(Vehicle::class.java)
            vehicleName.text = vehicle?.manufacture + " " + vehicle?.model
            Glide.with(context).load(vehicle?.imageUrls?.get(0)).into(vehicleDetailImageView)
        }.addOnFailureListener{
            println(it.localizedMessage)
        }
    }

    private fun fetchVendorDetails(vendorId: String) {
        database.getReference("vendors").child(vendorId).get().addOnSuccessListener {
            val vendor = it.getValue(Vendor::class.java)
            vendorname.text = vendor?.fname + " " + vendor?.lname
            Glide.with(context).load(vendor?.profileImgUrl).into(vendorImg)
        }.addOnFailureListener{
            println(it.localizedMessage)
        }
    }


}