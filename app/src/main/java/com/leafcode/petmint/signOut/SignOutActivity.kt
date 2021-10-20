package com.leafcode.petmint.signOut

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.leafcode.petmint.MainActivity
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.ActivitySignOutBinding
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignOutActivity : AppCompatActivity() {

    //Object for binding the layout
    private lateinit var binding: ActivitySignOutBinding

    private val viewModel : SignOutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signOutButton = binding.signoutButton
        val deleteAccountButton = binding.deleteAccountButton
        val toolbar = binding.signOutToolbar

        val signOutActivityAd = binding.adView

        val adRequest = AdRequest.Builder().build()
        signOutActivityAd.loadAd(adRequest)

        //Setting the support action bar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Click listeners
        signOutButton.setOnClickListener {
            signOut()
        }

        deleteAccountButton.setOnClickListener {
            deleteAccount()
        }

        viewModel.adlocations.observe(this,{
            if (it == true){
                viewModel.deleteUserData()
                viewModel.adlocationsExecuted()
            }
        })

        viewModel.userDataDelete.observe(this,{
            if (it == true){
                Log.e("user data observe", "user data deleted")
                viewModel.deleteUserFinal()
                viewModel.userDataDeleted()
            }
        })

        viewModel.user.observe(this,{
            if (it == true){
                Log.e("user observe", "user deleted")
                viewModel.userDeleteSuccessfull()
                Toast.makeText(this,"Account deleted",Toast.LENGTH_SHORT).show()
                goToMainActivity()
            }
        })

        viewModel.error.observe(this,{
            if (it == true){
                Toast.makeText(this,"Some error occured",Toast.LENGTH_SHORT).show()
                viewModel.errorResolved()
            }
        })

        viewModel.userDeleteError.observe(this,{
            if (it == true){
                Toast.makeText(this,"Your data has been deleted,\nPlease sign-in again to delete your account permanently"
                ,Toast.LENGTH_SHORT).show()
                viewModel.userDeleteErrorSolved()
            }
        })

    }

    //On pressing of action bar buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Function to sing out of the app
    private fun signOut(){

        //Alert dialog builder for making the alert dialog
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.signoutdialogTitle)
        //set message for alert dialog
        builder.setMessage(R.string.signoutdialogMessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Sign out"){dialogInterface,which->
            Firebase.auth.signOut()
            goToMainActivity() }

        builder.setNegativeButton("Cancel"){dialogInterface,which->
        }

        //Creating the alert dialog from its builder and making it visible
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    //Function to delete the account of the user
    private fun deleteAccount(){

        //Alert dialog builder for making the alert dialog
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.deleteaccountdialogTitle)
        //set message for alert dialog
        val deleteaccountmessage = getString(R.string.deleteaccountdialogMessage_1) + "\n" +
                getString(R.string.deleteaccountdialogMessage_2)
        builder.setMessage(deleteaccountmessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Delete"){dialogInterface,which->
            viewModel.getLocations()
        }

        builder.setNegativeButton("Cancel"){dialogInterface,which->
        }

        //Creating the alert dialog from its builder and making it visible
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    //Function to go to the activity
    private fun goToMainActivity() {
        startActivity(Intent(this,MainActivity::class.java))
        finishAffinity()
    }

    //Companion object with TAG for log messages
    companion object {
        private const val TAG = "SignOutActivity"
    }
}