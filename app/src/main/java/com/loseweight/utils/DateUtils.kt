package com.loseweight.utils

import java.text.SimpleDateFormat
import java.util.*


internal object DateUtils {
    // This class should not be initialized
    private fun DateUtils() {

    }


    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    fun formatTime(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    fun formatTimeWithMarker(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    fun getHourOfDay(timeInMillis: Long): Int {
        val dateFormat = SimpleDateFormat("H", Locale.getDefault())
        return Integer.valueOf(dateFormat.format(timeInMillis))
    }

    fun getMinute(timeInMillis: Long): Int {
        val dateFormat = SimpleDateFormat("m", Locale.getDefault())
        return Integer.valueOf(dateFormat.format(timeInMillis))
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     *
     * @param timeInMillis The time to convert, in milliseconds.
     * @return The time or date.
     */
    fun formatDateTime(timeInMillis: Long): String {
        return if (isToday(timeInMillis)) {
            formatTime(timeInMillis)
        } else {
            formatDate(timeInMillis)
        }
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    fun formatDate(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    fun isToday(timeInMillis: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(timeInMillis)
        return date == dateFormat.format(System.currentTimeMillis())
    }

    /**
     * Checks if two dates are of the same day.
     *
     * @param millisFirst  The time in milliseconds of the first date.
     * @param millisSecond The time in milliseconds of the second date.
     * @return Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    fun hasSameDate(millisFirst: Long, millisSecond: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(millisFirst) == dateFormat.format(millisSecond)
    }
}