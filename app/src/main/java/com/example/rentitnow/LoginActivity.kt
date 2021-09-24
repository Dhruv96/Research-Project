package com.example.rentitnow


import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.logging.Logger


class LoginActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 100
    var userSignin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myScope = Scope(Scopes.PROFILE) //get name and id
        callbackManager= CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        login_button.setOnClickListener(View.OnClickListener {
            displayPopup(null, login_button)
            Log.v("tag", "1")
        })

        googlesigninBtn.setOnClickListener(View.OnClickListener {

            displayPopup(googlesigninBtn, null)
        })
    }


    private fun signInWithFacebook() {
        Log.v("tag", "3")
        login_button.setReadPermissions("email", "public_profile")
        login_button.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d(TAG, "facebook:onSuccess:$loginResult")
                        handleFacebookAccessToken(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        Log.d(TAG, "facebook:onCancel")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d(TAG, "facebook:onError", error)
                    }
                })
    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
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
                                    if(userSignin) {
                                        if(firstName != null && lastname != null && email != null ) {
                                            val currentUser = User(firstName, lastname, email, photoUrl, "", "", "", "")
                                            val intent = Intent(this, SignInWithGoogleAdditionalDetails::class.java)
                                            intent.putExtra(SignInWithGoogleAdditionalDetails.USER_OBJ, currentUser)
                                            startActivity(intent)
                                        }
                                    }
                                    else {
                                        if(firstName != null && lastname != null && email != null ) {
                                            val currentVendor = Vendor(firstName, lastname, email, "", "","")
                                            val intent = Intent(this, SignInWithSocialAdditionalVendorData::class.java)
                                            intent.putExtra(SignInWithSocialAdditionalVendorData.VENDOR_OBJ, currentVendor)
                                            startActivity(intent)
                                        }
                                    }

                                } else {
                                    Log.e("FBLOGIN_FAILD", `object`.toString())
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
//                                dismissDialogLogin()
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
                            val currentUser = User(firstName, lastname, email, photoUrl, "", "", "", "")
                            val intent = Intent(this, SignInWithGoogleAdditionalDetails::class.java)
                            intent.putExtra(SignInWithGoogleAdditionalDetails.USER_OBJ, currentUser)
                            startActivity(intent)
                        }
                    }
                    else {
                        if(firstName != null && lastname != null && email != null ) {
                            val currentVendor = Vendor(firstName, lastname, email, "", "","")
                            val intent = Intent(this, SignInWithSocialAdditionalVendorData::class.java)
                            intent.putExtra(SignInWithSocialAdditionalVendorData.VENDOR_OBJ, currentVendor)
                            startActivity(intent)
                        }
                    }

                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
                }
            }

    private fun displayPopup(googleButton: ImageButton?, facebookButton: LoginButton?) {
        var popupMenu: PopupMenu
        if(googleButton != null) {
            popupMenu = PopupMenu(this, googleButton)
        }
        else {
            popupMenu = PopupMenu(this, facebookButton)
        }
        popupMenu.menuInflater.inflate(R.menu.signin_choice,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_user -> {
                    userSignin = true
                    if(googleButton != null) {
                        signInWithGoogle()
                    }
                    else {
                        Log.v("tag", "2")
                        signInWithFacebook()
                    }
                }

                R.id.action_vendor -> {
                    userSignin = false
                    if(googleButton != null) {
                        signInWithGoogle()
                    }
                    else {
                        Log.v("tag", "2")
                        signInWithFacebook()
                    }
                }

            }
            true
        })
        popupMenu.show()
    }
    }













