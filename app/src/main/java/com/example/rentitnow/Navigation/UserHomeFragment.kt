package com.example.rentitnow.Navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rentitnow.Helpers.Companion.transformIntoDatePickerWithMinDate
import com.example.rentitnow.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.nav_header.*
import java.text.SimpleDateFormat
import java.util.*


class UserHomeFragment : Fragment() {

    private lateinit var databaseRef : DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        databaseRef= FirebaseDatabase.getInstance().getReference("users")
        val user = auth.currentUser
        val id=user?.uid
        databaseRef.child(id.toString()).get().addOnSuccessListener {
            if (it.exists()){
                val firstname=it.child("fname").value
                textViewPickup.setText("Hello "+firstname+"\n"+"Please select Pick Up Location, Date and Return Date")
            }
        }.addOnFailureListener {
            Log.e("FBLOGIN_FAILD", "error retriving data")
        }
        editTextPickupDate.transformIntoDatePickerWithMinDate(requireContext(), "MM/dd/yyyy")
        editTextReturnDate.transformIntoDatePickerWithMinDate(requireContext(), "MM/dd/yyyy")

        buttonConfirmPickUp.setOnClickListener(View.OnClickListener {
            val pickupdate = editTextPickupDate.text.toString()
            val returndate = editTextReturnDate.text.toString()
            if (pickupdate.isEmpty() || returndate.isEmpty()) {
                Toast.makeText(activity, "Please select pickup and return dates", Toast.LENGTH_SHORT).show()
            } else {

                try {
                    val datepickup: Date
                    val dateReturn: Date
                    val dates = SimpleDateFormat("MM/dd/yyyy")
                    datepickup = dates.parse(pickupdate)
                    dateReturn = dates.parse(returndate)
                    val difference = datepickup.time - dateReturn.time
                    if (difference > 0) {
                        Toast.makeText(
                            activity,
                            "Please select a valid return date.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
//                        val intent = Intent(activity, CarsListActivity::class.java)
//                        intent.putExtra("pickupDate", pickupdate)
//                        intent.putExtra("endDate", returndate)
//                        startActivity(intent)
                    }
                } catch (exception: Exception) {
                    Toast.makeText(view.context, "Unable to find difference", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })


    }


}