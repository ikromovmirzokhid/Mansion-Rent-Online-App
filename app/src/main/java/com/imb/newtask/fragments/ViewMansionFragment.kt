package com.imb.newtask.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import com.imb.newtask.activities.ShowMapActivity
import com.imb.newtask.pojos.*
import com.imb.newtask.utils.Constants
import com.imb.newtask.utils.Constants.Companion.BASE_URL
import com.imb.newtask.utils.Constants.Companion.REQUEST_CODE
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_create_apartment.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.backArrow
import kotlinx.android.synthetic.main.fragment_user_details.*
import kotlinx.android.synthetic.main.mansions_list_item_view.view.*


class ViewMansionFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var mansion: Mansion
    private lateinit var regionsList: ArrayList<String>
    private var selectedRegion = -1
    private var lng = 0.0
    private var lat = 0.0
    private lateinit var addressLoc: String
    private lateinit var regionsApiResult: RegionsApiResult
    private var isTvChecked = false
    private var isPoolChecked = false
    private var isSwimChecked = false
    private var isPlaysChecked = false
    private var tvPos = -1
    private var poolPos = -1
    private var playPos = -1
    private var swimPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mansionJson = arguments!!.getString("mansion")
        if (!mansionJson.equals(""))
            mansion = Gson().fromJson(mansionJson, Mansion::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_apartment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)
        val token = sPref.getString("token", "")

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        regionsList = ArrayList()


        navController = Navigation.findNavController(view)

        createBtn.visibility = View.GONE
        update.visibility = View.VISIBLE

        setTextFieldsEditable(false)
        initializeViews(mansion)

        lat = mansion.latitude.toDouble()
        lng = mansion.longitude.toDouble()

        var regionPos = -1

        userViewModel.getRegions(token!!).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                regionsApiResult = it
                for (i in 0 until it.regions.size) {
                    regionsList.add(it.regions[i].title)
                    if (mansion.region.title.equals(it.regions[i].title))
                        regionPos = i
                }

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
                    if (regionPos != -1)
                        regionSpinner.setSelection(regionPos)
                }
            }
        })

        showMapBtn.setOnClickListener {
            if (save.isVisible) {

                val intent = Intent(activity, ShowMapActivity::class.java)
                intent.putExtra("function", "identify")
                startActivityForResult(intent, REQUEST_CODE)

            } else if (update.isVisible) {

                val intent = Intent(activity, ShowMapActivity::class.java)
                intent.putExtra("function", "show")
                intent.putExtra("lat", lat)
                intent.putExtra("lng", lng)
                startActivity(intent)
            }
        }

        update.setOnClickListener {
            update.visibility = View.GONE
            save.visibility = View.VISIBLE
            setTextFieldsEditable(true)
        }

        save.setOnClickListener {
            if (!title.text.isNullOrEmpty() && !address.text.isNullOrEmpty() && !rooms.text.isNullOrEmpty() && !price.text.isNullOrEmpty() &&
                selectedRegion != -1 && lng != 0.0 && lat != 0.0
            ) {
                val newMansion = NewMansion(
                    title.text.toString(),
                    address.text.toString(),
                    lat.toString(),
                    lng.toString(),
                    rooms.text.toString().toInt(),
                    price.text.toString(),
                    selectedRegion
                )

                userViewModel.updateMansion(token, mansion.id, newMansion)
                    .observe(viewLifecycleOwner, Observer {

                        if (it != null) {
                            if (it.facilities != null)
                                for (i in 0 until it.facilities.size) {
                                    val facility = it.facilities[i]
                                    when {
                                        facility.name.equals("TV") -> {
                                            tvPos = i
                                        }
                                        facility.name.equals("Pool") -> {
                                            poolPos = i
                                        }
                                        facility.name.equals("Playstation") -> {
                                            playPos = i
                                        }
                                        facility.name.equals("Swimming Pool") -> {
                                            swimPos = i
                                        }
                                    }
                                }

                            if (tvCheck.isChecked != isTvChecked) {
                                if (isTvChecked) {
                                    userViewModel.deleteFacility(
                                        token,
                                        mansion.facilities[tvPos].id
                                    )
                                } else {
                                    val newFacility = ArrayList<Facility>()
                                    newFacility.add(Facility("TV"))
                                    userViewModel.createFacilities(
                                        token,
                                        NewFacilities(it.id, newFacility)
                                    )
                                }

                            }
                            if (poolCheck.isChecked != isPoolChecked) {
                                if (isPoolChecked) {
                                    userViewModel.deleteFacility(
                                        token,
                                        mansion.facilities[poolPos].id
                                    )
                                } else {
                                    val newFacility = ArrayList<Facility>()
                                    newFacility.add(Facility("Pool"))
                                    userViewModel.createFacilities(
                                        token,
                                        NewFacilities(it.id, newFacility)
                                    )
                                }

                            }
                            if (playSCheck.isChecked != isPlaysChecked) {
                                if (isPlaysChecked) {
                                    userViewModel.deleteFacility(
                                        token,
                                        mansion.facilities[playPos].id
                                    )
                                } else {
                                    val newFacility = ArrayList<Facility>()
                                    newFacility.add(Facility("Playstation"))
                                    userViewModel.createFacilities(
                                        token,
                                        NewFacilities(it.id, newFacility)
                                    )
                                }

                            }
                            if (swimCheck.isChecked != isSwimChecked) {
                                if (isSwimChecked) {
                                    userViewModel.deleteFacility(
                                        token,
                                        mansion.facilities[swimPos].id
                                    )
                                } else {
                                    val newFacility = ArrayList<Facility>()
                                    newFacility.add(Facility("Swimming Pool"))
                                    userViewModel.createFacilities(
                                        token,
                                        NewFacilities(it.id, newFacility)
                                    )
                                }

                            }

                            Snackbar.make(
                                view,
                                "Mansion successfully updated!",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            save.visibility = View.GONE
                            update.visibility = View.VISIBLE
                            setTextFieldsEditable(false)

                            tText.text = it.title
                        } else {
                            Snackbar.make(
                                view,
                                "Something went wrong! Please try again",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })

            } else {
                Snackbar.make(
                    view,
                    "All fields should be filled up properly!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        regionSpinner.onItemSelectedListener = this

        backArrow.setOnClickListener {
            navController.popBackStack()
        }


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedRegion = regionsApiResult.regions[position].id
    }

    private fun initializeViews(mansion: Mansion) {
        tText.text = mansion.title
        title.setText(mansion.title)
        rooms.setText(mansion.numOfRooms.toString())
        price.setText(mansion.price)
        address.setText(mansion.address)

        if (mansion.images != null)
            Glide.with(this).load(BASE_URL + mansion.images[0].picture)
                .into(mPicture)
        else
            Glide.with(this).load(activity!!.resources.getDrawable(R.drawable.mansion))
                .into(mPicture)

        if (mansion.facilities != null)
            for (i in 0 until mansion.facilities.size) {
                val facility = mansion.facilities[i]
                when {
                    facility.name.equals("TV") -> {
                        tvCheck.isChecked = true
                        isTvChecked = true
                        tvPos = i
                    }
                    facility.name.equals("Pool") -> {
                        poolCheck.isChecked = true
                        isPoolChecked = true
                        poolPos = i
                    }
                    facility.name.equals("Playstation") -> {
                        playSCheck.isChecked = true
                        isPlaysChecked = true
                        playPos = i
                    }
                    facility.name.equals("Swimming Pool") -> {
                        swimCheck.isChecked = true
                        isSwimChecked = true
                        swimPos = i
                    }
                }
            }
    }

    private fun setTextFieldsEditable(isEditable: Boolean) {
        if (isEditable) {
            setViewClickableFocusable(title, true)
            setViewClickableFocusable(rooms, true)
            setViewClickableFocusable(price, true)
            setViewClickableFocusable(regionSpinner, true)
            setViewClickableFocusable(tvCheck, true)
            setViewClickableFocusable(poolCheck, true)
            setViewClickableFocusable(playSCheck, true)
            setViewClickableFocusable(swimCheck, true)
        } else {
            setViewClickableFocusable(title, false)
            setViewClickableFocusable(rooms, false)
            setViewClickableFocusable(price, false)
            setViewClickableFocusable(regionSpinner, false)
            setViewClickableFocusable(tvCheck, false)
            setViewClickableFocusable(poolCheck, false)
            setViewClickableFocusable(playSCheck, false)
            setViewClickableFocusable(swimCheck, false)
        }

    }

    private fun setViewClickableFocusable(v: View, setClickable: Boolean) {
        if (setClickable) {
            v.isClickable = true
            v.isFocusable = true
            if (v is EditText) {
                v.isCursorVisible = true
                v.isFocusableInTouchMode = true
            }
            if (v is Spinner)
                v.isEnabled = true
        } else {
            v.isClickable = false
            v.isFocusable = false
            if (v is EditText) {
                v.isCursorVisible = false
                v.isFocusableInTouchMode = false
            }
            if (v is Spinner)
                v.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            addressLoc = data!!.getStringExtra("address")
            lat = data.getDoubleExtra("latitude", 0.0)
            lng = data.getDoubleExtra("longitude", 0.0)
            address.text = addressLoc
        }
    }

}
