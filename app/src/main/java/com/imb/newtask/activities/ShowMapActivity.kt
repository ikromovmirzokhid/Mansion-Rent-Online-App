package com.imb.newtask.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.imb.newtask.R
import com.imb.newtask.utils.Constants
import com.imb.newtask.utils.Constants.Companion.locationRequestCode
import com.imb.newtask.utils.GpsUtils
import com.imb.newtask.utils.Place
import kotlinx.android.synthetic.main.activity_show_map.*
import java.util.*

class ShowMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var geocoder: Geocoder
    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isPermissionGranted = false
    private var isGpsOn = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var wayLatitude = 0.0
    private var wayLongtitude = 0.0
    private val UPDATE_INTERVAL = (10 * 1000).toLong()
    private val FASTEST_INTERVAL: Long = 2000
    private var currentPos: LatLng? = null
    private lateinit var selectedPlace: String

    private lateinit var function: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_map)

        function = intent.getStringExtra("function")
        if (function.equals("identify")) {
            // initialization of request of current location
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest.create()
            locationRequest.apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = UPDATE_INTERVAL
                fastestInterval = FASTEST_INTERVAL
            }

            // updated location will come to locationCallback
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    onLocationChanged(locationResult!!.lastLocation)
                }
            }

            geocoder = Geocoder(this, Locale.ENGLISH)

            // checking whether location access permission is granted or not
            checkPermissionGranted()

            //check whether gps on or not. If it is turned off then ask from user to turn on it
            checkGpsStatus()

            getDeviceLocation()

            confirmLocBtn.setOnClickListener {
                val intent = Intent()
                intent.putExtra("latitude", wayLatitude)
                intent.putExtra("longitude", wayLongtitude)
                intent.putExtra("address", selectedPlace)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            locationBtn.setOnClickListener {
                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
            }
        } else if (function.equals("show")) {
            confirmLocBtn.visibility = View.GONE
            locationBtn.visibility = View.GONE
            pinIc.visibility = View.GONE

        }


        //getting map view on the screen
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnBack.setOnClickListener {
            finish()
        }


    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {

            googleMap = p0
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(41.311081, 69.240562),
                    7f
                )
            )
            if (function.equals("identify")) {
                // if permission granted enabling my location
                if (isPermissionGranted) {
                    googleMap.isMyLocationEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = false
                }

                // tracking camera in order to determine marker's position
                googleMap.setOnCameraIdleListener {
                    val latLng = googleMap.cameraPosition.target
                    wayLatitude = latLng.latitude
                    wayLongtitude = latLng.longitude
                    selectedPlace = Place.getPlaceNameFromPosition(latLng, geocoder)
                }
            } else if (function.equals("show")) {
                wayLatitude = intent.getDoubleExtra("lat", 0.0)
                wayLongtitude = intent.getDoubleExtra("lng", 0.0)
                if (wayLatitude != 0.0 && wayLongtitude != 0.0) {
                    currentPos = LatLng(wayLatitude, wayLongtitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15f))

                    googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                wayLatitude,
                                wayLongtitude
                            )
                        ).title("This is your mansion").icon(
                            BitmapDescriptorFactory.fromBitmap(
                                drawBitmap()
                            )
                        )
                    )
                }
            }
        }

    }

    // this function checks permission for devices's location is granted or not
    private fun checkPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationRequestCode
            )
        } else {
            isPermissionGranted = true
        }
    }

    // this function is callback of GpsStatus. Determines whether gps is on or not if gps is off will be asked from user to enable it
    private fun checkGpsStatus() {
        GpsUtils(this)
            .turnGPSOn(object : GpsUtils.OnGpsStatusListener {
                override fun onGpsStatusListener(gpsStatus: Boolean) {
                    isGpsOn = gpsStatus
                }
            })
    }

    // updates device's current location
    private fun onLocationChanged(location: Location) {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)

        wayLatitude = location.latitude
        wayLongtitude = location.longitude

        // saving device's current position
        currentPos = LatLng(wayLatitude, wayLongtitude)

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15f))
    }

    // this function gets device's current location if permission is granted and
    private fun getDeviceLocation() {
        if (isPermissionGranted || isGpsOn) {
            mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
                // if location is came successfully then moving camera to that position
                if (it != null) {
                    wayLatitude = it.latitude
                    wayLongtitude = it.longitude
                    currentPos = LatLng(wayLatitude, wayLongtitude)
                    googleMap.isMyLocationEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = false
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15f))
                }
                // if current location is null updating location through locationRequest
                else {
                    mFusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null
                    )
                }
            }
        }
    }

    // result of request of permission about device's current location comes to this function
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationRequestCode ->
                // if permission is granted then checking gps status and if gps is on identifying device's location
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true
                    checkGpsStatus()
                    getDeviceLocation()
                } else {
                    isPermissionGranted = false
                    //locationBtn.visibility = View.GONE
                    Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //if gps is off, turning gps on will be asked from user if he/she accepts response comes here
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if user turned gps on then determines user's location
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGpsOn = true
                getDeviceLocation()
            }
        }
        // if rejects turning on of gps, will be asked enable gps until user does
        else {
            checkGpsStatus()
            getDeviceLocation()
        }
    }

    private fun drawBitmap(): Bitmap? {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.pin_icon
        )
        return Bitmap.createScaledBitmap(bitmap, 120, 120, false)
    }
}
