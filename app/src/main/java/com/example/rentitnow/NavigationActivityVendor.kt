package com.example.rentitnow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.rentitnow.Fragments.PublishCarFragment
import com.example.rentitnow.Fragments.PublishedVehiclesFragment
import com.example.rentitnow.Fragments.VendorBookingHistoryFragment
import com.example.rentitnow.Fragments.VendorHomeFragment
import com.example.rentitnow.Navigation.VendorProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.nav_header.*

class NavigationActivityVendor : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var databaseRef : DatabaseReference
    private lateinit var pref: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_vendor)
        drawerLayout=findViewById(R.id.drawer_layout_vendor)
        navigationView = findViewById(R.id.nav_view_vendor)
        val toolbar = findViewById<Toolbar>(R.id.toolBarVendor)
        val navHeader = navigationView.getHeaderView(0)

        val nameViewProfile = navHeader.findViewById<TextView>(R.id.nameViewProfile)
        val emailViewProfile = navHeader.findViewById<TextView>(R.id.emailViewProfile)
        val userimageView = navHeader.findViewById<ImageView>(R.id.userimageView)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container_vendor, VendorHomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home_vendor)
        }
        auth = FirebaseAuth.getInstance()
        pref = applicationContext.getSharedPreferences("logged_in", 0)
        databaseRef=FirebaseDatabase.getInstance().getReference("vendors")
        val user = auth.currentUser
        val id=user?.uid
//        databaseRef.child(id.toString()).get().addOnSuccessListener {
//            if (it.exists()){
//                val firstname=it.child("fname").value
//                val email=it.child("email").value
//                val photoURL=it.child("profileImgUrl").value
//                nameViewProfile.setText(firstname.toString())
//                emailViewProfile.setText(email.toString())
//                Glide.with(this).load(photoURL).into(userimageView)
//
//            }
//        }.addOnFailureListener {
//            Log.e("FBLOGIN_FAILD", "error retriving data")
//        }

        databaseRef.child(id.toString()).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vendorData = snapshot.getValue(Vendor::class.java)
                val firstname = vendorData?.fname
                val email = vendorData?.email
                val photoUrl = vendorData?.profileImgUrl
                nameViewProfile.setText(firstname.toString())
                emailViewProfile.setText(email.toString())
                Glide.with(this@NavigationActivityVendor).load(photoUrl).into(userimageView)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NavigationActivityVendor, error.message, Toast.LENGTH_SHORT).show()
            }

        })



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed();
            //Toast.makeText(this, "No further back allowed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {

            R.id.nav_home_vendor -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_vendor, VendorHomeFragment()).commit()
            R.id.nav_profile_vendor -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_vendor, VendorProfileFragment()).commit()
            R.id.nav_history -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_vendor, VendorBookingHistoryFragment()).commit()
            R.id.nav_logout -> logout(pref.getInt("userLoggedIn", 0))
            R.id.published_vehicles -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_vendor, PublishedVehiclesFragment()).commit()
            R.id.publish -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container_vendor, PublishCarFragment()).commit()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun logout(loginType: Int) {
        when (loginType) {
            0, 1 -> {
                FirebaseAuth.getInstance().signOut()
                val pref = applicationContext.getSharedPreferences("logged_in", 0)
                val editor = pref.edit()
                editor.clear()
                editor.apply()
                Toast.makeText(this, "Successfully signed out.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            2 -> {
                googleSignInClient.signOut()
                    .addOnCompleteListener(this, OnCompleteListener<Void?> { // ...
                        val pref = applicationContext.getSharedPreferences("logged_in", 0)
                        val editor = pref.edit()
                        editor.clear()
                        editor.apply()
                        Toast.makeText(this, "Successfully signed out.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    })
            }
            3 -> {
            }
            else ->  {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

}