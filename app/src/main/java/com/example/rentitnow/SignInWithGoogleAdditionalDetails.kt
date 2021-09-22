package com.example.rentitnow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.example.rentitnow.Helpers.Companion.transformIntoDatePicker
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in_with_google_additional_details.*
import kotlinx.android.synthetic.main.activity_sign_in_with_google_additional_details.genderRadioGrp
import kotlinx.android.synthetic.main.activity_sign_in_with_google_additional_details.maleRdb


class SignInWithGoogleAdditionalDetails : AppCompatActivity() {

    companion object {
        val USER_OBJ = "USER"
    }
    lateinit var databaseRef: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_with_google_additional_details)
        val user = intent.getSerializableExtra(LicenseDetailsActivity.USER_OBJ) as User
        editTextDob.transformIntoDatePicker(this, "dd-MMM-yyyy")
        databaseRef = FirebaseDatabase.getInstance()
        genderRadioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener {group, checkedId ->
            if (checkedId == maleRdb.id) {
                user.gender = "Male"
            }
            else {
                user.gender = "Female"
            }
        })
        continueBtn.setOnClickListener(View.OnClickListener {
            if(user.gender != "" && editTextDob.text.toString() != "") {
                user.date_of_birth = editTextDob.text.toString()
                val intent = Intent(this, LicenseDetailsActivity::class.java)
                intent.putExtra(LicenseDetailsActivity.USER_OBJ, user)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show()
            }
        })

    }
}