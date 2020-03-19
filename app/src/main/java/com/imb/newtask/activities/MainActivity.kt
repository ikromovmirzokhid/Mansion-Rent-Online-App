package com.imb.newtask.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import com.google.android.material.snackbar.Snackbar
import com.imb.newtask.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var sPref: SharedPreferences
    private lateinit var userFirstName: String
    private lateinit var userLastName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sPref = getSharedPreferences("User", Context.MODE_PRIVATE)
        val isUserExist = sPref.getBoolean("isUserExist", false)
        if (!isUserExist) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {

            userFirstName = sPref.getString("userFirstName", "")!!
            userLastName = sPref.getString("userLastName", "")!!


            val view = nView.getHeaderView(0)
            if (!userFirstName.equals("") && !userLastName.equals(""))
                view.fullName.text = "${userFirstName} ${userLastName}"

            Snackbar.make(nView, "Welcome ${userFirstName} ${userLastName}", Snackbar.LENGTH_SHORT)
                .show()

            nView.setNavigationItemSelectedListener {
                if (it.itemId == R.id.logOut) {
                    finish()
                    val editor = sPref.edit()
                    editor.putBoolean("isUserExist", false)
                    editor.apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    dLayout.closeDrawers()
                }
                return@setNavigationItemSelectedListener true
            }

            navigationToggle.setOnClickListener {
                dLayout.openDrawer(Gravity.LEFT)
            }
        }
    }
}
