package com.loseweight.interfaces

import com.loseweight.objects.Spinner
import java.util.*


interface SpinnerCallback {
    abstract fun onDone(list: ArrayList<Spinner>)
}