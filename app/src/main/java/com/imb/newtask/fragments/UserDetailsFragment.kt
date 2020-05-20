package com.imb.newtask.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import com.imb.newtask.pojos.User
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_user_details.*


class UserDetailsFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        navController = Navigation.findNavController(view)

        val sPref = activity!!.getSharedPreferences("User", Context.MODE_PRIVATE)
        val id = sPref.getInt("userId", -1)
        val token = sPref.getString("token", "")
        if (id != -1 && !token.equals("")) {
            userViewModel.getUserById(token!!, id).observe(viewLifecycleOwner, Observer {
                name.setText(it.firstName)
                surname.setText(it.lastName)
                username.setText(it.username)
                dateOfBirth.setText(it.dob)
                phoneNum.setText(it.phone)
                if (it.isOwner)
                    ownerCheck.isChecked = true
                if (it.isAdmin)
                    adminCheck.isChecked = true
                if (it.isStaff)
                    staffCheck.isChecked = true
            })
        }

        editBtn.setOnClickListener {
            saveBtn.visibility = View.VISIBLE
            editBtn.visibility = View.GONE
            setTextFieldsEditable(true)
        }

        saveBtn.setOnClickListener {
            setTextFieldsEditable(false)
            val newUser = User(
                id,
                username.text.toString(),
                name.text.toString(),
                surname.text.toString(),
                dateOfBirth.text.toString(),
                phoneNum.text.toString(),
                staffCheck.isChecked,
                adminCheck.isChecked,
                ownerCheck.isChecked
            )
            userViewModel.updateUserById(token!!, id, newUser)
                .observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        Snackbar.make(view, "Successfully Updated", Snackbar.LENGTH_SHORT).show()
                        saveBtn.visibility = View.GONE
                        editBtn.visibility = View.VISIBLE
                    } else {
                        Snackbar.make(
                            view,
                            "Something went wrong! Please try again",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        saveBtn.visibility = View.VISIBLE
                        editBtn.visibility = View.GONE
                    }
                })
        }

        backArrow.setOnClickListener {
            navController.popBackStack()
        }

    }

    private fun setTextFieldsEditable(isEditable: Boolean) {
        if (isEditable) {
            setViewClickableFocusable(name, true)
            setViewClickableFocusable(surname, true)
            setViewClickableFocusable(username, true)
            setViewClickableFocusable(dateOfBirth, true)
            setViewClickableFocusable(phoneNum, true)
            setViewClickableFocusable(ownerCheck, true)
            setViewClickableFocusable(staffCheck, true)
            setViewClickableFocusable(adminCheck, true)
        } else {
            setViewClickableFocusable(name, false)
            setViewClickableFocusable(surname, false)
            setViewClickableFocusable(username, false)
            setViewClickableFocusable(dateOfBirth, false)
            setViewClickableFocusable(phoneNum, false)
            setViewClickableFocusable(ownerCheck, false)
            setViewClickableFocusable(staffCheck, false)
            setViewClickableFocusable(adminCheck, false)
        }

    }

    private fun setViewClickableFocusable(v: View, setClickable: Boolean) {
        if (setClickable) {
            v.isClickable = true
            v.isFocusable = true

            if (v is EditText) {
                v.isFocusableInTouchMode = true
                v.isCursorVisible = true
            }
        } else {
            v.isClickable = false
            v.isFocusable = false
            if (v is EditText) {
                v.isFocusableInTouchMode = false
                v.isCursorVisible = false
            }
        }
    }

}
