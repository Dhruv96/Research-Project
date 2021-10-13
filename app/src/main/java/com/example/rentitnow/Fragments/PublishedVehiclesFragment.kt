package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.example.rentitnow.VehicleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_published_vehicles.*


class PublishedVehiclesFragment : Fragment() {
    var vehicles = mutableListOf<Vehicle>()
    val database = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_published_vehicles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehiclesRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = VehicleAdapter(vehicles, requireActivity())
        }
       fetchPublishedCars()
    }

    private fun fetchPublishedCars() {
        val vendorID = auth.currentUser!!.uid
        database.getReference("vehicles").
        orderByChild("vendorID").equalTo(vendorID).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                println("count: "+snapshot.children.count().toString())
                children.forEach {
                    val vehicle = it.getValue(Vehicle::class.java)
                    if (vehicle != null) {
                        vehicles.add(vehicle)
                    }
                }

                vehiclesRecyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}