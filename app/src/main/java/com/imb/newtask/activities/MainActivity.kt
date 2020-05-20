package com.imb.newtask.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.imb.newtask.R
import com.imb.newtask.room.Database
import com.imb.newtask.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var sPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Database.init(this)

        sPref = getSharedPreferences("User", Context.MODE_PRIVATE)
        val isUserExist = sPref.getBoolean("isUserExist", false)
        val token = sPref.getString("token", "")

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val id = sPref.getInt("userId", -1)
        if (!isUserExist) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val inflater = navHostFragment.navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_main)

            if (id != -1 && !token.equals("")) {
                userViewModel.getUserById(token!!, id).observe(this, Observer {
                    if (it.isOwner)
                        graph.startDestination = R.id.homePageFragment
                    else
                        graph.startDestination = R.id.clientHomePageFragment

                    navHostFragment.navController.graph = graph
                })

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val editor = sPref.edit()
        editor.putString("dates", "")
        editor.putString("region", "")
        editor.putInt("roomNum", -1)
        editor.putInt("regionNum", -1)
        editor.putString("start", "")
        editor.putString("end", "")
        editor.putInt("nights", -1)
        editor.putString("startDate", "")
        editor.putString("endDate", "")
        editor.apply()
    }

}
