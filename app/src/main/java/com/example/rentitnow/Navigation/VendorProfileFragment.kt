package com.example.rentitnow.Navigation

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.rentitnow.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_user_profile.*
import java.sql.DriverManager


class VendorProfileFragment : Fragment() {
    
    private lateinit var databaseRef : DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var fileUrl: Uri
    val storageRef = Firebase.storage.reference

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {

            SelectUserImage.setImageURI(uri)
            DriverManager.println("URI:")
            DriverManager.println(uri.toString())
            fileUrl = uri
            DriverManager.println("FILE URL:")
            println(fileUrl)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        databaseRef= FirebaseDatabase.getInstance().getReference("vendors")
        val user = auth.currentUser
        val id=user?.uid
        databaseRef.child(id.toString()).get().addOnSuccessListener {
            if (it.exists()){
                val firstname=it.child("fname").value
                val lastname=it.child("lname").value
                val email=it.child("email").value
                val photoURL=it.child("profileImgUrl").value
                editTextFirstName.setText(firstname.toString())
                editTextLastName.setText(lastname.toString())
                editTextEmail.setText(email.toString())

                Glide.with(activity).load(photoURL).into(SelectUserImage)


            }
        }.addOnFailureListener {
            Log.e("FBLOGIN_FAILD", "error retriving data")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_profile, container, false)
    }


}