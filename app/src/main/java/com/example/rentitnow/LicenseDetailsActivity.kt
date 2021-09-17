package com.example.rentitnow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LicenseDetailsActivity : AppCompatActivity() {

    companion object{
        val USER_OBJ = "USER"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_details)

        val user = intent.getSerializableExtra(USER_OBJ) as User
        user.licenseImgUrl = "new Url"
        println(user)
    }
}