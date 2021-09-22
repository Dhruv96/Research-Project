package com.example.rentitnow

import android.net.Uri

data class User(
    var fname: String,
    var lname:String,
    var email: String,
    var profileImgUrl: String?,
    var licenseNo: String,
    var licenseImgUrl: String,
    var date_of_birth: String,
    var gender: String) : java.io.Serializable
