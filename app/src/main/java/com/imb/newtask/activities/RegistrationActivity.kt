package com.imb.newtask.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import com.imb.newtask.pojos.UserRegister
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        progressDialog = ProgressDialog(this)

        register.setOnClickListener {
            if (!name.text.isNullOrEmpty() && !surname.text.isNullOrEmpty() && !username.text.isNullOrEmpty() && !password.text.isNullOrEmpty() &&
                !dateOfBirth.text.isNullOrEmpty() && !phoneNum.text.isNullOrEmpty()
            ) {
                progressDialog.setTitle("Registering...")
                progressDialog.show()
                val isOwner = ownerCheck.isChecked
                val user = UserRegister(
                    username.text.toString(),
                    name.text.toString(),
                    surname.text.toString(),
                    dateOfBirth.text.toString(),
                    phoneNum.text.toString(),
                    isOwner,
                    password.text.toString()
                )

                Log.d("User", user.toString())

                userViewModel.register(user).observe(this, Observer {
                    if (it != null) {
                        progressDialog.dismiss()
                        Snackbar.make(register, "Successfully Registered", Snackbar.LENGTH_SHORT)
                            .show()
                        this.finish()
                    } else {
                        progressDialog.dismiss()
                        Snackbar.make(
                            register,
                            "Something went wrong! Please try again",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                })

            } else {
                Snackbar.make(register, "All fields must be filled up", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        backArrow.setOnClickListener {
            this.finish()
        }
    }
}
