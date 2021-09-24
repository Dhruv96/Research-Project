package com.example.rentitnow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SignInWithSocialAdditionalVendorData : AppCompatActivity() {

    companion object{
        val VENDOR_OBJ = "VENDOR"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_with_social_additional_vendor_data)
    }
}