// MainActivity.kt
package com.example.recommondationsys

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.recommendationsys.ui.home.HomeFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var favouritesListView: ListView
    private val favouritesAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "Current layout is activity_main")

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Display navigation button
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

//        toggle = ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ).apply {
            drawerLayout.addDrawerListener(this)
            syncState()
        }
        Log.d("MainActivity", "ActionBarDrawerToggle initialized")

        // Add listener for manual drawer toggle
        toolbar.setNavigationOnClickListener {
            Log.d("MainActivity", "Toolbar navigation clicked")
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                Log.d("MainActivity", "Opening drawer")
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                Log.d("MainActivity", "Closing drawer")
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        // Initialize favourites list in sidebar
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0) // 获取 NavigationView 的头部视图
        favouritesListView = headerView.findViewById(R.id.favourites_list)
        favouritesListView.adapter = favouritesAdapter

        // Load default fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        Log.d("MainActivity", "Existing fragment: $currentFragment")

        if (currentFragment == null || currentFragment !is HomeFragment) {
            Log.d("MainActivity", "Loading HomeFragment for the first time")

            val homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit()
            // Update favourites in sidebar when drawer opens
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
                override fun onDrawerOpened(drawerView: View) {
                    // Get favourites from HomeFragment and update the adapter
                    favouritesAdapter.clear()
                    //favouritesAdapter.addAll(homeFragment.getFavourites())
                    favouritesAdapter.notifyDataSetChanged()
                }

                override fun onDrawerClosed(drawerView: View) {}
                override fun onDrawerStateChanged(newState: Int) {}
            })
        }else {
            Log.d("MainActivity", "HomeFragment already loaded")
        }

    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        Log.d("MainActivity", "onOptionsItemSelected triggered")

        if (toggle.onOptionsItemSelected(item)) {
            Log.d("MainActivity", "Drawer toggle handled the event")

            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
