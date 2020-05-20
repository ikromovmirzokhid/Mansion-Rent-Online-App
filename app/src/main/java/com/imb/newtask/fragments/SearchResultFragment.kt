package com.imb.newtask.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import com.imb.newtask.adapters.SearchedMansionsListAdapter
import com.imb.newtask.pojos.MansionsResult
import kotlinx.android.synthetic.main.activity_show_map.*
import kotlinx.android.synthetic.main.activity_show_map.btnBack
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.rView
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.fragment_settings.*


class SearchResultFragment : Fragment(), SearchedMansionsListAdapter.OnMansionClickListener {

    private lateinit var navController: NavController
    private lateinit var adapter: SearchedMansionsListAdapter
    private lateinit var mansionsResult: MansionsResult
    private var startDate = ""
    private var endDate = ""
    private var nights = -1
    private var rooms = -1
    private var dates = ""
    private var startDateOtherFormat = ""
    private var endDateOtherFormat = ""
    private lateinit var bundle1: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)

        navController = Navigation.findNavController(view)

        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        arguments.let {
            bundle1 = it!!
        }

        val resultJson = arguments!!.getString("searchedMansionsResult")
        mansionsResult = Gson().fromJson(resultJson, MansionsResult::class.java)

        nights = arguments!!.getInt("nights")
        startDate = arguments!!.getString("start")!!
        endDate = arguments!!.getString("end")!!
        rooms = arguments!!.getInt("rooms")
        dates = arguments!!.getString("dates")!!
        startDateOtherFormat = arguments!!.getString("startDate")!!
        endDateOtherFormat = arguments!!.getString("endDate")!!

        adapter = SearchedMansionsListAdapter(mansionsResult, nights)
        adapter.setMansionClickListener(this)

        rView.adapter = adapter
        rView.layoutManager = LinearLayoutManager(activity)

        datesTextView.text = dates
        roomsTextView.text = "$rooms guests, ${rooms} rooms"
        regionName.text = mansionsResult.mansions[0].region.title

        mapBtn.setOnClickListener {
            navController.navigate(
                R.id.action_searchResultFragment_to_mapSearchResultFragment,
                bundle1
            )
        }

    }

    override fun onMansionClickListener(bundle: Bundle) {
        bundle.putString("start", startDate)
        bundle.putString("end", endDate)
        bundle.putInt("nights", nights)
        bundle.putString("startDate", startDateOtherFormat)
        bundle.putString("endDate", endDateOtherFormat)
        navController.navigate(R.id.action_searchResultFragment_to_mansionDetailFragment, bundle)
    }

}
