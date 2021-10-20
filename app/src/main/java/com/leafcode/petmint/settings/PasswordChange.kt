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
import com.leafcode.petmint.databinding.FragmentPasswordChangeBinding

class PasswordChange : Fragment() {

    private val activityViewModel : SettingsActivityViewModel by activityViewModels()

  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentPasswordChangeBinding = FragmentPasswordChangeBinding.inflate(layoutInflater,container,false)

      activityViewModel.updateActionBarTitle("Change Password")

      val passwordTextView = binding.passwordEditText
      val rePasswordTextView = binding.reEnterPasswordEditText
      val submitButton = binding.submitButton
      val progressBar = binding.passwordChangeProgressBar
      progressBar.visibility = View.GONE


      submitButton.setOnClickListener {

          if (!checkInternetConnectivity()){
              Toast.makeText(this.requireActivity(),"No Internet connection",Toast.LENGTH_SHORT).show()
              return@setOnClickListener
          }

          val newPassword = passwordTextView.text.toString()
          val rePassword = rePasswordTextView.text.toString()

          if (newPassword.equals(rePassword)){
              activityViewModel.updateNewPassword(rePassword)
              progressBar.visibility = View.VISIBLE
          } else {
              Toast.makeText(this.requireActivity(),"Passwords don't match",Toast.LENGTH_SHORT).show()
          }

      }

      activityViewModel.passwordChange.observe(viewLifecycleOwner,{
          if (it == true){
              activityViewModel.passwordchangeSuccessful()
              Toast.makeText(this.requireActivity(),"Password successfully Changed", Toast.LENGTH_SHORT).show()
              this.findNavController().popBackStack()
          }
      })

      activityViewModel.passworderror.observe(viewLifecycleOwner,{
          if (it == true){
              activityViewModel.passworderrorSolved()
              Toast.makeText(this.requireActivity(),"Please sign-in again and then try changing password", Toast.LENGTH_SHORT).show()
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