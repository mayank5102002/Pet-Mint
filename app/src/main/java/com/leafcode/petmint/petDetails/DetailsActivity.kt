package com.leafcode.petmint.petDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.ActivityDetailsBinding
import com.leafcode.petmint.pet.Pet

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsBinding

    private val viewModel : DetailsActivityViewModel by viewModels()

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        viewModel.setCurrentPet(intent.getParcelableExtra<Pet>("currentPet")!!)

        //Initialising the toolbar and setting it as the support bar
        toolbar = binding.detailsActivityToolbar
        navHostFragment = supportFragmentManager.findFragmentById(R.id.details_activity_support) as NavHostFragment
        navController = navHostFragment.findNavController()

        //Setting the back button for the title bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Making the title as live data to change it from other fragments
        viewModel.title.observe(this, {
            supportActionBar?.title = it
        })

    }

    //On pressing of action bar buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //Popping the current fragment from the backstack
                if (!navController.popBackStack()) {
                    // Call finish() on your Activity
                    finish()
                }
                return true
            }
            else->super.onOptionsItemSelected(item)
        }
    }

    //Overriding onBackPressed function to correctly exit from the fragments and activity
    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            // Call finish() on your Activity
            finish()
        }
    }
}