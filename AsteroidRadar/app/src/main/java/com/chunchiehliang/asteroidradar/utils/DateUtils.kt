package com.chunchiehliang.asteroidradar.utils

import com.chunchiehliang.asteroidradar.utils.Constants.DEFAULT_END_DATE_DAYS
import java.text.SimpleDateFormat
import java.util.*

fun getTodayFormattedDate(): String {
    val currentTime = Calendar.getInstance().time
    val dateFormat = getFormat()
    return dateFormat.format(currentTime)
}

fun getSevenDaysLaterFormattedDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, DEFAULT_END_DATE_DAYS)
    val time = calendar.time
    val dateFormat = getFormat()
    return dateFormat.format(time)
}

private fun getFormat() = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())