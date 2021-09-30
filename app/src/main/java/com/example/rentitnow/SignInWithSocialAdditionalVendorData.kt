package com.example.rentitnow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_vendor_tab.*

class SignInWithSocialAdditionalVendorData : AppCompatActivity() {
    companion object {
        val VENDOR_OBJ = "VENDOR"
    }
    val databaseRef = FirebaseDatabase.getInstance().reference
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_with_social_additional_vendor_data)
        val vendor = intent.getSerializableExtra(VENDOR_OBJ) as Vendor


        continue_btn_vendor.setOnClickListener(View.OnClickListener{
            var phnNumber= editTextPhn.text.toString()
            var address = editTextAddress.text.toString()
            var city = spinnerCity.selectedItem.toString()

            if (
                    phnNumber.trim() != "" &&
                    address.trim() != "" &&
                    city.trim() != "" ) {
                vendor.phn=phnNumber
                vendor.address=address
                vendor.city=city
                databaseRef.child("vendors").child(auth.currentUser!!.uid).setValue(vendor)
                val intent = Intent(this, NavigationActivityVendor::class.java)
                startActivity(intent)

            }
        })

    }
}