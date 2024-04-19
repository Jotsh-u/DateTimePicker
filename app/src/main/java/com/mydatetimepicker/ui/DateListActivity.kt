package com.mydatetimepicker.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mydatetimepicker.R
import com.mydatetimepicker.adapter.AdapterDateList
import com.mydatetimepicker.adapter.AdapterMeridianList
import com.mydatetimepicker.adapter.AdapterMinutesList
import com.mydatetimepicker.databinding.ActivityDatesListBinding
import com.mydatetimepicker.utill.CommonFunction
import com.mydatetimepicker.utill.CommonFunction.addEmptyValue
import com.mydatetimepicker.utill.CommonFunction.addEmptyValueInString
import com.mydatetimepicker.utill.CommonFunction.generateListOfDates
import com.mydatetimepicker.utill.CommonFunction.getFormattedDate
import com.mydatetimepicker.utill.CommonFunction.getHours
import com.mydatetimepicker.utill.CommonFunction.getInitHourIndex
import com.mydatetimepicker.utill.CommonFunction.getInitMeridianIndex
import com.mydatetimepicker.utill.CommonFunction.getInitMinuteIndex
import com.mydatetimepicker.utill.CommonFunction.getMeridian
import com.mydatetimepicker.utill.CommonFunction.getMinutes
import com.mydatetimepicker.utill.CommonFunction.getYearWiseDate
import com.mydatetimepicker.utill.CommonFunction.hideKeyboard
import com.mydatetimepicker.utill.CommonFunction.isValidDate
import com.mydatetimepicker.utill.CommonFunction.showAlertMessage
import com.mydatetimepicker.utill.CommonFunction.smoothScrollToTop
import com.mydatetimepicker.utill.CommonFunction.validateDateRange
import com.mydatetimepicker.utill.CustomsSnapHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class DateListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDatesListBinding
    private var list: ArrayList<Calendar> = arrayListOf()
    private var listHour: ArrayList<Int> = arrayListOf()
    private var listMin: ArrayList<Int> = arrayListOf()
    private var listMeridian: ArrayList<String> = arrayListOf()
    private var adapterList: AdapterDateList? = null
    private var adapterHourList: AdapterMinutesList? = null
    private var adapterMinList: AdapterMinutesList? = null
    private var adapterMeridianList: AdapterMeridianList? = null
    private var adapterToList: AdapterDateList? = null
    private var adapterToHourList: AdapterMinutesList? = null
    private var adapterToMinList: AdapterMinutesList? = null
    private var adapterToMeridianList: AdapterMeridianList? = null

    companion object {
        private const val YEAR_COUNTER: Int = -1
        private const val VISIBLE_THRESH_HOLD = 5
        const val SLOW_SPEED = 100F
        const val FAST_SPEED = 25F
        //From Today Date
        private var todayDate: Calendar = Calendar.getInstance()
        //From Selected Date
        private val fromSelectedDate: Calendar = Calendar.getInstance()
        //To Selected Date
        private val toSelectedDate: Calendar = Calendar.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //FROM
        binding.rvDateListView.setHasFixedSize(true)
        binding.rvDateListView.setItemViewCacheSize(10)
        binding.rvFromHour.setHasFixedSize(true)
        binding.rvFromHour.setItemViewCacheSize(10)
        binding.rvFromMinute.setHasFixedSize(true)
        binding.rvFromMinute.setItemViewCacheSize(10)
        binding.rvFromMeridian.setHasFixedSize(true)
        binding.rvFromMeridian.setItemViewCacheSize(10)
        binding.rvDateListView.layoutManager = LinearLayoutManager(this)
        binding.rvFromHour.layoutManager = LinearLayoutManager(this)
        binding.rvFromMinute.layoutManager = LinearLayoutManager(this)
        binding.rvFromMeridian.layoutManager = LinearLayoutManager(this)
        //TO
        binding.rvToDateListView.setHasFixedSize(true)
        binding.rvToDateListView.setItemViewCacheSize(10)
        binding.rvToHour.setHasFixedSize(true)
        binding.rvToHour.setItemViewCacheSize(10)
        binding.rvToMinute.setHasFixedSize(true)
        binding.rvToMinute.setItemViewCacheSize(10)
        binding.rvToMeridian.setHasFixedSize(true)
        binding.rvToMeridian.setItemViewCacheSize(10)
        binding.rvToDateListView.layoutManager = LinearLayoutManager(this)
        binding.rvToHour.layoutManager = LinearLayoutManager(this)
        binding.rvToMinute.layoutManager = LinearLayoutManager(this)
        binding.rvToMeridian.layoutManager = LinearLayoutManager(this)

        setDateAdapter()
        setHourAdapter()
        setMinAdapter()
        setMeridianAdapter()

        setToDateAdapter()
        setToHourAdapter()
        setToMinAdapter()
        setToMeridianAdapter()

        setOnClickListener()

        fetchDatesList()
        adapterList?.notifyDataSetChanged()
        adapterToList?.notifyDataSetChanged()

        resetDateData(SLOW_SPEED)
        resetToDateData(SLOW_SPEED)
    }

    private fun setOnClickListener() {

        binding.txtCancel.setOnClickListener {
            finish()
        }

        binding.txtReset.setOnClickListener {
            resetDateData(SLOW_SPEED)
            resetToDateData(SLOW_SPEED)
        }

      /*  binding.txtFilterApply.setOnClickListener {
            this.hideKeyboard(it)
        val yearValue = binding.edtYear.text
            val tempList = getYearWiseDate(yearValue.toString().toInt())
            list.clear()
            if (tempList.size > 0)
                list = tempList

            adapterList?.notifyDataSetChanged()
            adapterToList?.notifyDataSetChanged()
        }*/

        binding.txtDone.setOnClickListener {
            val sdf = SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", Locale.ENGLISH)

            val fromDate = sdf.format(fromSelectedDate.timeInMillis)
            val toDate = sdf.format(toSelectedDate.timeInMillis)

            val diffInMillis = toSelectedDate.timeInMillis - fromSelectedDate.timeInMillis
            val days = diffInMillis.milliseconds.toInt(DurationUnit.DAYS)

            if (!validateDateRange(fromSelectedDate.timeInMillis, toSelectedDate.timeInMillis)) {
                showAlertMessage(this, getString(R.string.error_select_valid_range_date_time))
            } else if (days > 31) {
                showAlertMessage(this, getString(R.string.error_selection_range_validation))
                resetDateData(FAST_SPEED)
                resetToDateData(FAST_SPEED)
            } else {
                //VALID RANGE DATE HERE DO SOMETHING HERE WITH THAT RANGE
                Log.e("fromSelectedDate", "From date>> $fromDate To date>> $toDate")
            }
        }

        binding.rvDateListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount =
                    (binding.rvDateListView.layoutManager as LinearLayoutManager).childCount
                val totalItemCount =
                    (binding.rvDateListView.layoutManager as LinearLayoutManager).itemCount
                val firstItemVisible =
                    (binding.rvDateListView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if ((totalItemCount - visibleItemCount)
                    <= (firstItemVisible + VISIBLE_THRESH_HOLD)
                ) {
                    // End has been reached
//                    fetchDatesList()
//                    adapterList?.notifyDataSetChanged()
                }

            }
        })

        binding.rvToDateListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount =
                    (binding.rvToDateListView.layoutManager as LinearLayoutManager).childCount
                val totalItemCount =
                    (binding.rvToDateListView.layoutManager as LinearLayoutManager).itemCount
                val firstItemVisible =
                    (binding.rvToDateListView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if ((totalItemCount - visibleItemCount)
                    <= (firstItemVisible + VISIBLE_THRESH_HOLD)
                ) {
                    // End has been reached
                    fetchDatesList()
                    adapterToList?.notifyDataSetChanged()
                }

            }
        })
    }

    private fun fetchDatesList() {
        val startDate = if (list.size > 2) {
            val index = list.size - 1
            list[index]
        } else {
            Calendar.getInstance()
        }
        val endDate = Calendar.getInstance().also {
            it.timeInMillis = startDate?.timeInMillis!!
            it.add(Calendar.YEAR, YEAR_COUNTER)
        }

        val tempList = generateListOfDates(startDate, endDate, list.size)
        if (tempList.size > 0)
            list.addAll(tempList)

    }

    private fun setDateAdapter() {
        adapterList = AdapterDateList(list)
        binding.rvDateListView.adapter = adapterList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                Log.e(
                    "POSITION",
                    "Position:> $position From Date:> ${getFormattedDate("EEE dd MMM yyyy",list[position].time)}"
                )
                fromSelectedDate.set(Calendar.DAY_OF_YEAR, list[position].get(Calendar.DAY_OF_YEAR))
                fromSelectedDate.set(Calendar.YEAR, list[position].get(Calendar.YEAR))
//                validateDateTime()
            }
        }
        CustomsSnapHelper(binding.rvDateListView, dateSnapListener)
    }

    private fun setToDateAdapter() {
        adapterToList = AdapterDateList(list)
        binding.rvToDateListView.adapter = adapterToList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {

                Log.e(
                    "POSITION",
                    "Position:> $position To Date:> ${getFormattedDate("EEE dd MMM yyyy",list[position].time)}"
                )
                toSelectedDate.set(Calendar.DAY_OF_YEAR, list[position].get(Calendar.DAY_OF_YEAR))
                toSelectedDate.set(Calendar.YEAR, list[position].get(Calendar.YEAR))
                validateToDateTime()
            }
        }
        CustomsSnapHelper(binding.rvToDateListView, dateSnapListener)
    }

    private fun setHourAdapter() {
        listHour = addEmptyValue(getHours(false))
        adapterHourList = AdapterMinutesList(listHour)
        binding.rvFromHour.adapter = adapterHourList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                Log.e("POSITION", "Position:> $position From Date Hour:> ${listHour[position]}")
                fromSelectedDate.set(Calendar.HOUR_OF_DAY, listHour[position])
                validateDateTime()
            }
        }
        CustomsSnapHelper(binding.rvFromHour, dateSnapListener)
    }

    private fun setToHourAdapter() {
        listHour = addEmptyValue(getHours(false))
        adapterToHourList = AdapterMinutesList(listHour)
        binding.rvToHour.adapter = adapterToHourList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                Log.e("POSITION", "Position:> $position To Date Hour:> ${listHour[position]}")
                toSelectedDate.set(Calendar.HOUR_OF_DAY, listHour[position])
                validateToDateTime()
            }
        }
        CustomsSnapHelper(binding.rvToHour, dateSnapListener)
    }

    private fun setMinAdapter() {
        listMin = addEmptyValue(getMinutes())
        adapterMinList = AdapterMinutesList(listMin)
        binding.rvFromMinute.adapter = adapterMinList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                Log.e("POSITION", "Position:> $position From Date Min:> ${(listMin[position])}")
                fromSelectedDate.set(Calendar.MINUTE, listMin[position])
                validateDateTime()
            }
        }
        CustomsSnapHelper(binding.rvFromMinute, dateSnapListener)
    }

    private fun setToMinAdapter() {
        listMin = addEmptyValue(getMinutes())
        adapterToMinList = AdapterMinutesList(listMin)
        binding.rvToMinute.adapter = adapterToMinList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                Log.e("POSITION", "Position:> $position To Date Min:> ${(listMin[position])}")
                toSelectedDate.set(Calendar.MINUTE, listMin[position])
                validateToDateTime()
            }
        }
        CustomsSnapHelper(binding.rvToMinute, dateSnapListener)
    }

    private fun setMeridianAdapter() {
        listMeridian = addEmptyValueInString(getMeridian())
        adapterMeridianList = AdapterMeridianList(listMeridian)
        binding.rvFromMeridian.adapter = adapterMeridianList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                Log.e(
                    "POSITION",
                    "Position:> $position From Date Meridian:> ${(listMeridian[position])}"
                )
                if (listMeridian[position] == "AM")
                    fromSelectedDate.set(Calendar.AM_PM, Calendar.AM)
                else if (listMeridian[position] == "PM")
                    fromSelectedDate.set(Calendar.AM_PM, Calendar.PM)

                validateDateTime()
            }
        }
        CustomsSnapHelper(binding.rvFromMeridian, dateSnapListener)
    }

    private fun setToMeridianAdapter() {
        listMeridian = addEmptyValueInString(getMeridian())
        adapterToMeridianList = AdapterMeridianList(listMeridian)
        binding.rvToMeridian.adapter = adapterToMeridianList

        val dateSnapListener = object : CustomsSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                Log.e(
                    "POSITION",
                    "Position:> $position TO Date Meridian:> ${(listMeridian[position])}"
                )
                if (listMeridian[position] == "AM")
                    toSelectedDate.set(Calendar.AM_PM, Calendar.AM)
                else if (listMeridian[position] == "PM")
                    toSelectedDate.set(Calendar.AM_PM, Calendar.PM)

                validateToDateTime()
            }
        }
        CustomsSnapHelper(binding.rvToMeridian, dateSnapListener)
    }

    private fun isStoppedScrolling(): Boolean {
        return binding.rvDateListView.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvFromHour.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvFromMinute.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvFromMeridian.scrollState == RecyclerView.SCROLL_STATE_IDLE
    }

    private fun isToRVStoppedScrolling(): Boolean {
        return binding.rvToDateListView.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvToHour.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvToMinute.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvToMeridian.scrollState == RecyclerView.SCROLL_STATE_IDLE
    }

    private fun validateDateTime() {
        if (isStoppedScrolling()) {
            if (isValidDate(fromSelectedDate, todayDate))
                setSelectedDateTime()
            else
                resetDateData(SLOW_SPEED)
        }
    }

    private fun validateToDateTime() {
        if (isToRVStoppedScrolling()) {
            if (isValidDate(toSelectedDate, todayDate))
                setSelectedToDateTime()
            else
                resetToDateData(SLOW_SPEED)
        }
    }

    private fun resetDateData(resetSpeed: Float) {
        todayDate = Calendar.getInstance()
        resetDate(binding.rvDateListView, resetSpeed)
        resetMeridian(binding.rvFromMeridian, resetSpeed)
        resetHour(binding.rvFromHour, resetSpeed)
        resetMinute(binding.rvFromMinute, resetSpeed)
    }

    private fun resetToDateData(resetSpeed: Float) {
        todayDate = Calendar.getInstance()
        resetDate(binding.rvToDateListView, resetSpeed)
        resetMeridian(binding.rvToMeridian, resetSpeed)
        resetHour(binding.rvToHour, resetSpeed)
        resetMinute(binding.rvToMinute, resetSpeed)
    }

    private fun resetDate(rv: RecyclerView, scrollSpeed: Float) {
        smoothScrollToTop(rv, 1, scrollSpeed)
    }

    private fun resetHour(rv: RecyclerView, scrollSpeed: Float) {
        val index = getInitHourIndex(todayDate)
        println("hour index $index")
        smoothScrollToTop(rv, index, scrollSpeed)
    }

    private fun resetMinute(rv: RecyclerView, scrollSpeed: Float) {
        val index = getInitMinuteIndex(todayDate)
        println("minute index $index")
        smoothScrollToTop(rv, index, scrollSpeed)
    }

    private fun resetMeridian(rv: RecyclerView, scrollSpeed: Float) {
        smoothScrollToTop(rv, getInitMeridianIndex(todayDate), scrollSpeed)
    }

    private fun setSelectedDateTime() {
        fromSelectedDate.timeInMillis = fromSelectedDate.timeInMillis
    }

    private fun setSelectedToDateTime() {
        toSelectedDate.timeInMillis = toSelectedDate.timeInMillis
    }
}