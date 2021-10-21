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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CarListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CarListFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var vehicles = mutableListOf<Vehicle>()

    companion object {
        val PICKUP_DATE = "pickupDate"
        val RETURN_DATE = "returnDate"
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
            textViewPickUpDate.text = pickupDate
            textViewReturnDate.text = returnDate
            recyclerViewVehicles.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = VehicleAdapterUserHome(vehicles, requireActivity(), pickupDate, returnDate)
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
                    if (vehicle != null) {
                        vehicles.add(vehicle)
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