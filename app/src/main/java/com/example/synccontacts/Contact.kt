package com.example.synccontacts

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Contact(
    @SerializedName("phone")
    var phone:Long,
    @SerializedName("name")
    var name:String
)