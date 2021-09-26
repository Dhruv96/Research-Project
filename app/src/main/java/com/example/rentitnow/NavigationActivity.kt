package com.example.rentitnow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.rentitnow.Navigation.UserHomeFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.nav_header.*

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var databaseRef : DatabaseReference
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //Call shared  pref to get data profile
//        pref = applicationContext.getSharedPreferences("users", 0)
        databaseRef=FirebaseDatabase.getInstance().getReference()
        databaseRef.child("users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)
                    nameViewProfile.setText(user?.fname)
                    emailViewProfile.setText(user?.email)

//                    Glide.with(this).load(user?.profileImgUrl).into(userimageView)

                }


            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


//        nameViewProfile.setText(pref.getString("fname", ""))
//        emailViewProfile.setText(pref.getString("email", ""))
//
//        Glide.with(this).load(pref.getString("profileImgUrl", "")).into(userimageView)





        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, UserHomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        // Configure sign-in to request the user's ID, email address, and basic profile.

        // Configure sign-in to request the user's ID, email address, and basic profile.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            //super.onBackPressed();
            Toast.makeText(this, "No further back allowed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, UserHomeFragment()).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, UserHomeFragment()).commit()
            R.id.nav_history -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, UserHomeFragment()).commit()
//            R.id.nav_share -> {
//                //Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                val sendIntent = Intent()
//                sendIntent.action = Intent.ACTION_SEND
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing application to rent cars - Quick Rentals") // Simple text and URL to share
//                sendIntent.type = "text/plain"
//                this.startActivity(sendIntent)
//            }
            R.id.nav_logout -> signOut(pref.getInt("loginType", 0))
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    //Handle logout
    fun signOut(loginType: Int) {
        when (loginType) {
            0 -> {
                FirebaseAuth.getInstance().signOut()
                val pref = applicationContext.getSharedPreferences("MyPref", 0) // 0 - for private mode
                val editor = pref.edit()
                editor.clear()
                editor.apply()
                Toast.makeText(this, "Successfully signed out.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            1 -> {
                googleSignInClient.signOut()
                        .addOnCompleteListener(this, OnCompleteListener<Void?> { // ...
                            val pref = applicationContext.getSharedPreferences("MyPref", 0) // 0 - for private mode
                            val editor = pref.edit()
                            editor.clear()
                            editor.apply()
                            Toast.makeText(this, "Successfull signed out.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                        })
            }
            2 -> {
            }
            else -> startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun onFragmentInteraction() {
        pref = applicationContext.getSharedPreferences("users", 0) // 0 - for private mode
        nameViewProfile.setText(pref.getString("fname", ""))
    }
}