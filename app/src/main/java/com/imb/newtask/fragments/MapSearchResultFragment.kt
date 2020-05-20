package com.imb.newtask.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import com.imb.newtask.adapters.MapMansionsListAdapter
import com.imb.newtask.adapters.SearchedMansionsListAdapter
import com.imb.newtask.pojos.Mansion
import com.imb.newtask.pojos.MansionsResult
import com.imb.newtask.utils.Place
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_map_search_result.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.backArrow


class MapSearchResultFragment : Fragment(), MapMansionsListAdapter.OnMansionClickListener,
    OnMapReadyCallback {

    private lateinit var navController: NavController
    private lateinit var adapter: MapMansionsListAdapter
    private lateinit var mansionsResult: MansionsResult
    private var startDate = ""
    private var endDate = ""
    private var nights = -1
    private var rooms = -1
    private var dates = ""
    private var startDateOtherFormat = ""
    private var endDateOtherFormat = ""
    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var wayLatitude = 0.0
    private var wayLongtitude = 0.0
    private var currentPos: LatLng? = null
    private lateinit var mansionList: List<Mansion>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        backArrow.setOnClickListener {
            navController.popBackStack()
        }

        val resultJson = arguments!!.getString("searchedMansionsResult")
        mansionsResult = Gson().fromJson(resultJson, MansionsResult::class.java)

        mansionList = mansionsResult.mansions

        nights = arguments!!.getInt("nights")
        startDate = arguments!!.getString("start")!!
        endDate = arguments!!.getString("end")!!
        rooms = arguments!!.getInt("rooms")
        dates = arguments!!.getString("dates")!!
        startDateOtherFormat = arguments!!.getString("startDate")!!
        endDateOtherFormat = arguments!!.getString("endDate")!!

        adapter = MapMansionsListAdapter(mansionsResult, nights)
        adapter.setMansionClickListener(this)

        mansionListView.adapter = adapter
        mansionListView.layoutManager = LinearLayoutManager(activity)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


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
            if (mansionList != null) {
                for (i in 0 until mansionList.size) {
                    wayLatitude = mansionList[i].latitude.toDouble()
                    wayLongtitude = mansionList[i].longitude.toDouble()
                    if (wayLatitude != 0.0 && wayLongtitude != 0.0) {
                        currentPos = LatLng(wayLatitude, wayLongtitude)

                        googleMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    wayLatitude,
                                    wayLongtitude
                                )
                            ).title(mansionList[i].title).icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    drawBitmap()
                                )
                            )
                        )
                    }
                }
            }
        }


    }

    override fun onMansionClickListener(bundle: Bundle) {
        bundle.putString("start", startDate)
        bundle.putString("end", endDate)
        bundle.putInt("nights", nights)
        bundle.putString("startDate", startDateOtherFormat)
        bundle.putString("endDate", endDateOtherFormat)
        navController.navigate(R.id.action_mapSearchResultFragment_to_mansionDetailFragment, bundle)
    }


    private fun drawBitmap(): Bitmap? {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.pin_icon
        )
        return Bitmap.createScaledBitmap(bitmap, 120, 120, false)
    }
}
