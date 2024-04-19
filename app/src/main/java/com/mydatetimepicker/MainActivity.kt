package com.mydatetimepicker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mydatetimepicker.databinding.ActivityMainBinding
import com.mydatetimepicker.ui.BottomSheetDateTimePicker
import com.mydatetimepicker.ui.DateListActivity
import com.mydatetimepicker.ui.DatePickerSheet
import com.mydatetimepicker.utill.CommonFunction
import com.mydatetimepicker.utill.OnDateTimeSelectedListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtShowBottom.setOnClickListener {
            val startDate: Calendar = Calendar.getInstance()
            val modal = BottomSheetDateTimePicker(startDate,12, dateTimeSelectedListener = {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

               val date = dateFormat.format(it.time)

                Log.e("SELECTED_DATE",">> $date")
            })
            supportFragmentManager.let { modal.show(it, "BottomSheetDateTimePicker") }

        }
        binding.txtShow.setOnClickListener {
            val startDate: Calendar = Calendar.getInstance()
            val dateTimeSelectedListener = object :
                OnDateTimeSelectedListener {
                override fun onDateTimeSelected(selectedDateTime: Calendar) {
                    Toast.makeText(
                        this@MainActivity,
                        "Selected date ${selectedDateTime.time}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val dialog = DatePickerSheet(
                this,
                startDate,
                12,
                dateTimeSelectedListener,
                "Select date and time"
            )

            dialog.setTitleTextColor(R.color.colorPrimaryDark)

            dialog.setDividerBgColor(R.color.colorAccent)

            dialog.setCancelBtnColor(R.color.colorPrimaryDark)
            dialog.setCancelBtnTextColor(R.color.colorAccent)

            dialog.setSubmitBtnColor(R.color.colorPrimaryDark)
            dialog.setSubmitBtnTextColor(R.color.colorAccent)

            dialog.setCancelBtnText("Cancel")
            dialog.setSubmitBtnText("Submit")
            dialog.setFontSize(18)
            dialog.setCenterDividerHeight(48)

            dialog.show()
        }

        binding.txtGenerateDate.setOnClickListener {
           startActivity(Intent(this
           ,DateListActivity::class.java))

        }
    }
}