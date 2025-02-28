package com.example.habittacker.models

import android.provider.ContactsContract.CommonDataKinds.Im

data class habit(
    var id:String?=null,
    var habit:String?=null,
    var hours:String?=null,
    var timestamp: Long = 0
)
