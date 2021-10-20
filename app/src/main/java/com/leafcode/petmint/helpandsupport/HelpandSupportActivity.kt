package com.leafcode.petmint.helpandsupport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.ActivityHelpSupportBinding

//Help and Support Activity
class HelpandSupportActivity : AppCompatActivity() {

    //Declaring variables
    private lateinit var binding : ActivityHelpSupportBinding

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private val viewModel : HelpandSupportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialising the toolbar and setting it as the support bar
        toolbar = binding.helpSupportToolbar
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_help_support) as NavHostFragment
        navController = navHostFragment.findNavController()

        //Setting the back button for the title bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Making the title as live data to change it from other fragments
        viewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })

    }

    //On pressing of action bar buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                //Popping the current fragment from the backstack
                if (!navController.popBackStack()) {
                    // Call finish() on your Activity
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Overriding onBackPressed function to correctly exit from the fragments and activity
    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            // Call finish() on your Activity
            finish()
        }
    }
}