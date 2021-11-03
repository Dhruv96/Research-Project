package com.example.rentitnow

data class Vendor (
    var fname: String,
    var lname:String,
    var email: String,
    var phn: String,
    var address:String,
    var city: String,
    var profileImgUrl: String?): java.io.Serializable
{
        constructor() :
                this("", "", "", "", "" ,"", "") {

        }
}
