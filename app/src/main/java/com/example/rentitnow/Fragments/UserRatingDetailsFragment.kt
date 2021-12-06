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
import com.example.rentitnow.User
import com.example.rentitnow.Vendor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_vendor_details.*


class UserRatingDetailsFragment : Fragment() {

    val database = FirebaseDatabase.getInstance()
    lateinit var user: User
    lateinit var userID: String
    var sum = 0.0f
    var reviewsList = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            user = requireArguments().getSerializable(USER) as User
            Glide.with(context).load(user.profileImgUrl).into(vendorImgView_details)
            vendorName_details.text = user.fname + " ${user.lname}"
            fetchUserId()
        }

    }

    private fun fetchUserId() {
        database.getReference("users").orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent( object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children
                userID = items.elementAt(0).key.toString()
                println("VENDOR ID: ${userID}")
                calculateUserRating()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun calculateUserRating() {
        database.getReference("VendorRatings").orderByChild("userId").equalTo(userID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("ON DATA CHANGE REVIEWS")
                val ratings = snapshot.children
                if(snapshot.childrenCount > 0) {
                    ratings.forEach {
                        val rating = it.getValue(Rating::class.java)
                        reviewsList.add(rating!!.feedback)
                        sum += rating.rating
                    }
                    vendorRating.text = String.format("%.2f",(sum/snapshot.childrenCount))
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_details, container, false)
    }

    companion object {
        val USER = "user"
    }
}