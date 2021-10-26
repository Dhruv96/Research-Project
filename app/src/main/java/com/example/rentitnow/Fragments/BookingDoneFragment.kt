package com.example.rentitnow.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rentitnow.Navigation.UserHomeFragment
import com.example.rentitnow.NavigationActivityUser
import com.example.rentitnow.R
import kotlinx.android.synthetic.main.fragment_booking_done.*

class BookingDoneFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_done, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_to_home.setOnClickListener {
            (activity as NavigationActivityUser).navigationView.setCheckedItem(R.id.nav_home)
            val home = UserHomeFragment()
            (context as NavigationActivityUser).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_user, home, "findThisFragment")
                .addToBackStack(null)
                .commit()
        }
    }

}