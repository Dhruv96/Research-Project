package com.example.rentitnow.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.Data.BookingStatus
import com.example.rentitnow.Data.PaymentStatus
import com.example.rentitnow.R
import com.example.rentitnow.User
import com.example.rentitnow.Vehicle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_vendor_booking_details.*


class VendorBookingDetailsFragment : Fragment() {
    var booking: Booking? = null
    lateinit var bookingId: String
    val auth = Firebase.auth
    var vehicle: Vehicle? = null
    var user: User? = null
    val database = FirebaseDatabase.getInstance()
    companion object {
        val BOOKING = "booking"
        val BOOKINGID = "bookingid"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_booking_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            booking = requireArguments().getParcelable<Booking>(BOOKING)
            bookingId = requireArguments().getString(BOOKINGID).toString()
            textViewBookingId.text = bookingId.substring(0,10)
            TextViewStartDate.text = booking?.pickUpDate
            textViewEndDate.text = booking?.returnDate
            finalpriceTextView.text = "$" + booking?.finalPrice.toString()
            fetchVehicleDetails(booking?.vehicleId.toString(), carNameTextView, vehicleImageView)
            fetchUserDetails(booking?.userId.toString(), textViewUserName)
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Action")
            if (booking?.paymentStatus==PaymentStatus.PENDING.type){
                buttonIssueCar.text="Take Payement"

            }
            if (booking?.paymentStatus==PaymentStatus.PAID.type&& booking?.bookingStatus==BookingStatus.UPCOMING.type) {
                buttonIssueCar.text = "Issue Car"
                }

            if (booking?.bookingStatus==BookingStatus.IN_PROGRESS.type) {

                    buttonIssueCar.text = "Process Return"

                }

            buttonIssueCar.setOnClickListener {
                if (buttonIssueCar.text.equals("Take Payement")) {

                    builder.setMessage("Please Use POS/collect cash of " + "$" + booking?.finalPrice.toString())
                    builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                        buttonIssueCar.text = "Issue Car"
                        booking?.paymentStatus = PaymentStatus.PAID.type
                        database.getReference("bookings").child(bookingId).setValue(booking)
                        Toast.makeText(context, "Payement Completed", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }

                    builder.show()

                } else if (buttonIssueCar.text.equals("Issue Car")) {

                    builder.setMessage("Please give Addons " + booking?.addOnsString + " to Customer")
                    builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                        buttonIssueCar.text = "Process Return"
                        booking?.bookingStatus = BookingStatus.IN_PROGRESS.type
                        database.getReference("bookings").child(bookingId).setValue(booking)
                        Toast.makeText(context, "Car Issued", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }

                    builder.show()

                } else if (buttonIssueCar.text.equals("Process Return")) {

                    builder.setMessage("Please check if vehicle has any scratches or any damages!")
                    builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                        buttonIssueCar.visibility = View.INVISIBLE
                        booking?.bookingStatus = BookingStatus.COMPLETED.type
                        database.getReference("bookings").child(bookingId).setValue(booking)
                        Toast.makeText(context, "Car Returned", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }

                    builder.show()

                }
            }


            


        }

    }
            fun fetchVehicleDetails(vehicleId: String, vehicleName: TextView, imageView: ImageView) {
                database.getReference("vehicles").child(vehicleId).get().addOnSuccessListener {
                    vehicle = it.getValue(Vehicle::class.java)
                    vehicleName.text = vehicle?.manufacture + " " + vehicle?.model
                    Glide.with(context).load(vehicle?.imageUrls?.get(0)).into(imageView)
                }.addOnFailureListener{
                    println(it.localizedMessage)
                }
            }
            fun fetchUserDetails(userId: String, userName: TextView) {
                database.getReference("users").child(userId).get().addOnSuccessListener {
                    user = it.getValue(User::class.java)
                    userName.text = user?.fname + " " + user?.lname
                }.addOnFailureListener{
                    println(it.localizedMessage)
                }
            }




}