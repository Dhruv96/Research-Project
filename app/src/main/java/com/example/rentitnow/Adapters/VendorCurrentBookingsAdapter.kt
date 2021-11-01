package com.example.rentitnow.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentitnow.Data.Booking
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
    val databaseRef = FirebaseDatabase.getInstance().reference

    inner class CurrentBookingsHolder(private val cardCellBinding: VendorCurrentBookingsBinding) : RecyclerView.ViewHolder(
        cardCellBinding.root
    ) {
        fun bindVehicle(booking:Booking) {
            cardCellBinding.textViewBookingId.text= bookingIds.get(adapterPosition).substring(0,10)
            cardCellBinding.carNameTextView.text = booking.manufacture + " " + booking.model
            cardCellBinding.TextViewStartType.text = booking.pickUpDate
            cardCellBinding.textViewEndDate.text = booking.returnDate
            cardCellBinding.textViewUserName.text=booking.userFname
            cardCellBinding.pricePerDayTextView.text = "$" + booking.finalPrice.toString()
            Glide.with(context).load(booking.imageUrls[0]).into(cardCellBinding.carImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentBookingsHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = VendorCurrentBookingsBinding.inflate(from, parent, false)
        return CurrentBookingsHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrentBookingsHolder, position: Int) {
        holder.bindVehicle(bookings[position])
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

}