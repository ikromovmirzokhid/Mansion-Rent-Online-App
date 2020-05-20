package com.imb.newtask.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
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
import com.imb.newtask.dialogs.RatingDialog
import com.imb.newtask.pojos.BookDetails
import com.imb.newtask.room.FavoriteMansion
import com.imb.newtask.room.RecentSearchedMansion
import com.imb.newtask.utils.Constants
import com.imb.newtask.utils.Converter
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_mansion_detail.*
import kotlinx.android.synthetic.main.fragment_settings.backArrow

class MansionDetailFragment : Fragment(), RatingDialog.RateMansion, OnMapReadyCallback {

    private lateinit var navController: NavController
    private lateinit var mansion: RecentSearchedMansion
    private lateinit var userViewModel: UserViewModel
    private var startDate = ""
    private var enddate = ""
    private lateinit var rateDialog: RatingDialog
    private var token = ""
    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var currentPos: LatLng? = null
    private lateinit var dialIntent: Intent
    private lateinit var favMansion: FavoriteMansion
    private var mansionLiked = false
    private var startDateOtherFormat = ""
    private var endDateOtherFormat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mansionJson = arguments!!.getString("mansion")
        startDate = arguments!!.getString("start")!!
        enddate = arguments!!.getString("end")!!
        startDateOtherFormat = arguments!!.getString("startDate")!!
        endDateOtherFormat = arguments!!.getString("endDate")!!

        if (!mansionJson.equals(""))
            mansion = Gson().fromJson(mansionJson, RecentSearchedMansion::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mansion_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)
        token = sPref.getString("token", "")!!

        navController = Navigation.findNavController(view)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        rateDialog = RatingDialog(activity, this)
        rateDialog.setCancelable(false)
        rateDialog.setCanceledOnTouchOutside(false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        backArrow.setOnClickListener {
            rateDialog.show()
        }

        initializeViews(mansion)

        bookBtn.setOnClickListener {
            val bookDetails = BookDetails(startDate, enddate, false, mansion.mansionId)
            userViewModel.bookMansion(token, bookDetails).observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Snackbar.make(
                        view,
                        "Mansion is successfully booked",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    rateDialog.show()
                } else {
                    Snackbar.make(
                        view,
                        "Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
        }

        btnCall.setOnClickListener {
            dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mansion.owner.phone))
            startActivity(dialIntent)
        }

        likeBtn.setOnClickListener {
            mansionLiked = if (mansionLiked) {
                userViewModel.deleteMansionFromFavoriteDB(favMansion)
                Glide.with(activity!!)
                    .load(activity!!.resources.getDrawable(R.drawable.ic_favorite_border_black_24dp))
                    .into(likeBtn)
                Snackbar.make(
                    view,
                    "Mansion is removed from favorites",
                    Snackbar.LENGTH_SHORT
                ).show()
                false
            } else {
                userViewModel.insertNewFavouriteMansion(Converter.convertToFavoriteMansion(mansion))
                Glide.with(activity!!)
                    .load(activity!!.resources.getDrawable(R.drawable.ic_favorite_red_24dp))
                    .into(likeBtn)
                Snackbar.make(
                    view,
                    "Mansion is added to favorites",
                    Snackbar.LENGTH_SHORT
                ).show()
                true
            }
        }


    }

    private fun initializeViews(mansion: RecentSearchedMansion) {
        if (mansion.image != "") {
            Glide.with(this).load(Constants.BASE_URL + mansion.image)
                .into(mansionPic)
        } else
            Glide.with(this).load(activity!!.resources.getDrawable(R.drawable.mansion))
                .into(mansionPic)

        mansionName.text = mansion.title
        ratingBar.rating = mansion.rates.avgRate.toFloat()

        if (!mansion.facilities.swimmingPool) {
            swimmingPoolIcon.visibility = View.GONE
            swimmingText.visibility = View.GONE
        }
        if (!mansion.facilities.pool) {
            poolIcon.visibility = View.GONE
            poolT.visibility = View.GONE
        }
        if (!mansion.facilities.playStation) {
            playIcon.visibility = View.GONE
            playText.visibility = View.GONE
        }
        if (!mansion.facilities.tv) {
            tvIcon.visibility = View.GONE
            tvText1.visibility = View.GONE
        }

        checkInDate.text = startDateOtherFormat
        checkOutDate.text = endDateOtherFormat

        addressText1.text = mansion.address
        phoneNumText.text = mansion.owner.phone

        userViewModel.getFavoriteMansionById(mansion.mansionId).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                favMansion = it
                Glide.with(activity!!)
                    .load(activity!!.resources.getDrawable(R.drawable.ic_favorite_red_24dp))
                    .into(likeBtn)
                mansionLiked = true
            } else {
                Glide.with(activity!!)
                    .load(activity!!.resources.getDrawable(R.drawable.ic_favorite_border_black_24dp))
                    .into(likeBtn)
                mansionLiked = false
            }
        })

    }

    override fun rateMansion(rating: Float) {
        userViewModel.rateMansionById(token, rating.toInt(), mansion.mansionId)
            .observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Snackbar.make(
                        bookBtn,
                        "Thanks for your rate!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                } else {
                    Snackbar.make(
                        bookBtn,
                        "Something went wrong, Please try again after a while",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }
            })
    }

    override fun notNowBtnClicked() {
        Snackbar.make(
            bookBtn,
            "Please rate next time",
            Snackbar.LENGTH_SHORT
        ).show()
        navController.popBackStack()
    }

    private fun drawBitmap(): Bitmap? {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.pin_icon
        )
        return Bitmap.createScaledBitmap(bitmap, 120, 120, false)
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
            currentPos = LatLng(mansion.latitude.toDouble(), mansion.longitude.toDouble())

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15f))

            googleMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        mansion.latitude.toDouble(),
                        mansion.longitude.toDouble()
                    )
                )
                    .icon(BitmapDescriptorFactory.fromBitmap(drawBitmap())).title(mansion.title)
            )
        }
    }

}