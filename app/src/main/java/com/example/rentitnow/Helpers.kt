package com.example.rentitnow

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.widget.EditText
import com.kaopiz.kprogresshud.KProgressHUD
import java.text.SimpleDateFormat
import java.util.*


class Helpers {
    companion object {
        fun EditText.transformIntoDatePicker(
                context: Context,
                format: String,
                maxDate: Date? = null
        ) {
            isFocusableInTouchMode = false
            isClickable = true
            isFocusable = false

            val myCalendar = Calendar.getInstance()
            val datePickerOnDataSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat(format, Locale.UK)
                    setText(sdf.format(myCalendar.time))
                }

            setOnClickListener {
                DatePickerDialog(
                        context, datePickerOnDataSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).run {
                    maxDate?.time?.also { datePicker.maxDate = it }
                    show()
                }
            }
        }

        fun EditText.transformIntoDatePickerWithMinDate(
                context: Context,
                format: String,
                maxDate: Date? = null,
                minDate: Date? = null
        ) {
            isFocusableInTouchMode = false
            isClickable = true
            isFocusable = false

            val myCalendar = Calendar.getInstance()
            val datePickerOnDataSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat(format, Locale.UK)
                    setText(sdf.format(myCalendar.time))
                    timePicker(context, this)
                }

            setOnClickListener {
                DatePickerDialog(

                        context, datePickerOnDataSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).run {
                    maxDate?.time?.also { datePicker.maxDate = it }
                    minDate?.time.also { datePicker.minDate = Date().time  }
                    show()
                }
            }
        }

        private fun timePicker(context: Context, editText: EditText) {
            // Get Current Time
            val c = Calendar.getInstance()
            var mHour = c[Calendar.HOUR_OF_DAY]
            var mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(context,
                    OnTimeSetListener { view, hourOfDay, minute ->
                        mHour = hourOfDay
                        mMinute = minute
                        if(mHour<10 && mMinute<10) {
                            editText.setText(editText.text.toString() + " " + "0" + mHour + ":" + "0" + minute)
                        }
                        else if(mHour<10) {
                            editText.setText(editText.text.toString() + " " + "0" + mHour + ":" + minute)
                        }
                        else if(mMinute<10) {
                            editText.setText(editText.text.toString() + " " + mHour + ":" +  "0" + minute)
                        }
                        else {
                            editText.setText(editText.text.toString() + " " + mHour + ":" + minute)
                        }

                    }, mHour, mMinute, false)

            timePickerDialog.show()
        }

        fun getLoader(context: Context): KProgressHUD? {
            val loader = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
            return loader
        }

    }

}