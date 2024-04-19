package com.mydatetimepicker.utill

import androidx.annotation.Keep
import java.util.Calendar

@Keep
interface OnDateTimeSelectedListener {
    fun onDateTimeSelected(selectedDateTime: Calendar)
}