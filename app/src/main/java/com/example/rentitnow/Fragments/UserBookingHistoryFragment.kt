package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentitnow.Adapters.HeaderAdapter
import com.example.rentitnow.Adapters.UserBookingsAdapter
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.Data.BookingStatus
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

    var bookings = mutableListOf<List<Booking>>()
    var bookingIds = mutableListOf<List<String>>()
    var sections = mutableListOf<String>()
    var listUpcoming = mutableListOf<Booking>()
    var listCompleted = mutableListOf<Booking>()
    var listInProgress = mutableListOf<Booking>()
    var listUpcomingIds = mutableListOf<String>()
    var listCompletedIds = mutableListOf<String>()
    var listInProgressIds = mutableListOf<String>()
    lateinit var listener: ValueEventListener

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
            adapter = HeaderAdapter(bookings, bookingIds, sections, requireContext())
        }
        fetchBookings()
    }

    private fun fetchBookings() {
        val userId = auth.currentUser?.uid
        listener = database.getReference("bookings").orderByChild("userId").equalTo(userId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sections.clear()
                    bookings.clear()
                    bookingIds.clear()
                    listUpcoming.clear()
                    listUpcomingIds.clear()
                    listCompleted.clear()
                    listCompletedIds.clear()
                    listInProgress.clear()
                    listInProgressIds.clear()
                    val children = snapshot.children
                    println("Booking count: "+snapshot.children.count().toString())
                    children.forEach {
                        val booking = it.getValue(Booking::class.java)
                        val bookingId = it.key
                        if (booking != null && bookingId != null) {
//                            bookings.add(booking)
//                            bookingIds.add(bookingId)

                            if(booking.bookingStatus == BookingStatus.COMPLETED.type) {
                                listCompleted.add(booking)
                                listCompletedIds.add(bookingId)
                            }
                            else if(booking.bookingStatus == BookingStatus.IN_PROGRESS.type) {
                                listInProgress.add(booking)
                                listInProgressIds.add(bookingId)
                            }
                            else {
                                listUpcoming.add(booking)
                                listUpcomingIds.add(bookingId)
                            }
                        }
                    }
                    bookings.add(listInProgress)
                    bookingIds.add(listInProgressIds)
                    bookings.add(listUpcoming)
                    bookingIds.add(listUpcomingIds)
                    bookings.add(listCompleted)
                    bookingIds.add(listCompletedIds)
                    sections.add(BookingStatus.IN_PROGRESS.type)
                    sections.add(BookingStatus.UPCOMING.type)
                    sections.add(BookingStatus.COMPLETED.type)
                    println(bookings.size)
                    println(listCompleted.size)
                    println(listInProgress.size)
                    println(listUpcoming.size)
                    //bookingsRecyclerView.adapter = HeaderAdapter(bookings, bookingIds, sections, requireContext())
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