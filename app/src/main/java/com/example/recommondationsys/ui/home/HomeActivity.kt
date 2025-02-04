package com.example.recommondationsys.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.recommendationsys.ui.home.HomeFragment
//import com.example.recommondationsys.data.SessionManager
import com.example.recommondationsys.ui.auth.AuthActivity
import com.google.android.material.navigation.NavigationView
import com.example.recommondationsys.R
class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /*// 只有在 `savedInstanceState` 为空时才添加 `HomeFragment`
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }*/


        // 绑定 DrawerLayout
//        drawerLayout = findViewById(R.id.drawer_layout)

        // 绑定 NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // 绑定 Navigation Drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.settingFragment),
            drawerLayout
        )

        // 绑定 Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setupWithNavController(navController)

        // 监听点击头像和用户名跳转到 SettingFragment
        val headerView = navigationView.getHeaderView(0)
        val profileImage = headerView.findViewById<ImageView>(R.id.user_avatar)
        val username = headerView.findViewById<TextView>(R.id.user_name)

        profileImage.setOnClickListener {
            navController.navigate(R.id.settingFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        username.setOnClickListener {
            navController.navigate(R.id.settingFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
}
