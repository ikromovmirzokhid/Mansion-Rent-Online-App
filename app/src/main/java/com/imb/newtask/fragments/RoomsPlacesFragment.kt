package com.imb.newtask.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import com.imb.newtask.pojos.RegionsApiResult
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_create_apartment.*
import kotlinx.android.synthetic.main.fragment_rooms_places.*
import kotlinx.android.synthetic.main.fragment_rooms_places.regionSpinner
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.backArrow


class RoomsPlacesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var navController: NavController
    private var roomNum = 0
    private lateinit var userViewModel: UserViewModel
    private lateinit var regionsApiResult: RegionsApiResult
    private lateinit var regionsList: ArrayList<String>
    private var selectedRegion = -1
    private var region = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rooms_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)

        navController = Navigation.findNavController(view)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val token = sPref.getString("token", "")
        regionsList = ArrayList()

        regionSpinner.onItemSelectedListener = this

        backArrow.setOnClickListener {
            navController.popBackStack()
        }

        plusBtn.setOnClickListener {
            roomNum = roomsNumTextView.text.toString().toInt()
            roomsNumTextView.text = (++roomNum).toString()
        }

        minusBtn.setOnClickListener {
            roomNum = roomsNumTextView.text.toString().toInt()
            if (roomNum > 0)
                roomsNumTextView.text = (--roomNum).toString()
        }

        userViewModel.getRegions(token!!).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                regionsApiResult = it
                for (i in 0 until it.regions.size)
                    regionsList.add(it.regions[i].title)

                regionSpinner.prompt = "Select Region"

                if (regionsList.isNotEmpty()) {
                    val adapter =
                        ArrayAdapter(
                            activity!!,
                            android.R.layout.simple_spinner_item,
                            regionsList
                        ).also {
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                    regionSpinner.adapter = adapter
                    regionSpinner.prompt = "Select Region"
                }
            }
        })

        saveBtn.setOnClickListener {
            if (selectedRegion != -1 && roomNum > 0) {
                val editor = sPref.edit()
                editor.putInt("roomNum", roomNum)
                editor.putInt("regionNum", selectedRegion)
                editor.putString("region", region)
                editor.apply()
                navController.popBackStack()
            } else
                Snackbar.make(view, "Fill up all required fields", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedRegion = regionsApiResult.regions[position].id
        region = regionsApiResult.regions[position].title
    }


}
