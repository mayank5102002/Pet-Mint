package com.leafcode.petmint.newpet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.ActivityAddPetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

//Activity for new pet addition
class AddPetActivity : AppCompatActivity() {

    //Value for tag for log messages
    private val TAG = "AddPetActivity"

    private lateinit var binding : ActivityAddPetBinding

    //Toolbar and navigation objects
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    //Viewmodel of this activity
    val viewModel : AddPetViewModel by viewModels()

    //Database and Firebase Authentication objects
    private lateinit var db: FirebaseDatabase
    private val auth : FirebaseAuth = Firebase.auth
    private lateinit var myRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setting the toolbar and navController for the activity
        toolbar = binding.addPetToolbar
        navHostFragment = supportFragmentManager.findFragmentById(R.id.add_pet_navigation_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        //Setting the database reference to users
        db = Firebase.database
        myRef = db.getReference(getString(R.string.users_location))

        //Calling the getadid function to update the ad number which is going to be added
        getAdId()

        //Setting back button for the toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Observing the title of the action bar to change it
        viewModel.title.observe(this, Observer {
            supportActionBar?.title = it
        })

        //Observing the ad number to modify the adId on its change
        viewModel.adNumber.observe(this, Observer {
            viewModel.setAdID()
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

    //Overridng back button
    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            // Call finish() on your Activity
            finish()
        }
    }

    //Function to get the ad number for the new ad
    private fun getAdId(){
        //Listener for database

        myRef.child(auth.uid.toString()).child("adNumber").get().addOnSuccessListener {

            if(it.exists()){
                val currentAds = it.getValue<Int>()
                if (currentAds != null){
                        viewModel.setAdNumber(currentAds + 1)
                } else {
                    Log.e("Error","Error in getting AdNumber")
                }
            } else {
                viewModel.setAdNumber(1)
            }

        }.addOnFailureListener {
            Log.e("Error","Error in getting AdNumber : $it")
        }
    }
}