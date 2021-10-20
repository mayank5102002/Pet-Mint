package com.leafcode.petmint.petDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentDetailsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.lang.Exception

class DetailsFragment : Fragment() {

    private val activityViewModel : DetailsActivityViewModel by activityViewModels()

    private val CALL_PERSMISSION_CODE = 502

    private lateinit var petNameTextView : TextView
    private lateinit var ownerNameTextView : TextView
    private lateinit var userNameTextView : TextView
    private lateinit var postingDateTextView : TextView
    private lateinit var genderTextView : TextView
    private lateinit var breedTextView : TextView
    private lateinit var animalTypeTextView : TextView
    private lateinit var descriptionTextView : TextView
    private lateinit var addressTextView : TextView
    private lateinit var adIdTextView : TextView
    private lateinit var callButton : Button
    private lateinit var parentScrollView : ScrollView
    private lateinit var progressBar : ProgressBar

    private lateinit var detailsFragmentAd : AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentDetailsBinding = FragmentDetailsBinding.inflate(layoutInflater,container,false)

        activityViewModel.updateActionBarTitle("Pet Details")

        val imageSlider = binding.imageSliderPager
        val imageUriList = mutableListOf<String>()
        petNameTextView = binding.petNameTextView
        ownerNameTextView = binding.ownerNameTextView
        userNameTextView = binding.userNameTextView
        postingDateTextView = binding.postingDateTextView
        genderTextView = binding.genderTextView
        breedTextView = binding.breedTextView
        animalTypeTextView = binding.animalTypeTextView
        descriptionTextView = binding.descriptionTextView
        addressTextView = binding.petAddressTextView
        adIdTextView = binding.adIdTextView
        callButton = binding.callButton
        parentScrollView = binding.scrollViewDetails
        progressBar = binding.detailsProgressBar
        progressBar.visibility = View.GONE

        detailsFragmentAd = binding.adView

        if (!activityViewModel.currentPet.value?.image1.isNullOrBlank()){
            imageUriList.add(activityViewModel.currentPet.value?.image1!!)
        }
        if (!activityViewModel.currentPet.value?.image2.isNullOrBlank()){
            imageUriList.add(activityViewModel.currentPet.value?.image2!!)
        }
        if (!activityViewModel.currentPet.value?.image3.isNullOrBlank()){
            imageUriList.add(activityViewModel.currentPet.value?.image3!!)
        }

        imageSlider.adapter = ImageSliderAdapter(imageUriList)

        if (activityViewModel.checkForOwner()){
            setHasOptionsMenu(true)
            callButton.visibility = View.GONE
        }

        updateTextViews()

        callButton.setOnClickListener {

                try {
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data =
                        Uri.parse("tel:" + "${activityViewModel.currentPet.value?.mobileNumber}")
                    startActivity(callIntent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this.requireActivity(),
                        "Some error occured while making the call",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("calling", e.toString())
                }

        }

        activityViewModel.error.observe(viewLifecycleOwner,{
            if (it == true){
                Toast.makeText(this.requireActivity(),"Some error in deleting Ad occured",Toast.LENGTH_SHORT).show()
                this.activity?.finish()
                activityViewModel.errorSolved()
                progressBar.visibility = View.GONE
            }
        })

        activityViewModel.adDeletion.observe(viewLifecycleOwner,{
            if (it == true){
                Toast.makeText(this.requireActivity(),"Ad deleted successfully, please refresh",Toast.LENGTH_SHORT).show()
                this.activity?.finish()
                activityViewModel.adDoneDeleted()
                progressBar.visibility = View.GONE
            }
        })

        return binding.root
    }

    fun updateTextViews(){

        val currentPet = activityViewModel.currentPet.value

        if (currentPet == null){
            return
        }

        if (!currentPet.name.isNullOrBlank()){
            petNameTextView.text = currentPet.name
        }

        if (!currentPet.userName.isNullOrBlank()){
            userNameTextView.text = currentPet.userName
        }

        if (!currentPet.ownerName.isNullOrBlank()){
            ownerNameTextView.text = currentPet.ownerName
        }

        if (!currentPet.postingDate.isNullOrBlank()){
            postingDateTextView.text = currentPet.postingDate.toString()
        }

        when(currentPet.gender){
            0->genderTextView.text = "Male"
            else->genderTextView.text = "Female"
        }

        if (!currentPet.breed.isNullOrBlank()){
            breedTextView.text = currentPet.breed
        }

        if (!currentPet.animalType.isNullOrBlank()){
            animalTypeTextView.text = currentPet.animalType
        }

        if (!currentPet.description.isNullOrBlank()){
            descriptionTextView.text = currentPet.description
        }

        if (!currentPet.address.isNullOrBlank() && !currentPet.city.isNullOrBlank() && !currentPet.city.isNullOrBlank()){
            val address = "${currentPet.address}, ${currentPet.city}, ${currentPet.state}"
            addressTextView.text = address
        }

        if (!currentPet.adID.isNullOrBlank()){
            adIdTextView.text = currentPet.adID
        }

        val adRequest = AdRequest.Builder().build()
        detailsFragmentAd.loadAd(adRequest)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu)
    }

    //On pressing of action bar buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.details_menu_delete_button->{
                deleteAdAndImages()
                Log.e("delete button","delete button clicked")
                return true
            }
            else->super.onOptionsItemSelected(item)
        }
    }

    fun deleteAdAndImages(){
        activityViewModel.deleteAd()
        progressBar.visibility = View.VISIBLE
    }

}