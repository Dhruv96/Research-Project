package com.example.rentitnow.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.Data.Rating
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.NavigationActivityVendor
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_user_rating.*


class RatingFragment : Fragment() {

    lateinit var booking: Booking
    lateinit var bookingId: String
    var userRating: Rating? = null
    val database = FirebaseDatabase.getInstance()
    private lateinit var pref: SharedPreferences

    companion object
    {
        val BOOKING = "booking"
        val BOOKING_ID = "booking_id"
        val USER_RATING = "user_rating"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_user_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("logged_in", 0)
        pref.getInt("vendorLoggedIn", 0)

        if(arguments != null) {
            booking = requireArguments().getSerializable(BOOKING) as Booking
            bookingId = requireArguments().getSerializable(BOOKING_ID) as String
            userRating = requireArguments().getSerializable(USER_RATING) as? Rating
            if(userRating != null) {
                userFeedback.setText(userRating!!.feedback)
                rating.rating = userRating!!.rating
            }
            fetchCarDetails()
        }

        submit_Button.setOnClickListener {
            val feedback = userFeedback.text.toString().trim()
            val rating = rating.rating
            if (pref.getInt("userLoggedIn", 4).equals(0)||pref.getInt("userLoggedIn", 0).equals(1)||pref.getInt("userLoggedIn", 0).equals(2)) {
                val userRating =
                    Rating(booking.userId, booking.vendorId, bookingId, rating, feedback)
                database.getReference("UserRatings").child(bookingId).setValue(userRating)
                    .addOnSuccessListener {
                        (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container_user,
                                PostFeedbackFragment(),
                                "findThisFragment"
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
            }else if (pref.getInt("vendorLoggedIn", 4).equals(0)||pref.getInt("vendorLoggedIn", 0).equals(1)||pref.getInt("vendorLoggedIn", 0).equals(2)){
                val vendorRating =
                    Rating(booking.userId, booking.vendorId, bookingId, rating, feedback)
                database.getReference("VendorRatings").child(bookingId).setValue(vendorRating)
                    .addOnSuccessListener {
                        (context as NavigationActivityVendor).supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container_vendor,
                                PostFeedbackFragment(),
                                "findThisFragment"
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun fetchCarDetails() {
        println("Fetching car details: ${booking.vehicleId}}")
        database.getReference("vehicles").child(booking.vehicleId).get().addOnSuccessListener {
            val vehicle = it.getValue(Vehicle::class.java)
            Glide.with(context).load(vehicle?.imageUrls?.get(0)).into(bookingImg)
        }
    }


}