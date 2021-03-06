package com.example.rentitnow


import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import android.text.TextUtils
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.ActionBar


class LoginActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 100
    var userSignin = true
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        sharedPreferences = getSharedPreferences("logged_in", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        FBlogin_button.setOnClickListener(View.OnClickListener {
            displayPopup(null, FBlogin_button)
            Log.v("tag", "1")
        })

        googlesigninBtn.setOnClickListener(View.OnClickListener {
            displayPopup(googlesigninBtn, null)
        })

        signupButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        })

        loginButton.setOnClickListener(View.OnClickListener {
            loginBtnAction()
        })

        forgotPassBtn.setOnClickListener{
            val email: String = emailEditText.getText().toString().trim()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(application, "Enter your registered email id", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(this, "Password reset link sent to ${email}", Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            }
        }



        LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        // Handle success
                        if (loginResult != null) {
                            handleFacebookAccessToken(loginResult.accessToken)
                        }
                    }

                    override fun onCancel() {
                        Log.d(TAG, "facebook:onCancel")
                    }

                    override fun onError(exception: FacebookException) {
                        Log.d(TAG, "facebook:onError", exception)
                    }
                }
        )
    }

    private fun loginBtnAction() {
        //Check for empty fields and then login
        if (emailEditText.getText().toString() == "") {
            Toast.makeText(this, "Please enter email address.", Toast.LENGTH_SHORT).show()

        } else if (passwordEditText.getText().toString() == "") {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show()
        } else {
            checkFromFirebase()
        }
    }

    private fun checkFromFirebase() {

        //Firebase authentication
        auth.signInWithEmailAndPassword(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
        )
            .addOnCompleteListener(this,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            checkUserInDB()
                        } else {
                            // If sign in fails, display a message to the user.
//                        hud.dismiss()
                            Log.w("FIREBASE ::", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                    this, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
    }

    private fun checkUserInDB() {
        var database = FirebaseDatabase.getInstance()

        var user = auth.currentUser
        if (user != null) {
            database.getReference("users").child(user.uid).get().addOnSuccessListener {
                if(it.exists()) {
                    editor.putInt("userLoggedIn", 0)
                    editor.putString("userId", user.uid)
                    editor.commit()
                    editor.apply()
                    val intent = Intent(this, NavigationActivityUser::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else {
                    database.getReference("vendors").child(user.uid).get().addOnSuccessListener {
                        if (it.exists()) {
                            editor.putInt("vendorLoggedIn", 0)
                            editor.putString("vendorId", user.uid)
                            editor.commit()
                            editor.apply()
                            val intent = Intent(this, NavigationActivityVendor::class.java)

                            startActivity(intent)
                            finish()
                        }
                        else {
                            Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun signInWithFacebook() {
        Log.v("tag", "3")

        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
        )
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser


                        val request = GraphRequest.newMeRequest(token) { `object`, response ->
                            try {
                                //here is the data that you want
                                Log.d("FBLOGIN_JSON_RES", `object`.toString())

                                if (`object`.has("id")) {
                                    val nameArray = user?.displayName?.split(" ")
                                    val firstName = nameArray?.get(0)
                                    val lastname = nameArray?.get(1)
                                    val email = user?.email
                                    val photoUrl = user?.photoUrl.toString()
                                    if (userSignin) {
                                        if (firstName != null && lastname != null && email != null) {
                                            val currentUser = User(
                                                    firstName,
                                                    lastname,
                                                    email,
                                                    photoUrl,
                                                    "",
                                                    "",
                                                    "",
                                                    ""
                                            )
                                            editor.putInt("userLoggedIn", 1)
                                            editor.commit()
                                            editor.apply()
                                            val currentUserId = auth.currentUser?.uid!!
                                            checkIfUserExists(currentUserId, currentUser)
                                        }
                                    } else {
                                        if (firstName != null && lastname != null && email != null) {
                                            val currentVendor = Vendor(
                                                    firstName,
                                                    lastname,
                                                    email,
                                                    "",
                                                    "",
                                                    "",
                                                    photoUrl
                                            )
                                            editor.putInt("vendorLoggedIn", 1)
                                            editor.commit()
                                            editor.apply()
                                            checkIfVendorExists(auth.currentUser?.uid!!, currentVendor)
                                        }
                                    }

                                } else {
                                    Log.e("FBLOGIN_FAILD", `object`.toString())
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()

                            }


                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "name,email,id,picture.type(large)")
                        request.parameters = parameters
                        request.executeAsync()


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                        ).show()

                    }
                }

    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }


        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val nameArray = user?.displayName?.split(" ")
                    val firstName = nameArray?.get(0)
                    val lastname = nameArray?.get(1)
                    val email = user?.email
                    val photoUrl = user?.photoUrl.toString()
                    if(userSignin) {
                        if(firstName != null && lastname != null && email != null ) {
                            val currentUser = User(
                                    firstName,
                                    lastname,
                                    email,
                                    photoUrl,
                                    "",
                                    "",
                                    "",
                                    ""
                            )
                            editor.putInt("userLoggedIn", 2)
                            editor.commit()
                            editor.apply()
                            checkIfUserExists(auth.currentUser?.uid!!, currentUser)
                        }
                    }
                    else {
                        if(firstName != null && lastname != null && email != null ) {
                            val currentVendor = Vendor(firstName, lastname, email, "", "", "", photoUrl)
                            editor.putInt("vendorLoggedIn", 2)
                            editor.commit()
                            editor.apply()
                            checkIfVendorExists(auth.currentUser?.uid!!, currentVendor)
                        }
                    }

                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
                }
            }

    private fun displayPopup(googleButton: ImageButton?, facebookButton: ImageButton?) {
        var popupMenu: PopupMenu
        if(googleButton != null) {
            popupMenu = PopupMenu(this, googleButton)
        }
        else {
            popupMenu = PopupMenu(this, facebookButton)
        }
        popupMenu.menuInflater.inflate(R.menu.signin_choice, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_user -> {
                    userSignin = true
                    if (googleButton != null) {
                        signInWithGoogle()
                    } else {
                        Log.v("tag", "2")
                        signInWithFacebook()
                    }
                }

                R.id.action_vendor -> {
                    userSignin = false
                    if (googleButton != null) {
                        signInWithGoogle()
                    } else {
                        Log.v("tag", "2")
                        signInWithFacebook()
                    }
                }

            }
            true
        })
        popupMenu.show()
    }

    private fun checkIfUserExists(userId: String, currentUser: User){
        val database = FirebaseDatabase.getInstance()
        database.getReference().child("users").child(userId).get().addOnSuccessListener {
            if(it.exists()) {
                val intent = Intent(this, NavigationActivityUser::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this, SignInWithGoogleAdditionalDetails::class.java)
                intent.putExtra(SignInWithGoogleAdditionalDetails.USER_OBJ, currentUser)
                startActivity(intent)
            }
        }
    }

    private fun checkIfVendorExists(vendorId: String, currentVendor: Vendor) {
        val database = FirebaseDatabase.getInstance()
        database.getReference().child("vendors").child(vendorId).get().addOnSuccessListener {
            if(it.exists()) {
                val intent = Intent(
                        this,
                        NavigationActivityVendor::class.java
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(
                        this,
                        SignInWithSocialAdditionalVendorData::class.java
                )
                intent.putExtra(
                        SignInWithSocialAdditionalVendorData.VENDOR_OBJ,
                        currentVendor
                )
                startActivity(intent)
            }
        }
    }
    override fun onStart() {

        val pref = applicationContext.getSharedPreferences("logged_in", 0) // 0 - for private mode
        val editor = pref.edit()
        if (pref.contains("userLoggedIn")) {
            if (pref.getInt("userLoggedIn",0) == 0||pref.getInt("userLoggedIn",0) == 1||pref.getInt("userLoggedIn",0) == 2) {
                val intent = Intent(this, NavigationActivityUser::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }else if (pref.contains("vendorLoggedIn")){
            val intent = Intent(this, NavigationActivityVendor::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        super.onStart()
    }
    }













