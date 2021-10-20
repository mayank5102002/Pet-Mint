package com.leafcode.petmint.settings

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
import com.leafcode.petmint.databinding.FragmentEmailChangeBinding

class EmailChange : Fragment() {

    private val activityViewModel : SettingsActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentEmailChangeBinding = FragmentEmailChangeBinding.inflate(layoutInflater,container,false)

        activityViewModel.updateActionBarTitle("Change Email")

        val emailTextView = binding.emailEditText
        val submitButton = binding.submitButton
        val progressBar = binding.emailChangeProgressBar
        progressBar.visibility = View.GONE

        emailTextView.setText(activityViewModel.email)

        submitButton.setOnClickListener {

            if (!checkInternetConnectivity()){
                Toast.makeText(this.requireActivity(),"No Internet connection",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newEmail = emailTextView.text.toString()
            newEmail.trim()

            if (newEmail != activityViewModel.email){
                activityViewModel.updateEmailAddress(newEmail)
                progressBar.visibility = View.VISIBLE
            } else {
                this.findNavController().popBackStack()
            }

        }

        activityViewModel.emailChange.observe(viewLifecycleOwner,{
            if (it == true){
                activityViewModel.emailchangeSuccessful()
                activityViewModel.changeEmail()
                Toast.makeText(this.requireActivity(),"Email Changed", Toast.LENGTH_SHORT).show()
                this.findNavController().popBackStack()
            }
        })

        activityViewModel.error.observe(viewLifecycleOwner,{
            if (it == true){
                activityViewModel.errorSolved()
                Toast.makeText(this.requireActivity(),"Some error occured", Toast.LENGTH_SHORT).show()
                this.findNavController().popBackStack()
            }
        })

        return binding.root
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