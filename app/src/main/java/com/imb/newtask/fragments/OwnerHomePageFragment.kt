package com.imb.newtask.fragments

import android.content.Context
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.imb.newtask.R
import com.imb.newtask.adapters.MansionsListAdapter
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*


class OwnerHomePageFragment : Fragment(), MansionsListAdapter.OnMansionClickListener {

    private lateinit var navController: NavController
    private lateinit var adapter: MansionsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        navController = Navigation.findNavController(view)

        val navHeaderView = nView.getHeaderView(0)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)
        val id = sPref.getInt("userId", -1)
        val token = sPref.getString("token", "")
        if (id != -1 && !token.equals("")) {
            userViewModel.getUserById(token!!, id).observe(viewLifecycleOwner,
                Observer {
                    if (it != null)
                        navHeaderView.fullName.text = "${it.firstName} ${it.lastName}"
                })

            userViewModel.getOwnerMansionsById(token, id).observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    if (it.count > 0) {
                        noMansionsLayout.visibility = View.GONE
                        adapter = MansionsListAdapter(it)
                        adapter.setMansionClickListener(this)
                        rView.adapter = adapter
                        rView.layoutManager = LinearLayoutManager(activity)
                        dLayout.setBackgroundColor(resources.getColor(R.color.grey))
                    } else {
                        createNewBtn.hide()
                        noMansionsLayout.visibility = View.VISIBLE
                        createBtn.setOnClickListener {
                            navController.navigate(R.id.action_homePageFragment_to_createApartmentFragment)
                        }
                    }
                }
            })

            createNewBtn.setOnClickListener {
                navController.navigate(R.id.action_homePageFragment_to_createApartmentFragment)
            }
        }

        nView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.settings) {
                navController.navigate(R.id.action_homePageFragment_to_settingsFragment)
            }
            dLayout.closeDrawer(Gravity.LEFT)
            return@setNavigationItemSelectedListener true
        }

        navigationToggle.setOnClickListener {
            dLayout.openDrawer(Gravity.LEFT)
        }

    }

    override fun onMansionClickListener(bundle: Bundle) {
        navController.navigate(R.id.action_homePageFragment_to_viewMansionFragment, bundle)
    }

}
