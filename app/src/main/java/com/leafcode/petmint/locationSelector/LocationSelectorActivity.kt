package com.leafcode.petmint.locationSelector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.ActivityLocationSelectorBinding

class LocationSelectorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLocationSelectorBinding

    //Viewmodel of this activity
    val viewModel : LocationSelectorViewModel by viewModels()

    // Objects for the layout
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setting up the toolbar and navController for the activity
        toolbar = binding.locationSelectorToolbar
        navHostFragment = supportFragmentManager.findFragmentById(R.id.location_selector_navigation_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        //Setting the back button for the action bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Making the title of the action bar to be live data so as to change it from various fragments
        viewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })


    }

    //On pressing of action bar buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                if (!navController.popBackStack()) {
                    // Call finish() on your Activity
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Overriding back button to ensure correct backing up
    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            // Call finish() on your Activity
            finish()
        }
    }
}