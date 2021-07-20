package com.android.service.touchwin

import com.google.gson.annotations.SerializedName

data class response(

    @SerializedName("message")
    var number: Int? = null) {
}