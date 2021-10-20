package com.leafcode.petmint.myAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.databinding.MyAccountFragmentBinding
import com.squareup.picasso.Picasso

class MyAccount : Fragment() {

    //Initialising different objects
    private val viewModel: MyAccountViewModel by viewModels()
    lateinit var userNameTextView : TextView
    lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : MyAccountFragmentBinding = MyAccountFragmentBinding.inflate(inflater,container,false)

        //Initialising the views using binding
        val settingsButton = binding.settingsLayoutButton
        val helpButton = binding.helpLayoutButton
        val signOutButton = binding.signoutLayoutButton
        userNameTextView = binding.userNameTextView
        imageView = binding.imageView

        updateAccount()

        //Click-Listeners
        settingsButton.setOnClickListener {
            goToSettingsFragment()
        }

        helpButton.setOnClickListener {
            goToHelpandSupportFragment()
        }

        signOutButton.setOnClickListener {
            goToSignOutFragment()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.authenticateUser()
        updateAccount()
    }

    //Function to go to sign out fragment
    private fun goToSignOutFragment() {
        this.findNavController().navigate(MyAccountDirections.actionMyAccountToSignOutActivity())
    }

    //Function to go to Help and support frgament
    private fun goToHelpandSupportFragment() {
        this.findNavController().navigate(MyAccountDirections.actionMyAccountToHelpandSupportActivity())
    }

    //Function to go to Settings frgament
    private fun goToSettingsFragment() {
        this.findNavController().navigate(MyAccountDirections.actionMyAccountToSettingsActivity())
    }


    //Function to update the account with latest details
    private fun updateAccount(){
        //Setting the name of the user
        userNameTextView.setText(viewModel.getUserName())

        //Setting the profile picture of the user
        if (viewModel.photoUrl!=null){
            Picasso.get().load(viewModel.photoUrl).into(imageView)
        }
    }
}