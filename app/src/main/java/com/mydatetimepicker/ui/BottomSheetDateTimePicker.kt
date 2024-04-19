package com.mydatetimepicker.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mydatetimepicker.adapter.DateAdapter
import com.mydatetimepicker.adapter.HourAdapter
import com.mydatetimepicker.adapter.MeridianAdapter
import com.mydatetimepicker.adapter.MinuteAdapter
import com.mydatetimepicker.databinding.BottomSheetFromToDateTimeBinding
import com.mydatetimepicker.utill.CustomSnapHelper
import com.mydatetimepicker.utill.DatePickerUtils
import com.mydatetimepicker.utill.FAST_SPEED
import com.mydatetimepicker.utill.PM
import com.mydatetimepicker.utill.SLOW_SPEED
import com.mydatetimepicker.utill.getFormattedHour
import java.util.Calendar
import java.util.Locale

class BottomSheetDateTimePicker(
//    private val endDate: Calendar,
    private val startDate: Calendar,
    private val maxMonthToDisplay: Int,
    private val dateTimeSelectedListener: (Calendar) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFromToDateTimeBinding
    private lateinit var utils: DatePickerUtils

    private var endDate: Calendar = Calendar.getInstance().also {
        it.timeInMillis = startDate.timeInMillis
        it.add(Calendar.MONTH, maxMonthToDisplay)
    }

//    private var startDate: Calendar = Calendar.getInstance().also {
//        it.timeInMillis = endDate.timeInMillis
//        it.add(Calendar.MONTH, maxMonthToDisplay)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFromToDateTimeBinding.inflate(inflater, container, false)
        init()
        setOnClickListener()
        return binding.root
    }

    private fun setOnClickListener() {
        binding.txtDone.setOnClickListener {
            dateTimeSelectedListener.invoke(utils.selectedDateTime)
        }
        binding.txtCancel.setOnClickListener { dismiss() }
    }

    private fun init() {
        utils = DatePickerUtils(startDate, endDate)

        binding.rvFromDate.setHasFixedSize(true)
        binding.rvFromDate.setItemViewCacheSize(10)
        binding.rvFromHour.setHasFixedSize(true)
        binding.rvFromHour.setItemViewCacheSize(10)
        binding.rvFromMinute.setHasFixedSize(true)
        binding.rvFromMinute.setItemViewCacheSize(10)
        binding.rvFromMeridian.setHasFixedSize(true)
        binding.rvFromMeridian.setItemViewCacheSize(10)

        binding.rvToDate.setHasFixedSize(true)
        binding.rvToDate.setItemViewCacheSize(10)
        binding.rvToHour.setHasFixedSize(true)
        binding.rvToHour.setItemViewCacheSize(10)
        binding.rvToMinute.setHasFixedSize(true)
        binding.rvToMinute.setItemViewCacheSize(10)
        binding.rvToMeridian.setHasFixedSize(true)
        binding.rvToMeridian.setItemViewCacheSize(10)

        binding.rvFromDate.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFromHour.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFromMinute.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFromMeridian.layoutManager = LinearLayoutManager(requireActivity())

        binding.rvToDate.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvToHour.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvToMinute.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvToMeridian.layoutManager = LinearLayoutManager(requireActivity())
        setDateAdapter()
        setMinuteAdapter()
        setHourAdapter()
        setMeridianAdapter()
        initDates(FAST_SPEED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = false
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    private fun setDateAdapter() {
        val dateAdapter = DateAdapter(utils.getAllDates(), 14, 38)
        binding.rvFromDate.adapter = dateAdapter
        binding.rvToDate.adapter = dateAdapter

        val dateSnapListener = object : CustomSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                utils.setSelectedDate(
                    dateAdapter.dates[position].get(Calendar.DAY_OF_YEAR),
                    dateAdapter.dates[position].get(Calendar.YEAR)
                )
                validateDateTime()
            }
        }
        CustomSnapHelper(binding.rvFromDate, dateSnapListener)
//        CustomSnapHelper(binding.rvToDate, dateSnapListener)

    }

    private fun setHourAdapter() {
        val hourAdapter = HourAdapter(
            utils.addEmptyValue(
                utils.getHours(false)
            ),
            14,
            38
        )
        binding.rvFromHour.adapter = hourAdapter
        binding.rvToHour.adapter = hourAdapter

        val hourSnapListener = object : CustomSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                println("hour snapped position $position")
                if (position >= 3) {
                    utils.currentSelectedHour = hourAdapter.hour[position]
                    println("current selected hour ${utils.currentSelectedHour}")
                    utils.setSelectedHour(
                        getFormattedHour(
                            utils.isPmSelectedUnvalidated,
                            hourAdapter.hour[position]
                        )
                    )
                    validateDateTime()
                } else {
                    utils.setMinimumHour(binding.rvFromHour)
                    utils.setMinimumHour(binding.rvToHour)
                }
            }
        }
        CustomSnapHelper(binding.rvFromHour, hourSnapListener)
