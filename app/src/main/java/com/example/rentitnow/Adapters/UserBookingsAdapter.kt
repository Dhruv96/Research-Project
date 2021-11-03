package com.example.rentitnow.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentitnow.Data.Booking
import com.example.rentitnow.Vehicle
import com.example.rentitnow.Vendor
import com.example.rentitnow.databinding.UserBookingRecyclerviewItemBinding
import com.example.rentitnow.databinding.VehicleListItemBinding
import com.google.firebase.database.FirebaseDatabase

class UserBookingsAdapter(private val bookings: List<Booking>, private val bookingids: List<String>
, private val context: Context
):
    RecyclerView.Adapter<UserBookingsAdapter.UserBookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBookingViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = UserBookingRecyclerviewItemBinding.inflate(from, parent, false)
        return UserBookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserBookingViewHolder, position: Int) {
        holder.bindBooking(bookings[position], bookingids[position])
    }

    override fun getItemCount(): Int {
        return bookings.size
    }


    inner class UserBookingViewHolder(private val cardCellBinding: UserBookingRecyclerviewItemBinding): RecyclerView.ViewHolder(cardCellBinding.root) {
        val database = FirebaseDatabase.getInstance()
        fun bindBooking(booking: Booking, bookingId: String) {
            cardCellBinding.textViewBookingId.text = bookingId.substring(0,10)
            cardCellBinding.TextViewStartType.text = booking.pickUpDate
            cardCellBinding.textViewEndDate.text = booking.returnDate
            cardCellBinding.pricePerDayTextView.text = booking.finalPrice.toString()
            fetchVehicleDetails(booking.vehicleId, cardCellBinding.carNameTextView, cardCellBinding.vehicleImageView)
            fetchVendorDetails(booking.vendorId, cardCellBinding.textViewVendorName)
        }

        private fun fetchVehicleDetails(vehicleId: String, vehicleName: TextView, imageView: ImageView) {
            database.getReference("vehicles").child(vehicleId).get().addOnSuccessListener {
                val vehicle = it.getValue(Vehicle::class.java)
                vehicleName.text = vehicle?.manufacture + " " + vehicle?.model
                Glide.with(context).load(vehicle?.imageUrls?.get(0)).into(imageView)
            }.addOnFailureListener{
                println(it.localizedMessage)
            }
        }

        private fun fetchVendorDetails(vendorId: String, vendorName: TextView) {
            database.getReference("vendors").child(vendorId).get().addOnSuccessListener {
                val vendor = it.getValue(Vendor::class.java)
                vendorName.text = vendor?.fname + " " + vendor?.lname
            }.addOnFailureListener{
                println(it.localizedMessage)
            }
        }
    }

}