package com.example.rentitnow

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentitnow.databinding.VehicleRecyclerviewItemBinding
import com.facebook.FacebookSdk
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_publish_car.view.*
import kotlinx.android.synthetic.main.vehicle_recyclerview_item.view.*


class VehicleAdapter(private val vehicles: MutableList<Vehicle>, private val context: Context, private val vehicleIds: MutableList<String>) :
    RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    val auth = Firebase.auth
    val databaseRef = FirebaseDatabase.getInstance().reference

    inner class VehicleViewHolder(private val cardCellBinding: VehicleRecyclerviewItemBinding) : RecyclerView.ViewHolder(
            cardCellBinding.root
    ) {
        fun bindVehicle(vehicle: Vehicle) {
            cardCellBinding.carNameTextView.text = vehicle.manufacture + " " + vehicle.model
            cardCellBinding.TextViewStartType.text = vehicle.transmissionType
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
        val vehicledata: Vehicle = vehicles.get(position)

        holder.itemView.editButton.setOnClickListener{
            updateCarDetails(vehicledata, holder)
            notifyDataSetChanged()

        }

        holder.itemView.deleteButton.setOnClickListener{

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Action")
            builder.setMessage("Are you sure, you want to delete this vehicle ?")
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                databaseRef.child("vehicles").child(vehicleIds.get(holder.adapterPosition)).removeValue()
                Toast.makeText(context, "Car Deleted successfully!", Toast.LENGTH_SHORT).show()
                vehicles.removeAt(holder.adapterPosition)
                Log.d("delete",vehicledata.toString())
                notifyDataSetChanged()
                dialog.dismiss()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
               dialog.dismiss()
            }

            builder.show()
            Log.d("delete",vehicledata.toString())

        }

    }

    private fun updateCarDetails(vehicledata: Vehicle, holder: VehicleAdapter.VehicleViewHolder) {
        val progressDialog = ProgressDialog(context)
        lateinit var carsImageSwitcher: ImageSwitcher
        var position = 0
        val mydialog = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val myView: View = inflater.inflate(R.layout.fragment_publish_car, null)
        mydialog.setView(myView)
        val dialog = mydialog.create()
        val vehicleTypes = listOf(VehicleType.CAR.type, VehicleType.TRUCK.type)
        val fuelTypes = listOf(FuelType.PETROL.type, FuelType.DIESEL.type)
        val transmissionTypes = listOf(TransmissionType.AUTOMATIC.type, TransmissionType.MANUAL.type)
        val adapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, vehicleTypes)
        val adapter2 = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, fuelTypes)
        val adapter3 = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, transmissionTypes)
        myView.spinnerItemType.adapter = adapter
        myView.spinnerFuelType.adapter = adapter2
        myView.spinnerTransmissionType.adapter = adapter3
        myView.publishButton.text = "UPDATE"
        if (vehicledata.type == VehicleType.CAR.type) {
            myView.spinnerItemType.setSelection(0)
        } else {
            myView.spinnerItemType.setSelection(1)
        }
        if (vehicledata.fuelType == FuelType.PETROL.type) {
            myView.spinnerFuelType.setSelection(0)
        } else {
            myView.spinnerFuelType.setSelection(1)
        }

        if (vehicledata.transmissionType == TransmissionType.AUTOMATIC.type) {
            myView.spinnerTransmissionType.setSelection(0)
        } else {
            myView.spinnerTransmissionType.setSelection(1)
        }
        carsImageSwitcher = myView.findViewById(R.id.carsImageSwitcher)
        carsImageSwitcher?.setFactory {
            val imgView = ImageView(FacebookSdk.getApplicationContext())
            imgView.scaleType = ImageView.ScaleType.FIT_CENTER
            imgView.setPadding(8, 8, 8, 8)
            imgView
        }

        carsImageSwitcher.setOnClickListener{
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(context as Activity, intent, 100, null)
        }

        Glide.with(context).load(vehicledata.imageUrls[position]).into(carsImageSwitcher.getCurrentView() as ImageView)

        myView.editTextCostPerday.setText(vehicledata.costPerDay.toString())
        myView.editTextModel.setText(vehicledata.model)
        myView.editTextManufacture.setText(vehicledata.manufacture)
        myView.editTextDescription.setText(vehicledata.description)


        myView.nextButton.setOnClickListener {
            if (vehicledata.imageUrls!!.size > 0) {
                if (position < vehicledata.imageUrls!!.size - 1) {
                    position++
                    Glide.with(context).load(vehicledata.imageUrls[position]).into(carsImageSwitcher.getCurrentView() as ImageView)
                } else {
                    position = 0
                    Glide.with(context).load(vehicledata.imageUrls[position]).into(carsImageSwitcher.getCurrentView() as ImageView)
                }
            }


        }

        myView.prevButton.setOnClickListener {
            if (vehicledata.imageUrls!!.size > 0) {
                if (position > 0) {
                    position--
                    Glide.with(context).load(vehicledata.imageUrls[position]).into(carsImageSwitcher.getCurrentView() as ImageView)
                } else {
                    position = vehicledata.imageUrls!!.size - 1
                    Glide.with(context).load(vehicledata.imageUrls[position]).into(carsImageSwitcher.getCurrentView() as ImageView)
                }
            }

        }

        myView.publishButton.setOnClickListener{
            if(myView.editTextDescription.text.toString() != "" && myView.editTextCostPerday.text.toString() != "" &&
                    myView.editTextModel.text.toString() != "" && myView.editTextManufacture.text.toString() != "" && vehicledata.imageUrls!!.size > 0 ) {
                progressDialog.show()

                val vehicleType = myView.spinnerItemType.selectedItem.toString()
                val costPerDay = myView.editTextCostPerday.text.toString().toFloat()
                val vehicle = Vehicle(vehicleType, costPerDay, vehicledata.imageUrls, auth.currentUser!!.uid, myView.editTextModel.text.toString(),
                        myView.editTextManufacture.text.toString(), myView.spinnerTransmissionType.selectedItem.toString(), myView.editTextDescription.text.toString(), myView.spinnerFuelType.selectedItem.toString())
                databaseRef.child("vehicles").child(vehicleIds.get(holder.adapterPosition)).setValue(vehicle)
                progressDialog.hide()
                Toast.makeText(context, "Uploaded successfully!", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
                dialog.dismiss()

            }

            else {
                Toast.makeText(context, "Please enter all the values and at least 1 image!", Toast.LENGTH_SHORT).show()
            }
            notifyDataSetChanged()
        }
        dialog.show()
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }
}