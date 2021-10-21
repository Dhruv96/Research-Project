package com.example.rentitnow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentitnow.Fragments.VehicleDetailsFragment
import com.example.rentitnow.databinding.VehicleListItemBinding
import com.example.rentitnow.databinding.VehicleRecyclerviewItemBinding

class VehicleAdapterUserHome(private val vehicles: List<Vehicle>, private val context: Context,
         private val pickupDate: String?, private val returnDate: String?): RecyclerView.Adapter<VehicleAdapterUserHome.VehicleViewHolder>() {

    inner class VehicleViewHolder(private val cardCellBinding: VehicleListItemBinding) : RecyclerView.ViewHolder(cardCellBinding.root) {
        fun bindVehicle(vehicle: Vehicle) {
            cardCellBinding.carNameTextView.text = vehicle.manufacture + " " + vehicle.model
            cardCellBinding.transmissionTypeTextView.text = vehicle.transmissionType
            cardCellBinding.pricePerDayTextView.text = "$" + vehicle.costPerDay.toString() + " per Day"
            Glide.with(context).load(vehicle.imageUrls[0]).into(cardCellBinding.carImageView)

            cardCellBinding.buttonView.setOnClickListener {
                val bundle = Bundle()
                val vehicleDetails = VehicleDetailsFragment()
                bundle.putParcelable(VehicleDetailsFragment.VEHICLE, vehicles.get(adapterPosition))
                bundle.putString(VehicleDetailsFragment.PICKUP_DATE, pickupDate)
                bundle.putString(VehicleDetailsFragment.RETURN_DATE, returnDate)
                vehicleDetails.arguments = bundle
                (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_user, vehicleDetails, "findThisFragment")
                        .addToBackStack(null)
                        .commit()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleAdapterUserHome.VehicleViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = VehicleListItemBinding.inflate(from, parent, false)
        return VehicleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    override fun onBindViewHolder(holder: VehicleAdapterUserHome.VehicleViewHolder, position: Int) {
        holder.bindVehicle(vehicles[position])
    }
}