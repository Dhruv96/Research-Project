package com.example.rentitnow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentitnow.databinding.VehicleRecyclerviewItemBinding
import kotlinx.android.synthetic.main.fragment_user_profile.*

class VehicleAdapter(private val vehicles: List<Vehicle>, private val context: Context ) :
    RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    inner class VehicleViewHolder(private val cardCellBinding: VehicleRecyclerviewItemBinding) : RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bindVehicle(vehicle: Vehicle) {
            cardCellBinding.carNameTextView.text = vehicle.manufacture + " " + vehicle.model
            cardCellBinding.transmissionTypeTextView.text = vehicle.transmissionType
            cardCellBinding.pricePerDayTextView.text = "$" + vehicle.costPerDay.toString() + " per Day"
            Glide.with(context).load(vehicle.imageUrls[0]).into(cardCellBinding.carImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = VehicleRecyclerviewItemBinding.inflate(from, parent, false)
        return VehicleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
       holder.bindVehicle(vehicles[position])
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }
}