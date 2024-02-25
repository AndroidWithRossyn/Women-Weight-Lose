package com.loseweight.utils

import android.content.Context
import android.widget.Toast

fun Context.sToast(msg :String){
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

