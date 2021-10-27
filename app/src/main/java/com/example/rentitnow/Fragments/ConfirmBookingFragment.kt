package com.example.rentitnow.Fragments

import android.app.AlertDialog
import android.app.Application
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageSwitcher
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rentitnow.BuildConfig
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.Data.BookingStatus
import com.example.rentitnow.Data.PaymentStatus
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.R
import com.example.rentitnow.Vehicle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.OrderIntent.*
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.*
import kotlinx.android.synthetic.main.fragment_confirm_booking.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class ConfirmBookingFragment : Fragment() {
    private var finalVehiclePrice = 0.0
    private var finalVehiclePriceWithAddOns = 0.0
    private var pricePVRT = 0.0
    private var priceVLF = 0.0
    private var priceGST = 0.0
    private var pricePST = 0.0
    private var finalvehiclePriceWithTax = 0.0
    private val YOUR_CLIENT_ID = "AZex4sT9oBS9jI4pSYwvdoZyrhy6oe7h5rUDOEMlPhlT-I9yUHEfAWhG7PsH9ltqx3mvqmd9x-3WU6Ay"

    private lateinit var booking: Booking
    private lateinit var vehicleId: String
    private var selectedVehicle: Vehicle? = null
    private val database = FirebaseDatabase.getInstance().reference

    companion object {
        val VEHICLE = "vehicle"
        val PICKUP_DATE = "pickup_date"
        val RETURN_DATE = "return_date"
        val PickUpLoc="pickup_loc"
        val AddOnsString="addOns_string"
        val AddOnsPrice="addOns_price"
        val NoofDays="noOf_days"
        val VEHICLE_ID = "vehicle_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            selectedVehicle=requireArguments().getParcelable<Vehicle>(VEHICLE)
            vehicleId = requireArguments().getString(VEHICLE_ID).toString()
            booking = Booking(requireArguments().getString(AddOnsString).toString(), requireArguments().getString(AddOnsPrice)!!.toDouble(),
                    requireArguments().getString(PickUpLoc).toString(),PaymentStatus.PENDING.type,requireArguments().getString(PICKUP_DATE).toString(),
                    requireArguments().getString(RETURN_DATE).toString(), requireArguments().getString(NoofDays)!!.toInt(),0.0,BookingStatus.UPCOMING.type,
                     vehicleId, selectedVehicle!!.vendorID)

                finalVehiclePrice = selectedVehicle?.costPerDay?.toDouble()!! * booking?.noOfDays?.toDouble()!!
                finalVehiclePriceWithAddOns = selectedVehicle!!.costPerDay.toDouble() * booking.noOfDays.toDouble() + booking.addOnsPrice.toDouble()

                pricePVRT = 1.50 * booking.noOfDays.toDouble()
                priceVLF = 2.00 * booking.noOfDays.toDouble()
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

        buttonInformationTaxes.setOnClickListener{
            val mydialog = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val myView: View = inflater.inflate(R.layout.taxes_info_layout, null)
            mydialog.setView(myView)
            val dialog = mydialog.create()
            dialog.setCancelable(true)
            dialog.show()
        }

        val config = CheckoutConfig(
            application = requireActivity().application,
            clientId = YOUR_CLIENT_ID,
            environment = Environment.SANDBOX,
            returnUrl = "${BuildConfig.APPLICATION_ID}://paypalpay",
            currencyCode = CurrencyCode.CAD,
            userAction = UserAction.PAY_NOW,

            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)
        println("AMOUNT: ${finalPriceWithDiscount}")

        buttonPayPaypal.setup(
            createOrder = CreateOrder { createOrderActions ->
                val order = Order(
                    intent = CAPTURE,
                    appContext = AppContext(
                        userAction = UserAction.PAY_NOW
                    ),
                    purchaseUnitList = listOf(
                        PurchaseUnit(
                            amount = Amount(
                                currencyCode = CurrencyCode.CAD,
                                value = String.format("%.2f", finalPriceWithDiscount)
                            )
                        )
                    )
                )

                createOrderActions.create(order)
            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                    if(captureOrderResult is CaptureOrderResult.Success) {
                        println("Payment Succeeded")
                        // open success screen
                        booking.paymentStatus = PaymentStatus.PAID.type
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.CEILING
                        val priceUptoTwoDecimal = df.format(finalPriceWithDiscount)
                        booking.finalPrice = priceUptoTwoDecimal.toDouble()
                        val bookingID = UUID.randomUUID().toString()
                        database.child("bookings").child(bookingID).setValue(booking).addOnSuccessListener {
                            val bookingDone = BookingDoneFragment()
                            (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_user, bookingDone, "findThisFragment")
                                .addToBackStack(null)
                                .commit()
                        }
                            .addOnFailureListener{
                                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }

                    }
                    else if(captureOrderResult is CaptureOrderResult.Error) {
                        println("Payment Failed")
                        // show error
                    }
                }
            },

            onError = OnError { errorInfo ->
                Log.d("OnError", "Error: $errorInfo")
                println(errorInfo.reason)
            }

        )
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_booking, container, false)
    }


}
