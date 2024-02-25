package com.loseweight.interfaces

import java.util.*

interface DateEventListener {
    abstract fun onDateSelected(date: Date, hourOfDay: Int, minute: Int)
}