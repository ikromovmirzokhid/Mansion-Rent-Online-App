package com.imb.newtask.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.imb.newtask.R
import com.imb.newtask.adapters.FavoriteMansionsListAdapter
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_favorite_mansions.*

class FavoriteMansionsFragment : Fragment(), FavoriteMansionsListAdapter.OnMansionClickListener {

    private lateinit var navController: NavController
    private lateinit var adapter: FavoriteMansionsListAdapter
    private var startDate = ""
    private var endDate = ""
    private var nights = -1
    private var startDateOtherFormat = ""
    private var endDateOtherFormat = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_mansions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = Navigation.findNavController(view)

        backArrow.setOnClickListener {
            navController.popBackStack()
        }

        nights = arguments!!.getInt("nights")
        startDate = arguments!!.getString("start")!!
        endDate = arguments!!.getString("end")!!
        startDateOtherFormat = arguments!!.getString("startDate")!!
        endDateOtherFormat = arguments!!.getString("endDate")!!

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.getAllFavoriteMansions().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                notFoundIcon.visibility = View.GONE
                noResultMessage.visibility = View.GONE
                adapter = FavoriteMansionsListAdapter(it, nights, this)
                adapter.setMansionClickListener(this)
                favoriteMansionsListView.adapter = adapter
                favoriteMansionsListView.layoutManager = LinearLayoutManager(activity)
            } else {
                notFoundIcon.visibility = View.VISIBLE
                noResultMessage.visibility = View.VISIBLE
            }
        })

    }

    override fun onMansionClickListener(bundle: Bundle) {
        bundle.putString("start", startDate)
        bundle.putString("end", endDate)
        bundle.putInt("nights", nights)
        bundle.putString("startDate", startDateOtherFormat)
        bundle.putString("endDate", endDateOtherFormat)
        navController.navigate(
            R.id.action_favoriteMansionsFragment_to_mansionDetailFragment,
            bundle
        )
    }

}