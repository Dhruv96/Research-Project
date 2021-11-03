package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentitnow.Adapters.UserBookingsAdapter
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_user_booking_history.*
import kotlinx.android.synthetic.main.fragment_vendor_home.*


class UserBookingHistoryFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()
    var bookings = mutableListOf<Booking>()
    var bookingIds = mutableListOf<String>()
    lateinit var listener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_booking_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = UserBookingsAdapter(bookings, bookingIds, requireContext())
        }

        fetchBookings()
    }

    private fun fetchBookings() {
        val userId = auth.currentUser?.uid
        listener = database.getReference("bookings").orderByChild("userId").equalTo(userId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookings.clear()
                    bookingIds.clear()
                    val children = snapshot.children
                    println("Booking count: "+snapshot.children.count().toString())
                    children.forEach {
                        val booking = it.getValue(Booking::class.java)
                        val bookingId = it.key
                        if (booking != null && bookingId != null) {
                            bookings.add(booking)
                            bookingIds.add(bookingId)
                        }
                    }
                    bookingsRecyclerView.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun onPause() {
        super.onPause()
        database.getReference("bookings").removeEventListener(listener)
        println("Listener removed")
    }

}