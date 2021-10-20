package com.leafcode.petmint.pets

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentPetsBinding
import com.leafcode.petmint.pet.Pet
import com.leafcode.petmint.petDetails.DetailsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PetsFragment : Fragment() {

    //Authorization object for accessing Firebase
    private lateinit var auth : FirebaseAuth

    //Object of shared preference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var recyclerView : RecyclerView
    private lateinit var layoutManager : RecyclerView.LayoutManager
    private lateinit var locationTextView : LinearLayout
    private lateinit var locationString : TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: RelativeLayout
    private lateinit var errorView: RelativeLayout
    private lateinit var searchText : TextView
    private val searchPets = mutableListOf<Pet>()

    private var cancelButtonIsThere = false

    private val viewModel: PetsViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val binding : FragmentPetsBinding = FragmentPetsBinding.inflate(inflater,container,false)

        //Initialising the Viewmodel and ViewModelFactory
        val application = requireNotNull(this.activity).application

        //Initialising the recycler view, layout manager and using them
        recyclerView = binding.petsRecyclerView
        emptyView = binding.emptyListView
        layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
        locationTextView = binding.myLocationTextView
        locationString = binding.location
        progressBar = binding.petsProgressBar
        progressBar.visibility = View.GONE
        searchText = binding.searchText
        val searchButton = binding.searchButton
        errorView = binding.errorView
        errorView.visibility = View.INVISIBLE

        //Initialising the shared preferences of the location
        sharedPreferences = this.requireActivity().getSharedPreferences(getString(R.string.location_preferences),
            Context.MODE_PRIVATE)

        val adapter = PetsAdapter(PetClickListener {
            val intent = Intent(this.requireActivity(),DetailsActivity::class.java)
            intent.putExtra("currentPet",it)
            startActivity(intent)
        })

        recyclerView.adapter = adapter

        //Adding decoration to the recycler view
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )

        //Submitting the list to the adapter
        viewModel.petsList.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
                emptyView.visibility = View.GONE
                errorView.visibility = View.GONE
                progressBar.visibility = View.GONE
            } else {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
        })

        //Authorising the Firebase
        auth = Firebase.auth
        viewModel.authoriseUser()

        //Observing the live data for the authentication of user
        viewModel.loggedIn.observe(viewLifecycleOwner, {
            if (!it){
                goToSignInFragment()
            }
        })

        viewModel.adsReady.observe(viewLifecycleOwner, {
            if (it == true) {
                viewModel.updatePetsList(viewModel.ads)
                viewModel.adsPublished()
            }
        })

        viewModel.fault.observe(viewLifecycleOwner, {
            if (it == true){
                viewModel.faultDealt()
                progressBar.visibility = View.GONE
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.GONE
                errorView.visibility = View.VISIBLE
            }
        })

        //Telling the fragment to implement menu
        setHasOptionsMenu(true)

        //Calling function to update the search location
        updateLocation()

        if (checkInternetConnectivity()) {
            getAds()
        } else {
            progressBar.visibility = View.GONE
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            errorView.visibility = View.VISIBLE
        }

        locationTextView.setOnClickListener {
            //Putting the reason for which location is being selected
            //Here, 1-> New Pet Location
            sharedPreferences.edit().putInt(getString(R.string.location_type),0).apply()
            this.findNavController().navigate(PetsFragmentDirections.actionPetsFragmentToLocationSelectorActivity2())
        }

        searchText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                cancelButtonIsThere = false
                searchButton.setImageDrawable(resources.getDrawable(R.drawable.search_button))

                if (p0.isNullOrBlank()){
                    viewModel.updatePetsList(viewModel.ads)
                }
            }

        })

        searchButton.setOnClickListener{

            searchPets.clear()

            val searchString = searchText.text.toString().lowercase()

            if (!searchString.isBlank()) {

                if (cancelButtonIsThere){

                    searchButton.setImageDrawable(resources.getDrawable(R.drawable.search_button))
                    searchText.setText("")
                    viewModel.updatePetsList(viewModel.ads)

                } else {

                    cancelButtonIsThere = true

                    val petsList = mutableListOf<Pet>()
                    petsList.addAll(viewModel.ads)

                    for (pet in petsList){

                        if (searchString.contains(pet.breed!!.lowercase()) ||
                            searchString.contains(pet.name!!.lowercase()) ||
                            searchString.contains(pet.animalType!!.lowercase()) ||
                            pet.breed.lowercase().contains(searchString) ||
                            pet.name.lowercase().contains(searchString) ||
                            pet.animalType.lowercase().contains(searchString)){

                            progressBar.visibility = View.VISIBLE

                            searchPets.add(pet)

                        }

                        viewModel.updatePetsList(searchPets)
                    }

                    searchButton.setImageDrawable(resources.getDrawable(R.drawable.cancel_button))
                }
            }

        }

        showDialogBox()

        return binding.root
    }

    private fun showDialogBox(){

        val sharedPreferences = this.requireActivity().getSharedPreferences(getString(R.string.starterDialogPreferences)
            ,Context.MODE_PRIVATE)

        val dialog = sharedPreferences.getBoolean(getString(R.string.starterDialogBoxSeen),false)

        if (!dialog) {
            starterDialogBox()
            sharedPreferences.edit().putBoolean(getString(R.string.starterDialogBoxSeen),true).apply()
        }


    }

    private fun getAds(){

        val state = getState()
        val city = getCity()

            if (viewModel.previous_city != city || viewModel.previous_state != state) {
                if (city != null && state != null) {
                    if (city.isNotEmpty() && state.isNotEmpty()) {
                        viewModel.selected_state = state
                        viewModel.selected_city = city
                    } else {
                        viewModel.selected_state = getString(R.string.country_string)
                        viewModel.selected_city = getString(R.string.country_city_string)
                    }
                } else {
                    viewModel.selected_state = getString(R.string.country_string)
                    viewModel.selected_city = getString(R.string.country_city_string)
                }

                viewModel.updatePreviousCity(viewModel.selected_city)
                viewModel.updatePreviousState(viewModel.selected_state)

                progressBar.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                viewModel.getAds()
                searchText.setText("")
            }

    }

    //Authorising the user on starting this fragment
    override fun onResume() {
        super.onResume()
        viewModel.authoriseUser()
        updateLocation()
        if (checkInternetConnectivity()) {
            getAds()
        } else {
            progressBar.visibility = View.GONE
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            errorView.visibility = View.VISIBLE
        }
    }

    private fun starterDialogBox(){

        //Alert dialog builder for making the alert dialog
        val builder = AlertDialog.Builder(this.requireActivity())
        //set title for alert dialog
        builder.setTitle(R.string.betaVersionAlertDialogTitle)
        //set message for alert dialog
        builder.setMessage(R.string.betaVersionAlertDialogContent)

        builder.setPositiveButton("Okay"){dialogInterface,which->
            }

        //Creating the alert dialog from its builder and making it visible
        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()

    }

    //Function to navigate to Sign in fragment
    private fun goToSignInFragment(){
        this.findNavController().navigate(PetsFragmentDirections.actionPetsFragmentToSignInFragment())
        return
    }

    //Function to update the location and its textViews
    private fun updateLocation(){

        //Getting the city and state from the sharedPreferences
        val currentCity = sharedPreferences.getString(getString(R.string.search_location_city),getString(R.string.country_city_string))
        val currentState = sharedPreferences.getString(getString(R.string.search_location_state),getString(R.string.country_string))

        if (currentCity != null && currentState !=null){
            if (currentCity.isNotEmpty() && currentState.isNotEmpty()){
                locationString.text = "$currentCity"
            }else{
                locationString.text = getString(R.string.country_city_string)
            }
        } else{
            locationString.text = getString(R.string.country_city_string)
        }

    }

    private fun getState(): String? {

        //Getting the state from the sharedPreferences

        return sharedPreferences.getString(getString(R.string.search_location_state), getString(R.string.country_string))

    }

    private fun getCity(): String? {

        //Getting the city from the sharedPreferences

        return sharedPreferences.getString(getString(R.string.search_location_city), getString(R.string.country_city_string))

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

    companion object{
        const val TAG = "PetsFragment"
    }

}