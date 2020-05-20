package com.imb.newtask.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import com.imb.newtask.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)

        navController = Navigation.findNavController(view)

        backArrow.setOnClickListener {
            navController.popBackStack()
        }

        userDetails.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_userDetailsFragment)
        }

        logOut.setOnClickListener {
            val editor = sPref.edit()
            editor.putBoolean("isUserExist", false)
            editor.putInt("userId", -1)
            editor.apply()
            Snackbar.make(view, "Goodbye, Comeback Soon!", Snackbar.LENGTH_SHORT).show()
            navController.navigate(R.id.action_settingsFragment_to_loginActivity)
            (activity as MainActivity).finish()
        }

    }

}
