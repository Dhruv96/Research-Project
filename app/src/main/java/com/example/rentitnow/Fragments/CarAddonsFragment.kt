package com.example.rentitnow.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import kotlinx.android.synthetic.main.fragment_car_addons.*
import java.text.SimpleDateFormat
import java.util.*


class CarAddonsFragment : Fragment() {
     var stringSelectedAddOns = ""
     var addOnPrice = 0
    var vehicle: Vehicle? = null
    lateinit var pickupDate: String
    lateinit var returnDate: String
    lateinit var pickUploc: String
    lateinit var noofDays: String
    companion object {
        val VEHICLE = "vehicle"
        val PICKUP_DATE = "pickup_date"
        val RETURN_DATE = "return_date"
        val PickUpLoc="pickup_loc"
        val NoofDays="noOf_days"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            vehicle = requireArguments().getParcelable<Vehicle>(VehicleDetailsFragment.VEHICLE)
            pickupDate = requireArguments().getString(VehicleDetailsFragment.PICKUP_DATE).toString()
            returnDate = requireArguments().getString(VehicleDetailsFragment.RETURN_DATE).toString()
            pickUploc=requireArguments().getString(VehicleDetailsFragment.PickUpLoc).toString()
            noofDays=requireArguments().getString(VehicleDetailsFragment.NoofDays).toString()

            buttonBooking.setOnClickListener(View.OnClickListener {
                calculateAddOns()
                val bundle = Bundle()
                val vehicleDetails = ConfirmBookingFragment()
                bundle.putParcelable(ConfirmBookingFragment.VEHICLE, vehicle)
                bundle.putString(ConfirmBookingFragment.PICKUP_DATE, pickupDate)
                bundle.putString(ConfirmBookingFragment.RETURN_DATE, returnDate)
                bundle.putString(ConfirmBookingFragment.PickUpLoc, pickUploc)
                bundle.putString(ConfirmBookingFragment.AddOnsString, stringSelectedAddOns)
                bundle.putString(ConfirmBookingFragment.AddOnsPrice, addOnPrice.toString())
                bundle.putString(ConfirmBookingFragment.NoofDays, noofDays)
                vehicleDetails.arguments = bundle
                Log.d("addons", bundle.toString())
                (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_user, vehicleDetails, "findThisFragment")
                        .addToBackStack(null)
                        .commit()

            })
        }
    }

    private fun calculateAddOns() {
        if (checkBoxAddionalKey.isChecked || checkBoxChildSeat.isChecked || checkBoxPorD.isChecked) {
            if (checkBoxAddionalKey.isChecked) {
                stringSelectedAddOns += "Additional Key"
                addOnPrice += 5
            }
            if (checkBoxChildSeat.isChecked) {
                stringSelectedAddOns += "| Child Seat"
                addOnPrice += 50
            }
            if (checkBoxPorD.isChecked) {
                stringSelectedAddOns += "| Petrol or Diesel"
                addOnPrice += 50
            }
        } else {
            stringSelectedAddOns = "None"
            addOnPrice = 0
        }
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_addons, container, false)
    }


}