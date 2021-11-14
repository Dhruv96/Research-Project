package com.example.rentitnow.Fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.rentitnow.R
import com.example.rentitnow.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.SelectUserImage
import kotlinx.android.synthetic.main.fragment_user_profile.editTextEmail
import kotlinx.android.synthetic.main.fragment_user_profile.editTextFirstName
import kotlinx.android.synthetic.main.fragment_user_profile.editTextLastName
import kotlinx.android.synthetic.main.fragment_user_profile.femaleRdb
import kotlinx.android.synthetic.main.fragment_user_profile.maleRdb
import java.sql.DriverManager


class UserProfileFragment : Fragment() {

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
                val licenseNo=it.child("licenseNo").value
                val licenseImg=it.child("licenseImgUrl").value
                val profileImg=it.child("profileImgUrl").value
                val dob=it.child("date_of_birth").value
                editTextFirstName.setText(firstname.toString())
                editTextLastName.setText(lastname.toString())
                editTextEmail.setText(email.toString())
                editTextEmail.isEnabled=false
                if (gender.toString()=="Male"){
                    maleRdb.setChecked(true)
                    Log.d("gender", "gender passed")
                }else{
                    femaleRdb.setChecked(true)
                }

                Glide.with(activity).load(photoURL).into(SelectUserImage)
                SelectUserImage.setOnClickListener(View.OnClickListener {
                    selectImageFromGalleryResult.launch("image/*")
                })

                update_btn_User.setOnClickListener({
                    val fname=editTextFirstName.text.toString()
                    val lname=editTextLastName.text.toString()
                    var gender = if (maleRdb.isChecked) "Male" else "Female"
                    if (
                        fname.trim() != "" &&
                        lname.trim() != "" &&
                        (maleRdb.isChecked || femaleRdb.isChecked) ) {
                        if (!this::fileUrl.isInitialized) {
                            val user = User(
                                fname,
                                lname,
                                email.toString(),
                                profileImg.toString(),
                                licenseNo.toString(),
                                licenseImg.toString(),
                                dob.toString(),
                                gender
                            )
                            databaseRef.child(id.toString()).setValue(user)

                            Toast.makeText(
                                context,
                                "Profile Updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else {


                            val currentUserImageRef =
                                storageRef.child("images/${auth.currentUser?.uid}")
                            val uploadTask = currentUserImageRef.putFile(fileUrl)
                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener {
                                // Handle unsuccessful uploads
                            }.addOnSuccessListener { taskSnapshot ->
                                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                                    val downloadUrl = task.result!!.toString()
                                    val user = User(
                                        fname,
                                        lname,
                                        email.toString(),
                                        downloadUrl,
                                        licenseNo.toString(),
                                        licenseImg.toString(),
                                        dob.toString(),
                                        gender
                                    )
                                    databaseRef.child(id.toString()).setValue(user)
                                    Toast.makeText(
                                        context,
                                        "Profile Updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    }
                })

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