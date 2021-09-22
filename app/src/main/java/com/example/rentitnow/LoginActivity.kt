package com.example.rentitnow


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
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


class LoginActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myScope = Scope("https://www.googleapis.com/auth/user.birthday.read")
        val myScope2 = Scope(Scopes.PLUS_ME)
        val myScope3 = Scope(Scopes.PROFILE) //get name and id
        callbackManager= CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(myScope, myScope2, myScope3)
            .requestProfile()
            .build()
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        facebookLogin.setOnClickListener(View.OnClickListener {
            signInWithFacebook()
        })

        googlesigninBtn.setOnClickListener(View.OnClickListener {
            signInWithGoogle()
        })
    }

    private fun signInWithFacebook() {
        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(
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
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
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
                    if(firstName != null && lastname != null && email != null ) {
                        val currentUser = User(firstName, lastname, email, photoUrl, "", "", "", "")
                        val intent = Intent(this, SignInWithGoogleAdditionalDetails::class.java)
                        intent.putExtra(SignInWithGoogleAdditionalDetails.USER_OBJ, currentUser)
                        startActivity(intent)
                    }
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
                }
            }
    }



private fun updateUI(user: FirebaseUser?) {

}










