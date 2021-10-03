package com.example.rentitnow.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_user_home.*
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.nav_header.*
import java.lang.System.load


class UserProfileFragment : Fragment() {

    private lateinit var databaseRef : DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        databaseRef= FirebaseDatabase.getInstance().getReference("users")
        val user = auth.currentUser
        val id=user?.uid
        databaseRef.child(id.toString()).get().addOnSuccessListener {
            if (it.exists()){
                val firstname=it.child("fname").value
                val lastname=it.child("lname").value
                val email=it.child("email").value
                val gender=it.child("gender").value
                val photoURL=it.child("profileImgUrl").value
                editTextFirstName.setText(firstname.toString())
                editTextLastName.setText(lastname.toString())
                editTextEmail.setText(email.toString())
                if (gender.toString()=="Male"){
                    maleRdb.setChecked(true)
                    Log.d("gender", "gender passed")
                }else{
                    femaleRdb.setChecked(true)
                }

                Glide.with(activity).load(photoURL).into(SelectUserImage)


            }
        }.addOnFailureListener {
            Log.e("FBLOGIN_FAILD", "error retriving data")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }


}