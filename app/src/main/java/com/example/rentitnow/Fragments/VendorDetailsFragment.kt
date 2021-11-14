package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.rentitnow.Data.Rating
import com.example.rentitnow.R
import com.example.rentitnow.Vendor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_vendor_details.*


class VendorDetailsFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()
    lateinit var vendor: Vendor
    lateinit var vendorID: String
    var sum = 0.0f
    var reviewsList = mutableListOf<String>()

    companion object {
        val VENDOR = "vendor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            vendor = requireArguments().getSerializable(VENDOR) as Vendor
            Glide.with(context).load(vendor.profileImgUrl).into(vendorImgView_details)
            vendorName_details.text = vendor.fname + " ${vendor.lname}"
            fetchVendorId()
        }

    }

    private fun calculateVendorRating() {
        database.getReference("UserRatings").orderByChild("vendorId").equalTo(vendorID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("ON DATA CHANGE REVIEWS")
                val ratings = snapshot.children
                if(snapshot.childrenCount > 0) {
                    ratings.forEach {
                        val rating = it.getValue(Rating::class.java)
                        reviewsList.add(rating!!.feedback)
                        sum += rating.rating
                    }
                    vendorRating.text = (sum/snapshot.childrenCount).toString()
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, reviewsList)
                    reviewsListView.adapter = adapter
                }
                else
                {
                    vendorRating.text = "Not received enough ratings"
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun fetchVendorId() {
        database.getReference("vendors").orderByChild("email").equalTo(vendor.email).addListenerForSingleValueEvent( object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children
                vendorID = items.elementAt(0).key.toString()
                println("VENDOR ID: ${vendorID}")
                calculateVendorRating()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}