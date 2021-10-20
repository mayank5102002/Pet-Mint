package com.leafcode.petmint.settings

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentSettingsBinding
import com.google.android.gms.ads.AdRequest

class SettingsFragment : Fragment() {

    private val activityViewModel : SettingsActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater,container,false)

        activityViewModel.updateActionBarTitle("Settings")

        val userNameChangeButton = binding.changeNameButton
        val emailChangeButton = binding.changeEmailButton
        val passwordChangeButton = binding.changePasswordButton
        val profilePicChangeButton = binding.changeProfilePicButton
        val resetPasswordButton = binding.resetPasswordButton

        val settingsFragmentAd = binding.adView

        val adRequest = AdRequest.Builder().build()
        settingsFragmentAd.loadAd(adRequest)

        userNameChangeButton.setOnClickListener {
            goToChangeNameFragment()
        }

        emailChangeButton.setOnClickListener {
            goToChangeEmailFragment()
        }

        passwordChangeButton.setOnClickListener {
            goToChangePasswordFragment()
        }

        profilePicChangeButton.setOnClickListener {
            goToChangeProfilePicFragment()
        }

        resetPasswordButton.setOnClickListener {

            if (!checkInternetConnectivity()){
                Toast.makeText(this.requireActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Alert dialog builder for making the alert dialog
            val builder = AlertDialog.Builder(this.activity)
            //set title for alert dialog
            builder.setTitle(R.string.signoutdialogTitle)

            //set message for alert dialog
            builder.setMessage(R.string.reset_password_dialog_title)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton(getString(R.string.reset_password_button)){dialogInterface,which->
                activityViewModel.resetPassword() }

            builder.setNegativeButton("Cancel"){dialogInterface,which->
            }

            //Creating the alert dialog from its builder and making it visible
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        activityViewModel.emailEmpty.observe(viewLifecycleOwner,{
            if (it == true){
                Toast.makeText(this.requireActivity(),"Please enter an Email-Id",Toast.LENGTH_SHORT).show()
                activityViewModel.emailEmptyAfter()
            }
        })

        activityViewModel.error.observe(viewLifecycleOwner,{
            if (it == true){
                activityViewModel.errorSolved()
                Toast.makeText(this.requireActivity(),"Some error occured", Toast.LENGTH_SHORT).show()
                this.findNavController().popBackStack()
            }
        })

        activityViewModel.verificationemail.observe(viewLifecycleOwner,{
            if (it == true){
                Toast.makeText(this.requireActivity(),"Password reset email sent", Toast.LENGTH_SHORT).show()
                activityViewModel.verificationEmailSent()
            }
        })

        return binding.root
    }

    private fun goToChangeNameFragment(){
        this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUserNameChange())
    }

    private fun goToChangeEmailFragment(){
        this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToEmailChange())
    }

    private fun goToChangePasswordFragment(){
        this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToPasswordChange())
    }

    private fun goToChangeProfilePicFragment(){
        this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToProfilePicChange())
    }

    private fun checkInternetConnectivity() : Boolean{

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo?.isConnected != null){
            return networkInfo.isConnected
        } else {
            return false
        }

    }


}