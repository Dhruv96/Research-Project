package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.VehicleAdapterUserHome
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.example.rentitnow.Vendor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_car_list.*
import kotlinx.android.synthetic.main.fragment_published_vehicles.*
import java.text.SimpleDateFormat


class CarListFragment : Fragment() {
    lateinit var pickupLoc: String
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var vehicles = mutableListOf<Vehicle>()
    var vehicleIds = mutableListOf<String>()
    var bookings = mutableListOf<Booking>()
    var vendors = mutableListOf<Vendor>()
    var vendorIds = mutableListOf<String>()
    var vehiclesTobeDeleted = mutableListOf<Vehicle>()
    var vehicleIdsTobeDeleted = mutableListOf<String>()
    lateinit var pickupDate: String
    lateinit var returnDate: String
    lateinit var listener1: ValueEventListener
    lateinit var listener2: ValueEventListener

    companion object {
        val pickUpLocation="pickup_loc"
        val PICKUP_DATE = "pickupDate"
        val RETURN_DATE = "returnDate"
        val NoofDays="noOf_days"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            pickupDate = requireArguments().getString(PICKUP_DATE).toString()
            returnDate = requireArguments().getString(RETURN_DATE).toString()
            println("PICKUP DATE: ${pickupDate}")
            pickupLoc = requireArguments().getString(pickUpLocation).toString()
            val noofdays = requireArguments().getString(NoofDays)
            textViewPickUpDate.text = pickupDate
            textViewReturnDate.text = returnDate
            recyclerViewVehicles.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = VehicleAdapterUserHome(vehicles, vehicleIds, requireActivity(), pickupDate, returnDate,pickupLoc,noofdays)
            }
            println("Inside onviewcreated")
            fetchVendors()
        }

    }

    private fun fetchVendors() {
        database.getReference("vendors").orderByChild("city").equalTo(pickupLoc).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                vendorIds.clear()
                vendors.clear()
                val vendorsList = snapshot.children
                vendorsList.forEach {
                    val vendorObj = it.getValue(Vendor::class.java)
                    val vendor_id = it.key
                    if (vendorObj != null && vendor_id != null) {
                        vendors.add(vendorObj)
                        vendorIds.add(vendor_id)
                    }
                }

                fetchVehicles()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun fetchVehicles() {
        listener1 =database.getReference("vehicles").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("VEHICLES CLEARED")
                vehicles.clear()
                vehicleIds.clear()
                val children = snapshot.children
                children.forEach {
                    val vehicle = it.getValue(Vehicle::class.java)
                    val vehicleId = it.key
                    if (vehicle != null && vehicleId != null) {
                        vehicles.add(vehicle)
                        vehicleIds.add(vehicleId)
                    }
                }
                fetchBookings()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun fetchBookings() {
        listener2 = database.getReference("bookings").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("BOOKINGS CLEARED")
                bookings.clear()
                val children = snapshot.children
                children.forEach {
                    val booking = it.getValue(Booking::class.java)
                    if (booking != null) {
                        bookings.add(booking)
                    }
                }
                filterVehicles()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun filterVehicles() {
        bookings.forEach { booking ->
               val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
               val pDate = dateFormatter.parse(pickupDate)
               val rDate = dateFormatter.parse(returnDate)
               val bookingPDate = dateFormatter.parse(booking.pickUpDate)
               val bookingRDate = dateFormatter.parse(booking.returnDate)


               if(!((pDate.compareTo(bookingRDate) > 0) || (rDate.compareTo(bookingPDate) < 0))){
                   // Hide vehicle from user
                   val index = vehicleIds.indexOf(booking.vehicleId)
                   if(index != -1) {
                       vehicleIds.removeAt(index)
                       vehicles.removeAt(index)
                   }
               }

           }

        vehicles.forEach {
            if(vendorIds.contains(it.vendorID)) {
                return@forEach
            }
            else {
                val index = vehicles.indexOf(it)
                vehicleIdsTobeDeleted.add(vehicleIds[index])
                vehiclesTobeDeleted.add(it)
            }
        }
        vehicleIds.removeAll(vehicleIdsTobeDeleted)
        vehicles.removeAll(vehiclesTobeDeleted)
        recyclerViewVehicles.adapter?.notifyDataSetChanged()
        }


    override fun onStop() {
        super.onStop()
        println("STOPPED")
        database.getReference("vehicles").removeEventListener(listener1)
        database.getReference("bookings").removeEventListener(listener2)
        println("Listener removed from car list")
    }
    }


