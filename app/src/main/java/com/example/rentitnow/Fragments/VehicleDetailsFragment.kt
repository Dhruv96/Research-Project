package com.example.rentitnow.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.example.rentitnow.Vendor
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_vehicle_details.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class VehicleDetailsFragment : Fragment() {
    lateinit var timer: Timer
    var vehicle: Vehicle? = null
    lateinit var pickupDate: String
    lateinit var returnDate: String
    lateinit var pickUploc: String
    lateinit var noofDays: String
    lateinit var vehicleId: String

   companion object {
       val VEHICLE = "vehicle"
       val VEHICLE_ID = "vehicle_id"
       val PICKUP_DATE = "pickup_date"
       val RETURN_DATE = "return_date"
       val PickUpLoc="pickup_loc"
       val NoofDays="noOf_days"
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
            vehicleId = requireArguments().getString(VEHICLE_ID).toString()
            pickupDate = requireArguments().getString(PICKUP_DATE).toString()
            returnDate = requireArguments().getString(RETURN_DATE).toString()
            pickUploc=requireArguments().getString(PickUpLoc).toString()
            noofDays=requireArguments().getString(NoofDays).toString()
            println("VEHICLE ID: ${vehicleId}")

            if(vehicle != null) {
                vehicleImageSwitcher?.setFactory{
                    val imgView = ImageView(requireContext())
                    imgView.scaleType = ImageView.ScaleType.FIT_CENTER
                    imgView.setPadding(8, 8, 8, 8)
                    imgView
                }

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
                            Glide.with(context)
                                    .load(vehicle!!.imageUrls[position])
                                    .transition(DrawableTransitionOptions.withCrossFade(1500))
                                    .into(vehicleImageSwitcher.currentView as ImageView)
                            position++
                        }
                    }
                }, 500, 5000)


                modelName.text = vehicle!!.manufacture + " " + vehicle!!.model
                fuelType.text = vehicle!!.fuelType
                transmissionType.text = vehicle!!.transmissionType
                vehicleDescription.text = vehicle!!.description
                pricePerday.text= vehicle!!.costPerDay.toString()
                fetchVendorName()

                selectAddons.setOnClickListener {
                    val bundle = Bundle()
                    val vehicleDetails = CarAddonsFragment()
                    bundle.putParcelable(CarAddonsFragment.VEHICLE,vehicle)
                    bundle.putString(CarAddonsFragment.PICKUP_DATE, pickupDate)
                    bundle.putString(CarAddonsFragment.RETURN_DATE, returnDate)
                    bundle.putString(CarAddonsFragment.PickUpLoc, pickUploc)
                    bundle.putString(CarAddonsFragment.NoofDays, noofDays)
                    bundle.putString(CarAddonsFragment.VEHICLE_ID, vehicleId)
                    vehicleDetails.arguments = bundle
                    (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_user, vehicleDetails, "findThisFragment")
                            .addToBackStack(null)
                            .commit()
                }
            }
        }

    }

    private fun fetchVendorName() {
        val database = FirebaseDatabase.getInstance()
        database.getReference("vendors").child(vehicle!!.vendorID).get().addOnSuccessListener {
            val vendor = it.getValue(Vendor::class.java)
            val firstname= vendor?.fname
            val lastname= vendor?.lname
            vendorName.text = firstname + " " + lastname
            vendorName.setOnClickListener {
                val vendorDetailsFragment = VendorDetailsFragment()
                val bundle = Bundle()
                bundle.putSerializable(VendorDetailsFragment.VENDOR,vendor)
                vendorDetailsFragment.arguments = bundle
                (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_user, vendorDetailsFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit()
            }
        }
            .addOnFailureListener{
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }


    override fun onStop() {
        super.onStop()
        timer.cancel()
    }


}