package com.example.rentitnow.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rentitnow.Adapters.VendorCurrentBookingsAdapter
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.R
import com.example.rentitnow.User
import com.example.rentitnow.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_car_list.*
import kotlinx.android.synthetic.main.fragment_published_vehicles.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_vendor_home.*
import java.text.SimpleDateFormat


class VendorHomeFragment : Fragment() {
    var bookings = mutableListOf<Booking>()
    var bookingIds = mutableListOf<String>()
    val database = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var listener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vendor_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewBookings.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = VendorCurrentBookingsAdapter(bookings,bookingIds, requireActivity())

        }
        fetchBookingDetails()
        recyclerViewBookings.adapter?.notifyDataSetChanged()

    }

    companion object {

    }
    private fun fetchBookingDetails() {
        val vendorID = auth.currentUser!!.uid
        println("Vendor Id"+vendorID)
        listener = database.getReference("bookings").
        orderByChild("vendorId").equalTo(vendorID).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookings.clear()
                bookingIds.clear()
                val children = snapshot.children
                println("Booking count: "+snapshot.children.count().toString())
                children.forEach {
                    val booking = it.getValue(Booking::class.java)
                    val bookingId = it.key
                    if (booking?.bookingStatus.equals("upcoming")){
                        if (booking != null && bookingId != null) {
                            bookings.add(booking)
                            bookingIds.add(bookingId)

                        }else{
                            textViewNoReservations.setText("No Bookings Found")
                        }
                    }
                }

                println("Bookings "+bookings)

                recyclerViewBookings.adapter?.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }



    override fun onStop() {
        super.onStop()
        database.getReference("bookings").removeEventListener(listener)
    }
}