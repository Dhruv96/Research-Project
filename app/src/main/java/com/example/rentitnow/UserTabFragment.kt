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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_user_tab.*


/**
 * A simple [Fragment] subclass.
 * Use the [UserTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserTabFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val OPERATION_CHOOSE_PHOTO = 2
    lateinit var fileUrl: Uri

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
            openGallery()
        })

        signup_btn.setOnClickListener(View.OnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPasswordSignup.text.toString()
            val fname = editTextFirstName.text.toString()
            var lname = editTextLastName.text.toString()
            var gender = if (maleRdb.isChecked) "Male" else "Female"
            println(email.trim())
            println(password.trim())
            println(fname.trim())
            println(lname.trim())

            if (email.trim() != "" &&
                    password.trim() != "" &&
                    fname.trim() != "" &&
                    lname.trim() != "" &&
                    (maleRdb.isChecked || femaleRdb.isChecked)) {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                println("Success")
                                val user = User(fname, lname, email, null, "", "", "01/01/1996", gender)
                                println(user)
                                val intent = Intent(activity, LicenseDetailsActivity::class.java)
                                intent.putExtra(LicenseDetailsActivity.USER_OBJ, user)
                                startActivity(intent)

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(activity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                                //updateUI(null)
                            }
                        }
            } else {
                Toast.makeText(activity, "Please enter all the values", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun openGallery(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPERATION_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            SelectUserImage.setImageURI(selectedImage)
            if (selectedImage != null) {
                fileUrl = selectedImage
                println(fileUrl)
            }
        }
    }

}