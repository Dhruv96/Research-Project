package com.example.rentitnow.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import kotlinx.android.synthetic.main.fragment_confirm_booking.*


class ConfirmBookingFragment : Fragment() {
    private var finalVehiclePrice = 0.0
    private var finalVehiclePriceWithAddOns = 0.0
    private var pricePVRT = 0.0
    private var priceVLF = 0.0
    private var priceGST = 0.0
    private var pricePST = 0.0
    private var finalvehiclePriceWithTax = 0.0

    private lateinit var booking: Booking
    private var selectedVehicle: Vehicle? = null

    companion object {
        val VEHICLE = "vehicle"
        val PICKUP_DATE = "pickup_date"
        val RETURN_DATE = "return_date"
        val PickUpLoc="pickup_loc"
        val AddOnsString="addOns_string"
        val AddOnsPrice="addOns_price"
        val NoofDays="noOf_days"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            selectedVehicle=requireArguments().getParcelable<Vehicle>(VEHICLE)
            booking = Booking(requireArguments().getString(AddOnsString).toString(), requireArguments().getString(AddOnsPrice)!!.toFloat(),
                    requireArguments().getString(PickUpLoc).toString(),"",requireArguments().getString(PICKUP_DATE).toString(),
                    requireArguments().getString(PICKUP_DATE).toString(), requireArguments().getString(NoofDays)!!.toInt(),0.0f,"",
                     "", selectedVehicle!!.vendorID)
                finalVehiclePrice = selectedVehicle?.costPerDay?.toDouble()!! * booking?.noOfDays?.toDouble()!!
                finalVehiclePriceWithAddOns = selectedVehicle!!.costPerDay.toDouble() * booking.noOfDays.toDouble() + booking.addOnsPrice.toDouble()

                pricePVRT = 1.50 * booking.noOfDays.toDouble()
                priceVLF = 1.07 * booking.noOfDays.toDouble()
                priceGST = 0.05 * finalVehiclePriceWithAddOns
                pricePST = 0.07 * finalVehiclePriceWithAddOns

                finalvehiclePriceWithTax = finalVehiclePriceWithAddOns + pricePVRT + priceVLF + priceGST + pricePST

                booking.finalPrice(String.format("%.2f", finalvehiclePriceWithTax))

        }

        //Set values in recipt
        textViewTitleCarPrice.setText(String.format("%s %s for %s day(s)", selectedVehicle?.manufacture, selectedVehicle?.model, booking?.noOfDays))
        textViewCarPrice.setText(String.format("CAD %s", finalVehiclePrice))

        textViewTitleAddOns.setText(booking?.addOnsString)
        textViewAddOns.setText(java.lang.String.format("CAD %s", booking?.addOnsPrice))

        textViewGST.setText(String.format("CAD %.2f", priceGST))
        textViewPST.setText(String.format("CAD %.2f", pricePST))
        textViewPVRT.setText(String.format("CAD %.2f", pricePVRT))
        textViewVLC.setText(String.format("CAD %.2f", priceVLF))

        textViewFinalPrice.setText(String.format("CAD %.2f", finalvehiclePriceWithTax))

        val finalPriceWithDiscount = finalvehiclePriceWithTax - finalvehiclePriceWithTax * 0.05

        textViewPaypalPrice.setText(String.format("CAD %.2f", finalPriceWithDiscount))
        textViewPickupPrice.setText(String.format("CAD %.2f", finalvehiclePriceWithTax))

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_booking, container, false)
    }


}