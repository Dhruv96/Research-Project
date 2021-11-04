package com.example.rentitnow.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.Fragments.VendorBookingDetailsFragment
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.R
import com.example.rentitnow.User
import com.example.rentitnow.Vehicle
import com.example.rentitnow.databinding.VendorCurrentBookingsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class VendorCurrentBookingsAdapter(
    private val bookings: MutableList<Booking>,
    private val bookingIds: MutableList<String>,
    private val context: Context
) :
    RecyclerView.Adapter<VendorCurrentBookingsAdapter.CurrentBookingsHolder>() {
    val auth = Firebase.auth
    var vehicle: Vehicle? = null
    var user: User? = null

    inner class CurrentBookingsHolder(private val cardCellBinding: VendorCurrentBookingsBinding) : RecyclerView.ViewHolder(
        cardCellBinding.root
    ) {
        val database = FirebaseDatabase.getInstance()
        fun bindVehicle(booking:Booking,bookingId:String) {
            cardCellBinding.textViewBookingId.text= bookingId.substring(0,10)
            cardCellBinding.TextViewStartDate.text = booking.pickUpDate
            cardCellBinding.textViewEndDate.text = booking.returnDate
            cardCellBinding.finalpriceTextView.text = "$" + booking.finalPrice.toString()
            fetchVehicleDetails(booking.vehicleId, cardCellBinding.carNameTextView, cardCellBinding.vehicleImageView)
            fetchUserDetails(booking.userId, cardCellBinding.textViewUserName)

        }
        private fun fetchVehicleDetails(vehicleId: String, vehicleName: TextView, imageView: ImageView) {
            database.getReference("vehicles").child(vehicleId).get().addOnSuccessListener {
                vehicle = it.getValue(Vehicle::class.java)
                vehicleName.text = vehicle?.manufacture + " " + vehicle?.model
                Glide.with(context).load(vehicle?.imageUrls?.get(0)).into(imageView)
            }.addOnFailureListener{
                println(it.localizedMessage)
            }
        }
        private fun fetchUserDetails(userId: String, userName: TextView) {
            database.getReference("users").child(userId).get().addOnSuccessListener {
                user = it.getValue(User::class.java)
                userName.text = user?.fname + " " + user?.lname
            }.addOnFailureListener{
                println(it.localizedMessage)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentBookingsHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = VendorCurrentBookingsBinding.inflate(from, parent, false)
        return CurrentBookingsHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrentBookingsHolder, position: Int) {
        holder.bindVehicle(bookings[position], bookingIds[position])
        holder.itemView.setOnClickListener({
            val bundle = Bundle()
            val vendorBookingsDetails = VendorBookingDetailsFragment()
            bundle.putParcelable(VendorBookingDetailsFragment.BOOKING,bookings.get(position))
            bundle.putString(VendorBookingDetailsFragment.BOOKING,bookingIds.get(position))
            vendorBookingsDetails.arguments = bundle
            (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_user, vendorBookingsDetails, "findThisFragment")
                .addToBackStack(null)
                .commit()

        })
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

}