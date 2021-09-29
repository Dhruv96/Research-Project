package com.example.rentitnow

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_user_tab.*
import kotlinx.android.synthetic.main.fragment_user_tab.editTextEmail
import kotlinx.android.synthetic.main.fragment_user_tab.editTextFirstName
import kotlinx.android.synthetic.main.fragment_user_tab.continue_btn_vendor
import kotlinx.android.synthetic.main.fragment_vendor_tab.*
import java.sql.DriverManager.println

class VendorTabFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    val databaseRef = FirebaseDatabase.getInstance().reference
    lateinit var fileUrl: Uri
    lateinit var vendor: Vendor
    val storageRef = Firebase.storage.reference
    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            vendorImgView.setImageURI(uri)
            println("URI:")
            println(uri.toString())
            fileUrl = uri
            println("FILE URL:")
            println(fileUrl)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        continue_btn_vendor.setOnClickListener(View.OnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPswd.text.toString()
            val fname = editTextFirstName.text.toString()
            var lname = editTextLName.text.toString()
            var phnNumber= editTextPhn.text.toString()
            var address = editTextAddress.text.toString()
            var city = spinnerCity.selectedItem.toString()

            if (email.trim() != "" &&
                    password.trim() != "" &&
                    fname.trim() != "" &&
                    lname.trim() != "" &&
                    phnNumber.trim() != "" &&
                address.trim() != "" &&
                city.trim() != "" ) {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {

                                if (!this::fileUrl.isInitialized) {
                                    vendor = Vendor(fname, lname, email, phnNumber, address, city, null)
                                    println(vendor)
                                    databaseRef.child("vendors").child(auth.currentUser!!.uid).setValue(vendor)
                                    val intent = Intent(activity, VendorCarsActivity::class.java)
                                    startActivity(intent)

                                }
                                else {
                                    val currentUserImageRef = storageRef.child("images/${auth.currentUser?.uid}")
                                    val uploadTask = currentUserImageRef.putFile(fileUrl)
                                    // Register observers to listen for when the download is done or if it fails
                                    uploadTask.addOnFailureListener {
                                        // Handle unsuccessful uploads
                                    }.addOnSuccessListener { taskSnapshot ->
                                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                                            val downloadUrl = task.result!!.toString()
                                            println("Success")
                                            val vendor = Vendor(fname, lname, email, phnNumber, address, city, downloadUrl)
                                            databaseRef.child("vendors").child(auth.currentUser!!.uid).setValue(vendor)
                                            val intent = Intent(activity, VendorCarsActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(activity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                                //updateUI(null)
                            }
                        }
            } else {
                Toast.makeText(activity, "Please enter all the values", Toast.LENGTH_SHORT).show()
            }
        })

        vendorImgView.setOnClickListener(View.OnClickListener {
            selectImageFromGallery()
        })
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")
}