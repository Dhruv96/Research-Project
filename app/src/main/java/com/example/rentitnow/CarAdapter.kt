package com.example.rentitnow

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentitnow.databinding.VehicleRecyclerviewItemBinding

class CarAdapter(private val vehicles: List<Vehicle>,private var pickUpDate: String?,private  var returnDate:String?, private val context: Context): RecyclerView.Adapter<CarAdapter.VehicleViewHolder>() {
    private val vehicleList: List<Vehicle>? = null


    inner class VehicleViewHolder(private val cardCellBinding: VehicleRecyclerviewItemBinding) : RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bindVehicle(vehicle: Vehicle) {
            cardCellBinding.carNameTextView.text = vehicle.manufacture + " " + vehicle.model
            cardCellBinding.transmissionTypeTextView.text = vehicle.transmissionType
            cardCellBinding.pricePerDayTextView.text = "$" + vehicle.costPerDay.toString() + " per Day"
            Glide.with(context).load(vehicle.imageUrls[0]).into(cardCellBinding.carImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.VehicleViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = VehicleRecyclerviewItemBinding.inflate(from, parent, false)
        return VehicleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    override fun onBindViewHolder(holder: CarAdapter.VehicleViewHolder, position: Int) {
        holder.bindVehicle(vehicles[position])

    }
}