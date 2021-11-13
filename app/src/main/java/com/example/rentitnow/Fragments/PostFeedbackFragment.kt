package com.example.rentitnow.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rentitnow.Navigation.UserHomeFragment
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.NavigationActivityVendor
import com.example.rentitnow.R
import kotlinx.android.synthetic.main.fragment_post_feedback.*

class PostFeedbackFragment : Fragment() {
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("logged_in", 0)

        backToHomeBtn.setOnClickListener{
            if (pref.getInt("userLoggedIn", 4).equals(0)||pref.getInt("userLoggedIn", 0).equals(1)||pref.getInt("userLoggedIn", 0).equals(2)) {
                (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_user, UserHomeFragment(), "findThisFragment")
                    .addToBackStack(null)
                    .commit()
                (activity as NavigationActivityUser).navigationView.setCheckedItem(R.id.nav_home)
            }else if (pref.getInt("vendorLoggedIn", 4).equals(0)||pref.getInt("vendorLoggedIn", 0).equals(1)||pref.getInt("vendorLoggedIn", 0).equals(2)){
                (context as NavigationActivityVendor).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_vendor, VendorHomeFragment(), "findThisFragment")
                    .addToBackStack(null)
                    .commit()
                (activity as NavigationActivityVendor).navigationView.setCheckedItem(R.id.nav_home)
            }
        }
    }
}