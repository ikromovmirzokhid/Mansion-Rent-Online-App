package com.imb.newtask.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        progressDialog = ProgressDialog(this)

        val sPref = getSharedPreferences("User", Context.MODE_PRIVATE)

        signIn.setOnClickListener {
            if (!loginEditText.text.isNullOrEmpty()) {
                if (!passwordEditText.text.isNullOrEmpty()) {
                    progressDialog.setTitle("Logging In...")
                    progressDialog.show()
                    val editor = sPref.edit()
                    userViewModel.login(
                        loginEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).observe(this, Observer {
                        if (it != null) {
                            editor.putBoolean("isUserExist", true)
                            editor.putString("userFirstName", it.firstName)
                            editor.putString("userLastName", it.lastName)
                            editor.apply()
                            Snackbar.make(
                                passwordEditText,
                                "Successfully Logged In",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            progressDialog.dismiss()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else
                            progressDialog.dismiss()
                        Snackbar.make(
                            passwordEditText,
                            "Wrong password or username",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    })


                } else {
                    progressDialog.dismiss()
                    passwordEditText.error = "This field can not be empty"
                    Snackbar.make(
                        passwordEditText,
                        "Please fill up every required field",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                loginEditText.error = "This field can not be empty"
                Snackbar.make(
                    loginEditText,
                    "Please fill up every required field",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }


        signUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}
