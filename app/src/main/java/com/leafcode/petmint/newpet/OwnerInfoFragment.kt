package com.leafcode.petmint.newpet

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentOwnerInfoBinding
import com.squareup.picasso.Picasso

//Fragment to select the owner details
class OwnerInfoFragment : Fragment() {

    //View model of activity of this fragment
    val activityViewModel : AddPetViewModel by activityViewModels()

    //Object of shared preference
    private lateinit var sharedPreferences: SharedPreferences

    //Declaring the views
    private lateinit var ownerCity : TextView
    private lateinit var ownerState : TextView
    private lateinit var cityLabel : TextView
    private lateinit var stateLabel : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentOwnerInfoBinding = FragmentOwnerInfoBinding.inflate(inflater,container,false)

        //Updating the title of action bar
        activityViewModel.updateActionBarTitle("Owner Info")

        //Initialising the views
        val userImage = binding.userImage
        val userName = binding.userNameTextView
        val userMobileNumber = binding.ownerMobileTextView
        val ownerAddress = binding.ownerAddressTextView
        ownerCity = binding.cityEditText
        ownerState = binding.stateEditText
        cityLabel = binding.cityLabel
        stateLabel = binding.stateLabel
        val pinCode = binding.pincodeTextView
        val nextButton = binding.nextButton
        val locationButton = binding.findLocationTextView

        //Checking if the photo of the user exists and then putting it in the layout
        if (activityViewModel.photoUrl != null){
        Picasso.get().load(activityViewModel.photoUrl).into(userImage)}

        //Checking if the name and and mobile number of the user exists and then putting it in the layout
        userName.setText(activityViewModel.ownerName)
        val mobileNumber = activityViewModel.mobileNumber
        if (!mobileNumber.equals(null)){
        userMobileNumber.setText(activityViewModel.mobileNumber) }

        //Initialising the shared preferences of the location
        sharedPreferences = this.requireActivity().getSharedPreferences(getString(R.string.location_preferences),Context.MODE_PRIVATE)

        //Calling the function to update location
        updateLocation()

        //Setting the click listener for selection button
        locationButton.setOnClickListener{
            //Putting the reason for which location is being selected
            //Here, 1-> New Pet Location
            sharedPreferences.edit().putInt(getString(R.string.location_type),1).apply()
            this.findNavController().navigate(OwnerInfoFragmentDirections.actionOwnerInfoFragmentToLocationSelectorActivity())
        }

        //Setting the click listener for next Button
        nextButton.setOnClickListener {

            //Checking if the user edited the name already there and then putting it in the viewmodel
            val name = userName.text.toString()
            if (!name.equals(activityViewModel.ownerName)){
                if (name.isNotEmpty()){

                    if (name.length<=20){
                        activityViewModel.ownerName = name
                    } else {
                        Toast.makeText(this.activity,"Only 20 letters are allowed for the Owner name",
                            Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                } else {
                    Toast.makeText(this.activity,"Please enter the name of the owner", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            //Checking if the user edited the mobile number and then adding it to the viewmodel
            val mobile = userMobileNumber.text.toString()
            if (mobile.isNotEmpty()) {
                if (!mobile.equals(activityViewModel.mobileNumber)) {

                    if (mobile.length == 10  && !mobile.contains('*')
                        && !mobile.contains('#') && !mobile.contains(' ') && !mobile.contains('.')
                        && !mobile.contains('+') && !mobile.contains('-') && !mobile.contains('(')
                        && !mobile.contains(')') && !mobile.contains('N') && !mobile.contains('/')
                        && !mobile.contains(',') && !mobile.contains(';')) {
                        activityViewModel.mobileNumber = mobile
                    } else {
                        Toast.makeText(
                            this.activity, "Please enter the correct Mobile Number",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                }
            }else {
                    Toast.makeText(this.activity,"Please enter the mobile number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            //Checking for address and adding it to the viewmodel
            val address = ownerAddress.text.toString()
                if (address.isNotEmpty()){

                    if (address.length<=50){
                        activityViewModel.address = address
                    } else {
                        Toast.makeText(this.activity,"Only 50 letters are allowed for the Address",
                            Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                } else {
                    Toast.makeText(this.activity,"Please enter the Address", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            //Checking if a city is selected and uploading it to the viewmodel
            val city = ownerCity.text.toString()
            if (city.isEmpty()){
                Toast.makeText(this.activity,"Please select the location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Checking if a state is selected and uploading it to the viewmodel
            val state = ownerState.text.toString()
            if (state.isEmpty()){
                Toast.makeText(this.activity,"Please select the location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Checking for the pincode and then adding it to the viewmodel
            val pincode = pinCode.text.toString()
            if (pincode.isNotEmpty()){

                if (pincode.length==6){
                    activityViewModel.pincode = pincode
                } else {
                    Toast.makeText(this.activity,"Please enter the Pin-Code correctly",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            } else {
                Toast.makeText(this.activity,"Please enter the pincode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Atlast navigating to the Image Selection Fragment
            this.findNavController().navigate(OwnerInfoFragmentDirections.actionOwnerInfoFragmentToImageSelectionFragment())

        }


        return binding.root
    }

    //Updating location on onResume
    override fun onResume() {
        super.onResume()
        updateLocation()
    }

    //Function to update the location and its textViews
    private fun updateLocation(){

        //Getting the city and state from the sharedPreferences
        val currentCity = sharedPreferences.getString(getString(R.string.new_pet_city),"")
        val currentState = sharedPreferences.getString(getString(R.string.new_pet_state),"")

        //Checking if the selected is a union territory
        val isUnionTerritory = sharedPreferences.getBoolean(getString(R.string.isNewPetUnionTerritory),false)

        //Adding the city and state to the viewmodel
        if (currentCity != null) {
            activityViewModel.city = currentCity
        }
        if (currentState != null) {
            activityViewModel.state = currentState
        }

        //Changing the city and state labels if the selected is a union territory
        //Then, putting the texts for city and state as the selected city and state
        if (!isUnionTerritory) {
            if (stateLabel.text.equals(getString(R.string.union_territory_string))){
                cityLabel.text = getString(R.string.city_label_string)
                stateLabel.text = getString(R.string.state_label_string)
            }
            ownerCity.text = activityViewModel.city
            ownerState.text = activityViewModel.state
        } else {
            cityLabel.text = getString(R.string.place_label_string)
            stateLabel.text = getString(R.string.union_territory_string)
            ownerCity.text = activityViewModel.city
            ownerState.text = activityViewModel.state
        }
    }
}