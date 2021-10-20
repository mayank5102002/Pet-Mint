package com.leafcode.petmint.newpet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentNewPetDetailsBinding

//Fragment to select the details of the pet
class NewPetDetailsFragment : Fragment() {

    //Viewmodel of the activity of this fragment
    val activityViewModel : AddPetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentNewPetDetailsBinding = FragmentNewPetDetailsBinding.inflate(inflater,container,false)

        //Updating the title of the action bar
        activityViewModel.updateActionBarTitle("Create new pet")

        //Spinner Initialisation
        val spinner = binding.animalTypeSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        this.activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.animal_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }

        //Initialising the texts for different fields
        val petNameTextView = binding.petNameEditText
        val genderMaleRadioButton = binding.genderMaleRadioButton
        val genderFemaleRadioButton = binding.genderFemaleRadioButton
        val breedTextView = binding.breedTextView
        val descriptionTextView = binding.desciptionTextView
        val nextButton = binding.nextButton

        //Setting the click listener for the next button
        nextButton.setOnClickListener {

            //Checking if the name is not empty and then addding it to the viewmodel
            if (petNameTextView.text.toString().isNotEmpty()){
                if (petNameTextView.text.toString().length<=20){
                 activityViewModel.nameOfPet = petNameTextView.text.toString()
                } else {
                    Toast.makeText(this.activity,"Only 20 letters are allowed for the pet name",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            } else {
                Toast.makeText(this.activity,"Please enter the name of the pet",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Checking if a gender is selected and then adding it to the viewmodel
            //Here, 0-> Male
            //1-> Female
            if (genderMaleRadioButton.isChecked){
                activityViewModel.gender = 0
            } else if (genderFemaleRadioButton.isChecked){
                activityViewModel.gender = 1
            } else {
                Toast.makeText(this.activity,"Please select a Gender for the pet",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Checking if user has selected any animal type and adding it to the viewmodel
            //And if not then selecting Dog by default
            activityViewModel.animalType = when(spinner.selectedItemPosition){
                1-> "Cat"
                2-> "Bird"
                3-> "Others"
                else-> "Dog"
            }

            //Checking if the yser has entered the breed and then adding it to the viewmodel
            if (breedTextView.text.toString().isNotEmpty()){

                if (breedTextView.text.toString().length<=40){
                    activityViewModel.breed = breedTextView.text.toString()
                } else {
                    Toast.makeText(this.activity,"Only 40 letters are allowed for the breed",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            } else {
                activityViewModel.breed = "Unknown"
            }

            //Checking if the user has entered the description and adding it to the viewmodel
            if (descriptionTextView.text.toString().isNotEmpty()){

                if (descriptionTextView.text.toString().length<=500 && descriptionTextView.text.toString().length>=20){
                    activityViewModel.description = descriptionTextView.text.toString()
                } else {
                    Toast.makeText(this.activity,"Please adjust the length of the description between 20 and 100 words.",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            } else {
                Toast.makeText(this.activity,"Please enter the description of the pet",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            this.findNavController().navigate(NewPetDetailsFragmentDirections.actionNewPetDetailsFragmentToOwnerInfoFragment())
        }

        return binding.root
    }

}