package com.example.rentitnow

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rentitnow.Helpers.Companion.transformIntoDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_user_tab.*


/**
 * A simple [Fragment] subclass.
 * Use the [UserTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserTabFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val OPERATION_CHOOSE_PHOTO = 1
    lateinit var fileUrl: Uri
    lateinit var user: User
    val storageRef = Firebase.storage.reference

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if(uri == null) {
                println("NULL URI")
            }
            else {
                println("NOT NULL URI")
            }
            SelectUserImage.setImageURI(uri)
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
        return inflater.inflate(R.layout.fragment_user_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        SelectUserImage.setBackgroundResource(R.drawable.maleuser);
        maleRdb.isChecked = true

        genderRadioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.maleRdb && SelectUserImage.drawable.constantState == context?.let { ContextCompat.getDrawable(it, R.drawable.femaleuser)?.constantState }) {
                SelectUserImage.setImageResource(R.drawable.maleuser)
            } else if (checkedId == R.id.femaleRdb && SelectUserImage.drawable.constantState == context?.let { ContextCompat.getDrawable(it, R.drawable.maleuser)?.constantState }) {
                SelectUserImage.setImageResource(R.drawable.femaleuser)
            }
        })

        SelectUserImage.setOnClickListener(View.OnClickListener {
            selectImageFromGallery()
        })

        editTextDOB.transformIntoDatePicker(requireContext(), "dd-MMM-yyyy")

        signup_btn.setOnClickListener(View.OnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPasswordSignup.text.toString()
            val fname = editTextFirstName.text.toString()
            var lname = editTextLastName.text.toString()
            var gender = if (maleRdb.isChecked) "Male" else "Female"
            val dob = editTextDOB.text.toString()
            println(email.trim())
            println(password.trim())
            println(fname.trim())
            println(lname.trim())

            if (email.trim() != "" &&
                    password.trim() != "" &&
                    fname.trim() != "" &&
                    lname.trim() != "" &&
                    (maleRdb.isChecked || femaleRdb.isChecked) && dob.trim() != "") {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                println("Success")
                                if (!this::fileUrl.isInitialized) {
                                    user = User(fname, lname, email, null, "", "", dob, gender)
                                    println(user)
                                    val intent = Intent(activity, LicenseDetailsActivity::class.java)
                                    intent.putExtra(LicenseDetailsActivity.USER_OBJ, user)
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
                                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener{task ->
                                            val downloadUrl = task.result!!.toString()
                                            user = User(fname, lname, email, downloadUrl, "", "", dob, gender)
                                            println(user)
                                            val intent = Intent(activity, LicenseDetailsActivity::class.java)
                                            intent.putExtra(LicenseDetailsActivity.USER_OBJ, user)
                                            startActivity(intent)
                                        }

                                    }
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(activity, task.exception?.localizedMessage,
                                        Toast.LENGTH_SHORT).show()
                                //updateUI(null)
                            }
                        }
            } else {
                Toast.makeText(activity, "Please enter all the values", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

}