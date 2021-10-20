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
import com.leafcode.petmint.databinding.FragmentUserNameChangeBinding

class UserNameChange : Fragment() {

    private val activityViewModel : SettingsActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentUserNameChangeBinding = FragmentUserNameChangeBinding.inflate(layoutInflater,container,false)

        activityViewModel.updateActionBarTitle("Change User Name")
        val userNameView = binding.nameEditText
        val progressBar = binding.nameChangeProgressBar
        val submitButton = binding.submitButton
        progressBar.visibility = View.GONE

        userNameView.setText(activityViewModel.userName)
        submitButton.setOnClickListener {

            if (!checkInternetConnectivity()){
                Toast.makeText(this.requireActivity(),"No Internet connection",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var newName = userNameView.text.toString()
            newName = newName.trim()

            if (newName != activityViewModel.userName){
                progressBar.visibility = View.VISIBLE
                activityViewModel.updateUserName(newName)
            } else {
                this.findNavController().popBackStack()
            }

        }

        activityViewModel.nameChange.observe(viewLifecycleOwner,{
            if (it == true){
                activityViewModel.namechangeSuccessful()
                activityViewModel.changeName()
                Toast.makeText(this.requireActivity(),"User Name Changed",Toast.LENGTH_SHORT).show()
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