package com.example.rentitnow

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_license_details.*


class LicenseDetailsActivity : AppCompatActivity() {

    val databaseRef = FirebaseDatabase.getInstance().reference
    val storageRef = Firebase.storage.reference
    val auth = Firebase.auth
    companion object{
        val USER_OBJ = "USER"
    }

    lateinit var fileUrl: Uri

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            licenseImageView.setImageURI(uri)
            fileUrl = uri
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_details)
        val user = intent.getSerializableExtra(USER_OBJ) as User

        licenseImageView.setOnClickListener {
            selectImageFromGallery()
        }

        uploadSignInButton.setOnClickListener {
            if (licenseNumber.text.toString().trim() != "") {
                if (!this::fileUrl.isInitialized) {
                    Toast.makeText(this, "Please upload the license image!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val licenseImageref = storageRef.child("licenseImages/${auth.currentUser?.uid}")
                    val uploadTask = licenseImageref.putFile(fileUrl)
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                    }.addOnSuccessListener { taskSnapshot ->
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                            val downloadUrl = task.result!!.toString()
                            user.licenseImgUrl = downloadUrl
                            user.licenseNo = licenseNumber.text.toString()
                            println(user)
                            // Adding user to database
                            databaseRef.child("users").child(auth.currentUser!!.uid).setValue(user)
                            val intent = Intent(this, NavigationActivityUser::class.java)
                            startActivity(intent)
                        }

                    }
                }
            } else {
                Toast.makeText(this, "Please enter license number", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")
}