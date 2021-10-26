package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentitnow.VehicleAdapterUserHome
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_car_list.*
import kotlinx.android.synthetic.main.fragment_published_vehicles.*


class CarListFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var vehicles = mutableListOf<Vehicle>()
    var vehicleIds = mutableListOf<String>()

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
            val pickupDate = requireArguments().getString(PICKUP_DATE)
            val returnDate = requireArguments().getString(RETURN_DATE)
            val pickuploc = requireArguments().getString(pickUpLocation)
            val noofdays = requireArguments().getString(NoofDays)
            textViewPickUpDate.text = pickupDate
            textViewReturnDate.text = returnDate
            recyclerViewVehicles.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = VehicleAdapterUserHome(vehicles, vehicleIds, requireActivity(), pickupDate, returnDate,pickuploc,noofdays)
            }
            fetchVehicles()
        }

    }

    private fun fetchVehicles() {
        database.getReference("vehicles").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                children.forEach {
                    val vehicle = it.getValue(Vehicle::class.java)
                    val vehicleId = it.key
                    if (vehicle != null && vehicleId != null) {
                        vehicles.add(vehicle)
                        vehicleIds.add(vehicleId)
                    }
                }

                recyclerViewVehicles.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


}