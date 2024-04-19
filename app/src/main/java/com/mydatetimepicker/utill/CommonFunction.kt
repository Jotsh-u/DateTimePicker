package com.mydatetimepicker.utill

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object CommonFunction {

    //LIST OF DATES YEAR WISE
    fun getYearWiseDate(year:Int): ArrayList<Calendar>{
        val startDate = Calendar.getInstance().also {
            it.set(Calendar.DAY_OF_MONTH,1)
            it.set(Calendar.MONTH,Calendar.JANUARY)
            it.set(Calendar.YEAR,year)
        }
        val endDate = Calendar.getInstance().also {
            it.set(Calendar.DAY_OF_MONTH,31)
            it.set(Calendar.MONTH,Calendar.DECEMBER)
            it.set(Calendar.YEAR,year)
        }
        val calendarStartDate = Calendar.getInstance().also { it.time = startDate.time }
        val calendarEndDate = Calendar.getInstance().also { it.time = endDate.time }
        val totalDaysBetweenEnds =
            TimeUnit.MILLISECONDS.toDays(calendarEndDate.timeInMillis - calendarStartDate.timeInMillis)
                .toInt()
        Log.e(
            "DATES",
            "MAX: ${startDate.getActualMaximum(Calendar.DATE)} Start: ${getFormattedDate("dd-MM-yyyy HH:mm:ss",calendarStartDate.time)}  End: ${getFormattedDate("dd-MM-yyyy HH:mm:ss",calendarEndDate.time)} DaysBet : $totalDaysBetweenEnds"
        )
        val tempList: ArrayList<Calendar> = arrayListOf()

        for (count in 0..totalDaysBetweenEnds) {
            val date =
                Calendar.getInstance().also { it.timeInMillis = calendarStartDate.timeInMillis }
            val value = date.also { it.add(Calendar.DAY_OF_YEAR, count) }
            Log.e("DATES","> ${getFormattedDate("dd MM yyyy",value.time)}")
            tempList.add(value)
        }
        return tempList
    }

    //LIST OF DATES MONTH AND YEAR WISE
    fun getMonthYearWiseDate(month:Int,year:Int): ArrayList<Calendar>{
        val startDate = Calendar.getInstance().also {
            it.set(Calendar.DAY_OF_MONTH,1)
            it.set(Calendar.MONTH,month)
            it.set(Calendar.YEAR,year)
        }
        val endDate = Calendar.getInstance().also {
            it.set(Calendar.DAY_OF_MONTH,startDate.getActualMaximum(Calendar.DATE))
            it.set(Calendar.MONTH,month)
            it.set(Calendar.YEAR,year)
        }
        val calendarStartDate = Calendar.getInstance().also { it.time = startDate.time }
        val calendarEndDate = Calendar.getInstance().also { it.time = endDate.time }
        val totalDaysBetweenEnds =
            TimeUnit.MILLISECONDS.toDays(calendarEndDate.timeInMillis - calendarStartDate.timeInMillis)
                .toInt()
        Log.e(
            "DATES",
            "MAX: ${startDate.getActualMaximum(Calendar.DATE)} Start: ${getFormattedDate("dd-MM-yyyy HH:mm:ss",calendarStartDate.time)}  End: ${getFormattedDate("dd-MM-yyyy HH:mm:ss",calendarEndDate.time)} DaysBet : $totalDaysBetweenEnds"
        )

        val tempList: ArrayList<Calendar> = arrayListOf()

        for (count in 0..totalDaysBetweenEnds) {
            val date =
                Calendar.getInstance().also { it.timeInMillis = calendarStartDate.timeInMillis }
            val value = date.also { it.add(Calendar.DAY_OF_YEAR, count) }
            tempList.add(value)
        }
        return tempList
    }

    fun generateListOfDates(startDate:Calendar,endDate:Calendar,currentListCount:Int): ArrayList<Calendar>{
        val calendarStartDate = Calendar.getInstance().also { it.time = startDate.time }
        val calendarEndDate = Calendar.getInstance().also { it.time = endDate.time }
        if (currentListCount > 2)
            calendarStartDate.add(Calendar.DAY_OF_YEAR, -1)
        else
            calendarStartDate.add(Calendar.DAY_OF_YEAR, 3)
        val totalDaysBetweenEnds =
            TimeUnit.MILLISECONDS.toDays(calendarStartDate.timeInMillis - calendarEndDate.timeInMillis)
                .toInt()

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        Log.e(
            "DATES",
            "Start: ${sdf.format(calendarStartDate.time)}  End: ${sdf.format(calendarEndDate.time)} DaysBet : $totalDaysBetweenEnds"
        )

        val tempList: ArrayList<Calendar> = arrayListOf()

        for (count in 0..totalDaysBetweenEnds) {
            val date =
                Calendar.getInstance().also { it.timeInMillis = calendarStartDate.timeInMillis }
            val value = date.also { it.add(Calendar.DAY_OF_YEAR, -count) }
            tempList.add(value)
        }
        return tempList
    }

    fun getHours(is24Hour: Boolean): ArrayList<Int> {

        val hours = ArrayList<Int>()
        if (is24Hour) {
            for (hour in 0..23) hours.add(hour)
        } else {
            for (hour in 1..12) hours.add(hour)
        }

        return hours
    }

    fun getMinutes(): ArrayList<Int> {
        val minutes = ArrayList<Int>()
        for (hour in 0..59) minutes.add(hour)
        return minutes
    }

    fun getMeridian(): ArrayList<String> {
        val meridian = ArrayList<String>()
        meridian.add(AM)
        meridian.add(PM)
        return meridian
    }

    fun addEmptyValue(list: ArrayList<Int>): ArrayList<Int> {
        list.add(0, -1)
        list.add(1, -1)
        list.add(2, -1)
        list.add(-1)
        list.add(-1)
        return list
    }

    fun addEmptyValueInString(list: ArrayList<String>): ArrayList<String> {
        list.add(0, "")
        list.add(1, "")
        list.add("")
        list.add("")
        return list
    }

    fun getInitHourIndex(todayDate:Calendar): Int {
        return if (todayDate.get(Calendar.HOUR_OF_DAY) > 12)
            todayDate.get(Calendar.HOUR_OF_DAY) - 12
        else
            todayDate.get(Calendar.HOUR_OF_DAY)
    }

    fun getInitMinuteIndex(todayDate:Calendar): Int {
        return todayDate.get(Calendar.MINUTE) + 1
    }

    fun getInitMeridianIndex(todayDate:Calendar): Int {
        return if (todayDate.get(Calendar.HOUR_OF_DAY) >= 12)
            Calendar.PM
        else
            Calendar.AM
    }

    fun isValidDate(selectedDate:Calendar,todayDate:Calendar): Boolean {
        println("${todayDate.time} START_DATE")
        println("${selectedDate.time} SELECTED_DATE_BEFORE_VALIDATION ")
        return selectedDate.timeInMillis <= todayDate.timeInMillis
    }

    fun smoothScrollToTop(rv: RecyclerView, position: Int, speed: Float) {
        val layoutManager = rv.layoutManager as LinearLayoutManager
        val smoothScroller = rv.context.getSmoothScroll(speed)
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }

    fun validateDateRange(fromDate: Long, toDate: Long): Boolean {
        return fromDate <= toDate
    }

    fun showAlertMessage(context: Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    private fun Context.getSmoothScroll(millisecondsPerInch: Float): LinearSmoothScroller {
        //val millisecondsPerInch = 100f; //default is 25f (bigger = slower)

        return object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return millisecondsPerInch / displayMetrics.densityDpi
            }

        }
    }

    fun getFormattedDate(outputPattern:String,date: Date):String{
//        val sdf = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
        val sdf = SimpleDateFormat(outputPattern, Locale.getDefault())
        return sdf.format(date.time)
    }

    fun Context.hideKeyboard(view: View) {
        try {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}