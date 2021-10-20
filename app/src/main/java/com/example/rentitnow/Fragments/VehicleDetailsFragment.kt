package com.example.rentitnow.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_vehicle_details.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class VehicleDetailsFragment : Fragment() {
    lateinit var timer: Timer
    var vehicle: Vehicle? = null
   companion object {
       val VEHICLE = "vehicle"
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            vehicle = requireArguments().getParcelable<Vehicle>(VEHICLE)
            if(vehicle != null) {
                val inAnim: Animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
                val outAnim: Animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out)
                vehicleImageSwitcher?.setFactory{
                    val imgView = ImageView(requireContext())
                    imgView.scaleType = ImageView.ScaleType.FIT_CENTER
                    imgView.setPadding(8, 8, 8, 8)
                    imgView
                }
                vehicleImageSwitcher.setInAnimation(inAnim)
                vehicleImageSwitcher.setOutAnimation(outAnim)
                println(vehicle!!.imageUrls.size)
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    var position = 0
                    override fun run() {
                        if (position == vehicle!!.imageUrls.size) {
                            position = 0
                        }
                        println("Position: ${position}")
                        activity?.runOnUiThread{
                            Glide.with(context).load(vehicle!!.imageUrls[position]).into(vehicleImageSwitcher.currentView as ImageView)
                            position++
                        }
                    }
                }, 500, 5000)


                modelName.text = vehicle!!.manufacture + " " + vehicle!!.model
                fuelType.text = vehicle!!.fuelType
                transmissionType.text = vehicle!!.transmissionType
                vehicleDescription.text = vehicle!!.description
                fetchVendorName()

                bookingProceed.setOnClickListener {

                }
            }
        }

    }

    private fun fetchVendorName() {
        val database = FirebaseDatabase.getInstance()
        database.getReference("vendors").child(vehicle!!.vendorID).get().addOnSuccessListener {
            val firstname=it.child("fname").value.toString()
            val lastname=it.child("lname").value.toString()
            vendorName.text = firstname + " " + lastname
        }
            .addOnFailureListener{
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }


}