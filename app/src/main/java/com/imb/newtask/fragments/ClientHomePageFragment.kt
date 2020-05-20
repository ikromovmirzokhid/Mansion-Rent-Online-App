package com.imb.newtask.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import com.imb.newtask.adapters.MansionsListAdapter
import com.imb.newtask.adapters.RecentSearchedMansionsListAdapter
import com.imb.newtask.adapters.SearchedMansionsListAdapter
import com.imb.newtask.room.RecentSearchedMansion
import com.imb.newtask.utils.Converter
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_client_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.dLayout
import kotlinx.android.synthetic.main.fragment_home_page.nView
import kotlinx.android.synthetic.main.fragment_home_page.navigationToggle
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*


class ClientHomePageFragment : Fragment(),
    RecentSearchedMansionsListAdapter.OnMansionClickListener {

    private lateinit var navController: NavController
    private lateinit var sPref: SharedPreferences
    private var dates = ""
    private var roomNum = -1
    private var regionNum = -1
    private var region = ""
    private var startDate = ""
    private var endDate = ""
    private var nights = -1
    private var startDateOtherFormat = ""
    private var endDateOtherFormat = ""
    private lateinit var adapter: RecentSearchedMansionsListAdapter
    private lateinit var recentSearchedMansionsList: List<RecentSearchedMansion>
    private lateinit var recentSearchedMansion: RecentSearchedMansion
    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_client_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        navController = Navigation.findNavController(view)

        val navHeaderView = nView.getHeaderView(0)

        val id = sPref.getInt("userId", -1)
        val token = sPref.getString("token", "")
        if (id != -1 && !token.equals("")) {
            userViewModel.getUserById(token!!, id).observe(viewLifecycleOwner,
                Observer {
                    if (it != null)
                        navHeaderView.fullName.text = "${it.firstName} ${it.lastName}"
                })
        }

        bundle = Bundle()


        nView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.settings) {
                navController.navigate(R.id.action_clientHomePageFragment_to_settingsFragment)
            } else if (it.itemId == R.id.favorites)
                navController.navigate(
                    R.id.action_clientHomePageFragment_to_favoriteMansionsFragment,
                    bundle
                )

            dLayout.closeDrawer(Gravity.LEFT)
            return@setNavigationItemSelectedListener true
        }

        userViewModel.getAllRecentSearchedMansions().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                notFoundIcon.visibility = View.GONE
                noResultMessage.visibility = View.GONE
                recentSearchedMansionsList = it
                adapter = RecentSearchedMansionsListAdapter(it)
                adapter.setMansionClickListener(this)
                recentSearchedMansionsListView.adapter = adapter
                recentSearchedMansionsListView.layoutManager = GridLayoutManager(activity, 2)
            } else {
                recentSearchedMansionsList = it
                notFoundIcon.visibility = View.VISIBLE
                noResultMessage.visibility = View.VISIBLE
                noResultMessage.text = "No recent searched mansions yet"
            }
        })

        navigationToggle.setOnClickListener {
            dLayout.openDrawer(Gravity.LEFT)
        }

        datesLayout.setOnClickListener {
            navController.navigate(R.id.action_clientHomePageFragment_to_datesFragment)
        }

        roomsLayout.setOnClickListener {
            navController.navigate(R.id.action_clientHomePageFragment_to_roomsPlacesFragment)
        }

        searchBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (regionSearch.text.toString() != "Choose Region" && roomsTextView.text.toString() != "-" && datesTextView.text.toString() != "-") {
                userViewModel.filterMansionListByRegionAndRooms(token!!, regionNum, roomNum)
                    .observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            if (it.count > 0) {
                                progressBar.visibility = View.GONE
                                notFoundIcon.visibility = View.GONE
                                noResultMessage.visibility = View.GONE
                                if (recentSearchedMansionsList != null) {
                                    if (recentSearchedMansionsList.isEmpty()) {
                                        if (it.mansions.size >= 2) {
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[0])
                                            userViewModel.insertRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[1])
                                            userViewModel.insertRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                        } else {
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[0])
                                            userViewModel.insertRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                        }
                                    } else if (recentSearchedMansionsList.size == 1) {
                                        if (it.mansions.size >= 2) {
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[0])
                                            recentSearchedMansion.id = 1
                                            userViewModel.updateRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[1])
                                            userViewModel.insertRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                        } else {
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[0])
                                            userViewModel.insertRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                        }
                                    } else if (recentSearchedMansionsList.size == 2) {
                                        if (it.mansions.size >= 2) {
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[0])
                                            recentSearchedMansion.id = 1
                                            userViewModel.updateRecentSearchedMansion(
                                                recentSearchedMansion
                                            )

                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[1])
                                            recentSearchedMansion.id = 2
                                            userViewModel.updateRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                        } else {
                                            recentSearchedMansion =
                                                Converter.convertToRecentSearchedMansion(it.mansions[0])
                                            recentSearchedMansion.id = 1
                                            userViewModel.updateRecentSearchedMansion(
                                                recentSearchedMansion
                                            )
                                        }
                                    }
                                }

                                val resultJson = Gson().toJson(it)
                                bundle.putString("searchedMansionsResult", resultJson)

                                navController.navigate(
                                    R.id.action_clientHomePageFragment_to_searchResultFragment,
                                    bundle
                                )

                            } else {
                                progressBar.visibility = View.GONE
                                notFoundIcon.visibility = View.VISIBLE
                                noResultMessage.visibility = View.VISIBLE

                            }
                        } else {
                            Snackbar.make(
                                view,
                                "Something went wrong!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            progressBar.visibility = View.GONE
                        }
                    })
            } else {
                progressBar.visibility = View.GONE
                Snackbar.make(view, "Fill up all required fields", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        dates = sPref.getString("dates", "")!!
        roomNum = sPref.getInt("roomNum", -1)
        regionNum = sPref.getInt("regionNum", -1)
        region = sPref.getString("region", "")!!
        startDate = sPref.getString("start", "")!!
        nights = sPref.getInt("nights", -1)
        endDate = sPref.getString("end", "")!!
        startDateOtherFormat = sPref.getString("startDate", "")!!
        endDateOtherFormat = sPref.getString("endDate", "")!!
        if (!region.equals(""))
            regionSearch.setText(region)
        if (roomNum != -1)
            roomsTextView.text = roomNum.toString()
        if (dates != "")
            datesTextView.text = dates

        bundle.putString("start", startDate)
        bundle.putString("end", endDate)
        bundle.putInt("rooms", roomNum)
        bundle.putString("dates", dates)
        bundle.putInt("nights", nights)
        bundle.putString("startDate", startDateOtherFormat)
        bundle.putString("endDate", endDateOtherFormat)

    }

    override fun onMansionClickListener(bundle: Bundle) {
        bundle.putString("start", startDate)
        bundle.putString("end", endDate)
        bundle.putString("startDate", startDateOtherFormat)
        bundle.putString("endDate", endDateOtherFormat)
        navController.navigate(R.id.action_clientHomePageFragment_to_mansionDetailFragment, bundle)
    }
}
