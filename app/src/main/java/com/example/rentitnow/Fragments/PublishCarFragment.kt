package com.example.rentitnow.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rentitnow.*
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_publish_car.*
import java.util.*
import kotlin.collections.ArrayList


class PublishCarFragment : Fragment() {
    val REQUEST_CODE = 200
    private var images: ArrayList<Uri?>? = null
    private var uploadedImageDownloadUrls: ArrayList<String> = ArrayList()
    lateinit var carsImageSwitcher: ImageSwitcher
    var position = 0
    val storage = Firebase.storage.reference
    val auth = Firebase.auth
    val databaseRef = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publish_car, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Uploading...")
        progressDialog.setMessage("Uploading images and details")

        carsImageSwitcher = view.findViewById(R.id.carsImageSwitcher)
        carsImageSwitcher?.setFactory{
            val imgView = ImageView(getApplicationContext())
            imgView.scaleType = ImageView.ScaleType.FIT_CENTER
            imgView.setPadding(8, 8, 8, 8)
            imgView
        }
        carsImageSwitcher.setImageResource(R.drawable.addphoto)
        val vehicleTypes = listOf(VehicleType.CAR.type, VehicleType.TRUCK.type)
        val fuelTypes = listOf(FuelType.PETROL.type, FuelType.DIESEL.type)
        val transmissionTypes = listOf(TransmissionType.AUTOMATIC.type, TransmissionType.MANUAL.type)
        val adapter = ArrayAdapter(requireActivity(), R.layout.support_simple_spinner_dropdown_item, vehicleTypes)
        val adapter2 = ArrayAdapter(requireActivity(), R.layout.support_simple_spinner_dropdown_item, fuelTypes)
        val adapter3 = ArrayAdapter(requireActivity(), R.layout.support_simple_spinner_dropdown_item, transmissionTypes)
        spinnerItemType.adapter = adapter
        spinnerFuelType.adapter = adapter2
        spinnerTransmissionType.adapter = adapter3
        images = ArrayList()

        carsImageSwitcher.setOnClickListener {
            openGalleryForImages()
        }

        nextButton.setOnClickListener{
            if(images!!.size > 0) {
                if(position < images!!.size - 1) {
                    position++
                    carsImageSwitcher.setImageURI(images!![position])
                }
                else {
                    position = 0
                    carsImageSwitcher.setImageURI(images!![position])
                }
            }


        }

        prevButton.setOnClickListener{
            if(images!!.size > 0)  {
                if(position > 0) {
                    position--
                    carsImageSwitcher.setImageURI(images!![position])
                }
                else {
                    position = images!!.size - 1
                    carsImageSwitcher.setImageURI(images!![position])
                }
            }

        }

       publishButton.setOnClickListener{
           if(editTextDescription.text.toString() != "" && editTextCostPerday.text.toString() != "" && editTextModel.text.toString() != "" && editTextManufacture.text.toString() != "" && images!!.size > 0 ) {
               progressDialog.show()
               for(i in 0 until images!!.size) {
                   val ref = storage.child("vehicleImages").child(images!![i]?.lastPathSegment!!)
                   println("REF: ${ref}")
                   val uploadTask = ref.putFile(images!![i]!!)
                   // Register observers to listen for when the download is done or if it fails
                   uploadTask.addOnFailureListener {
                       // Handle unsuccessful uploads
                       Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                   }.addOnSuccessListener { taskSnapshot ->
                       // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                       taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener{ task ->
                           val downloadUrl = task.result!!.toString()
                           uploadedImageDownloadUrls.add(downloadUrl)
                           println("Image uploaded!")
                           println("Uploaded Images: ${uploadedImageDownloadUrls.size}")
                           println("Selected Images: ${images!!.size}")

                           if (uploadedImageDownloadUrls.size == images!!.size) {
                               val vehicleType = spinnerItemType.selectedItem.toString()
                               val costPerDay = editTextCostPerday.text.toString().toFloat()
                               val vehicle = Vehicle(vehicleType, costPerDay, uploadedImageDownloadUrls, auth.currentUser!!.uid, editTextModel.text.toString(),
                               editTextManufacture.text.toString(), spinnerTransmissionType.selectedItem.toString(), editTextDescription.text.toString(), spinnerFuelType.selectedItem.toString())
                               databaseRef.child("vehicles").child(auth.currentUser!!.uid).setValue(vehicle)
                               progressDialog.hide()
                               Toast.makeText(activity, "Uploaded successfully!", Toast.LENGTH_SHORT).show()
                           }
                           else {
                               println("Not All images uploaded yet..")
                           }
                       }

                   }

               }

           }

           else {
               Toast.makeText(activity, "Please enter all the values and at least 1 image!", Toast.LENGTH_SHORT).show()
           }

       }

     }


    private fun openGalleryForImages() {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                    Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){

            // if multiple images are selected
            if (data?.clipData != null) {
                var count = data.clipData!!.itemCount

                for (i in 0 until count) {
                    var imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    images!!.add(imageUri)
                }

                // set first image
                carsImageSwitcher.setImageURI(images!![0])
                position = 0

            } else if (data?.getData() != null) {
                // if single image is selected

                var imageUri: Uri = data.data!!
                carsImageSwitcher.setImageURI(imageUri)
                position = 0
            }
        }
    }

}