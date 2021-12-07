package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentitnow.Helpers
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.example.rentitnow.VehicleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_published_vehicles.*
import kotlinx.android.synthetic.main.vehicle_recyclerview_item.*


class PublishedVehiclesFragment : Fragment() {
    var loader: KProgressHUD? = null
    var vehicles = mutableListOf<Vehicle>()
    var vehicleids = mutableListOf<String>()
    val database = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var listener: ValueEventListener

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
        loader = Helpers.getLoader(requireContext())
        vehiclesRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = VehicleAdapter(vehicles, requireActivity(), vehicleids)

        }
        fetchPublishedCars()
        vehiclesRecyclerView.adapter?.notifyDataSetChanged()


    }

    private fun fetchPublishedCars() {
        loader?.show()
        val vendorID = auth.currentUser!!.uid
        listener = database.getReference("vehicles").
        orderByChild("vendorID").equalTo(vendorID).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                vehicleids.clear()
                vehicles.clear()
                val children = snapshot.children
                println("count: "+snapshot.children.count().toString())
                children.forEach {
                    val vehicle = it.getValue(Vehicle::class.java)
                    val vehicleID = it.key
                    if (vehicle != null && vehicleID != null) {
                        vehicles.add(vehicle)
                        vehicleids.add(vehicleID)
                    }
                }
                println("Vehicles"+vehicles)
                loader?.dismiss()
                vehiclesRecyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onPause() {
        super.onPause()
        database.getReference("vehicles").removeEventListener(listener)
        println("Listener removed")
    }


}