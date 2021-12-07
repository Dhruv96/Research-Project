package com.example.rentitnow.Navigation

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
import com.example.rentitnow.Helpers
import com.example.rentitnow.R
import com.example.rentitnow.User
import com.example.rentitnow.Vendor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.SelectUserImage
import kotlinx.android.synthetic.main.fragment_user_profile.editTextEmail
import kotlinx.android.synthetic.main.fragment_user_profile.editTextFirstName
import kotlinx.android.synthetic.main.fragment_user_profile.editTextLastName
import kotlinx.android.synthetic.main.fragment_vendor_profile.*
import java.sql.DriverManager


class VendorProfileFragment : Fragment() {
    var loader: KProgressHUD? = null
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
        loader = Helpers.getLoader(requireContext())
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
                val phone=it.child("phn").value
                val address=it.child("address").value
                val city=it.child("city").value
                editTextFirstName.setText(firstname.toString())
                editTextLastName.setText(lastname.toString())
                editTextEmail.setText(email.toString())
                editTextPhone.setText(phone.toString())
                editTextEmail.isEnabled=false
                Glide.with(activity).load(photoURL).into(SelectUserImage)
                SelectUserImage.setOnClickListener(View.OnClickListener {
                    selectImageFromGalleryResult.launch("image/*")
                })

                update_btn_Vendor.setOnClickListener {
                    loader?.show()
                    val fname = editTextFirstName.text.toString()
                    val lname = editTextLastName.text.toString()
                    val phoneNo = editTextPhone.text.toString()
                    if (
                        fname.trim() != "" &&
                        lname.trim() != "" &&
                        phoneNo.trim() != ""
                    ) {
                        if (!this::fileUrl.isInitialized) {
                            val vendor = Vendor(
                                fname,
                                lname,
                                email.toString(),
                                phoneNo,
                                address.toString(),
                                city.toString(),
                                photoURL.toString()
                            )
                            databaseRef.child(id.toString()).setValue(vendor)

                            loader?.dismiss()
                            Toast.makeText(
                                context,
                                "Profile Updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {


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
                                    val vendor = Vendor(
                                        fname,
                                        lname,
                                        email.toString(),
                                        phoneNo,
                                        address.toString(),
                                        city.toString(),
                                        downloadUrl
                                    )
                                    databaseRef.child(id.toString()).setValue(vendor)
                                    loader?.dismiss()
                                    Toast.makeText(
                                        context,
                                        "Profile Updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    }
                }


            }
        }.addOnFailureListener {
            loader?.dismiss()
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