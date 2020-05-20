package com.imb.newtask.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_create_apartment.*
import kotlinx.android.synthetic.main.fragment_dates.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.backArrow
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class DatesFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var monthList: Array<String>
    private var checkInDay = -1
    private var checkInMonth = -1
    private var checkInYear = -1
    private var checkOutDay = 0
    private var checkOutMonth = 0
    private var checkOutYear = 0
    private var startDate = ""
    private var endDate = ""
    private var nights = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)

        navController = Navigation.findNavController(view)

        monthList = resources.getStringArray(R.array.month)


        checkOutCalendar.visibility = View.GONE

        backArrow.setOnClickListener {
            navController.popBackStack()
        }

        checkInLayout.setOnClickListener {
            dateTitle.text = resources.getText(R.string.check_in_text)
            checkInCalendar.visibility = View.VISIBLE
            checkOutCalendar.visibility = View.GONE
        }

        checkOutLayout.setOnClickListener {
            if (checkInDate.text.toString().equals("-"))
                Snackbar.make(view, "First select Check-in date", Snackbar.LENGTH_SHORT).show()
            else {
                dateTitle.text = resources.getText(R.string.check_out_text)
                checkOutCalendar.visibility = View.VISIBLE
                checkInCalendar.visibility = View.GONE
            }
        }


        checkInCalendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            checkInDate.text = "${monthList[month]} $dayOfMonth, $year"
            checkInDay = dayOfMonth
            checkInMonth = month
            checkInYear = year
        }

        checkOutCalendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            if (dayOfMonth < checkInDay || month < checkInMonth || year < checkInYear)
                Snackbar.make(view, "Invalid Date", Snackbar.LENGTH_SHORT).show()
            else {
                checkOutDate.text = "${monthList[month]} $dayOfMonth, $year"
                checkOutMonth = month
                checkOutDay = dayOfMonth
                checkOutYear = year
            }
        }

        saveBtn.setOnClickListener {
            if (!checkInDate.text.toString().equals("-") && !checkOutDate.text.toString().equals("-")) {
                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date1 = Date(
                    checkInYear - 1900,
                    checkInMonth + 1,
                    checkInDay,
                    calendar.time.hours,
                    calendar.time.minutes,
                    calendar.time.seconds
                )
                startDate = simpleDateFormat.format(date1)

                val date2 = Date(
                    checkOutYear - 1900,
                    checkOutMonth + 1,
                    checkOutDay,
                    calendar.time.hours,
                    calendar.time.minutes,
                    calendar.time.seconds
                )
                endDate = simpleDateFormat.format(date2)

                val diff = date2.time - date1.time
                nights = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()

                val editor = sPref.edit()
                editor.putString(
                    "dates",
                    "${monthList[checkInMonth]} $checkInDay - ${monthList[checkOutMonth]} $checkOutDay"
                )
                editor.putString("startDate", "${monthList[checkInMonth]} $checkInDay")
                editor.putString("endDate", "${monthList[checkOutMonth]} $checkOutDay")
                editor.putString("start", startDate)
                editor.putString("end", endDate)
                editor.putInt("nights", nights)
                editor.apply()
                navController.popBackStack()
            } else
                Snackbar.make(view, "Fill up all required fields", Snackbar.LENGTH_SHORT).show()
        }

    }

}