//        CustomSnapHelper(binding.rvToHour, hourSnapListener)
    }

    private fun setMinuteAdapter() {
        val minuteAdapter =
            MinuteAdapter(
                utils.addEmptyValue(utils.getMinutes()),
                14,
                38
            )
        binding.rvFromMinute.adapter = minuteAdapter
        binding.rvToMinute.adapter = minuteAdapter


        val minuteSnapListener = object : CustomSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                if (position >= 3) {
                    utils.setSelectedMinute(minuteAdapter.minute[position])
                    validateDateTime()
                } else {
                    utils.setMinimumMinutes(binding.rvFromMinute)
                    utils.setMinimumMinutes(binding.rvToMinute)
                }
            }
        }
        CustomSnapHelper(binding.rvFromMinute, minuteSnapListener)
//        CustomSnapHelper(binding.rvToMinute, minuteSnapListener)
    }

    private fun setMeridianAdapter() {
        val meridianAdapter =
            MeridianAdapter(
                utils.addEmptyValueInString(utils.getMeridiem()),
                14,
                38
            )
        binding.rvFromMeridian.adapter = meridianAdapter
        binding.rvToMeridian.adapter = meridianAdapter

        val meridiemSnapListener = object : CustomSnapHelper.SnapListener {
            override fun onViewSnapped(position: Int) {
                utils.isPmSelectedUnvalidated = meridianAdapter.meridiem[position] == PM
                utils.setSelectedHour(
                    getFormattedHour(
                        utils.isPmSelectedUnvalidated,
                        utils.currentSelectedHour
                    )
                )
                validateDateTime()
            }
        }
        CustomSnapHelper(binding.rvFromMeridian, meridiemSnapListener)
//        CustomSnapHelper(binding.rvToMeridian, meridiemSnapListener)
    }

    private fun initDates(scrollSpeed: Float) {
        utils.resetDate(binding.rvFromDate, scrollSpeed)
        utils.resetMeridiem(binding.rvFromMeridian, scrollSpeed)
        utils.resetHour(binding.rvFromHour, scrollSpeed)
        utils.resetMinute(binding.rvFromMinute, scrollSpeed)
        
       /* utils.resetDate(binding.rvToDate, scrollSpeed)
        utils.resetMeridiem(binding.rvToMeridian, scrollSpeed)
        utils.resetHour(binding.rvToHour, scrollSpeed)
        utils.resetMinute(binding.rvToMinute, scrollSpeed)*/
    }


    private fun isStoppedScrolling(): Boolean {
        return binding.rvFromDate.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvFromHour.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvFromMinute.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                binding.rvFromMeridian.scrollState == RecyclerView.SCROLL_STATE_IDLE
    }

    private fun validateDateTime() {
        if (isStoppedScrolling()) {
            if (utils.isValidDate())
                utils.setSelectedDateTime()
            else
                initDates(SLOW_SPEED)
        }
    }
    
}