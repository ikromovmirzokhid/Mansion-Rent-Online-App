package com.imb.newtask.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import com.imb.newtask.activities.ShowMapActivity
import com.imb.newtask.pojos.*
import com.imb.newtask.utils.Constants.Companion.EXTERNAL_STORAGE_REQUEST
import com.imb.newtask.utils.Constants.Companion.PICK_FROM_GALLERY
import com.imb.newtask.utils.Constants.Companion.REQUEST_CODE
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_create_apartment.*
import kotlinx.android.synthetic.main.fragment_settings.backArrow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CreateApartmentFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var regionsList: ArrayList<String>
    private var selectedRegion = -1
    private var lng = 0.0
    private var lat = 0.0
    private lateinit var addressLoc: String
    private lateinit var regionsApiResult: RegionsApiResult
    private lateinit var facilities: ArrayList<Facility>
    private var imgPathUri: Uri? = null
    private lateinit var userViewModel: UserViewModel

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

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        navController = Navigation.findNavController(view)
        regionsList = ArrayList()
        facilities = ArrayList()


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

        showMapBtn.setOnClickListener {
            val intent = Intent(activity, ShowMapActivity::class.java)
            intent.putExtra("function", "identify")
            startActivityForResult(intent, REQUEST_CODE)
        }

        mPicture.setOnClickListener {
            askPermissionForExternalStorage()
        }

        createBtn.setOnClickListener {
            if (!title.text.isNullOrEmpty() && !address.text.isNullOrEmpty() && !rooms.text.isNullOrEmpty() && !price.text.isNullOrEmpty() &&
                selectedRegion != -1 && lng != 0.0 && lat != 0.0 && imgPathUri != null
            ) {
                val newMansion = NewMansion(
                    title.text.toString(), address.text.toString(), lat.toString(), lng.toString(),
                    rooms.text.toString().toInt(), price.text.toString(), selectedRegion
                )

                facilities.clear()
                if (tvCheck.isChecked)
                    facilities.add(Facility("TV"))
                if (poolCheck.isChecked)
                    facilities.add(Facility("Pool"))
                if (playSCheck.isChecked)
                    facilities.add(Facility("Playstation"))
                if (swimCheck.isChecked)
                    facilities.add(Facility("Swimming Pool"))

                userViewModel.createNewMansion(token, newMansion)
                    .observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            val newFacilities = NewFacilities(it.id, facilities)
                            userViewModel.createFacilities(token, newFacilities)
                                .observe(viewLifecycleOwner, Observer {
                                    if (it != null) {
                                        if (uploadImage(token, imgPathUri!!.path, it.mansionId)) {
                                            Snackbar.make(
                                                view,
                                                "Mansion successfully created!",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        } else
                                            Snackbar.make(
                                                view,
                                                "Something went wrong",
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                    } else
                                        Snackbar.make(
                                            view,
                                            "Something went wrong",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                })


                        } else {
                            Snackbar.make(
                                view,
                                "Something went wrong",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })

            } else
                Snackbar.make(
                    view,
                    "Please fill up all required fields!",
                    Snackbar.LENGTH_SHORT
                ).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            addressLoc = data!!.getStringExtra("address")
            lat = data.getDoubleExtra("latitude", 0.0)
            lng = data.getDoubleExtra("longitude", 0.0)
            address.text = addressLoc
        } else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            imgPathUri = data!!.data
            Glide.with(this).load(imgPathUri).into(mPicture)
        }
    }

    private fun askPermissionForExternalStorage() {

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                EXTERNAL_STORAGE_REQUEST
            )
        } else {
            val gallery = Intent(Intent.ACTION_GET_CONTENT)
            gallery.type = "image/*"
            startActivityForResult(gallery, PICK_FROM_GALLERY)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val gallery = Intent(Intent.ACTION_GET_CONTENT)
                gallery.type = "image/*"
                startActivityForResult(gallery, PICK_FROM_GALLERY)
            } else {
                Toast.makeText(activity, "Permission is denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(token: String, imgPath: String?, mansionId: Int): Boolean {
        Log.d("ImageUri", imgPath)
        val file = File(imgPath)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val part = MultipartBody.Part.createFormData("picture", file.name, requestBody)
        var success = false
        val mansionID = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "$mansionId")


        userViewModel.uploadMansionPicture(token, mansionID, part)
            .observe(viewLifecycleOwner, Observer {
                if (it != null)
                    success = true
            })
        return success
    }

}
