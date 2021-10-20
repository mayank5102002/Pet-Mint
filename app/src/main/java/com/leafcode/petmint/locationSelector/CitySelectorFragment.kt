package com.leafcode.petmint.locationSelector

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentCitySelectorBinding

//Fragement to select the city
class CitySelectorFragment : Fragment() {

    //Viewmodel of the activity of this fragment
    val activityViewModel : LocationSelectorViewModel by activityViewModels()

    //Recycler view object
    private lateinit var recyclerView : RecyclerView
    private lateinit var layoutManager : RecyclerView.LayoutManager

    //Shared Preferences object
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentCitySelectorBinding = FragmentCitySelectorBinding.inflate(layoutInflater,container,false)

        //Updating the title of the action bar
        activityViewModel.updateActionBarTitle("Select City")

        //Getting the arguments passed on by its previous fragment
        val arguments = CitySelectorFragmentArgs.fromBundle(requireArguments())

        //Shared Preferences for location to set the city and state selected
        sharedPreferences = this.requireActivity().getSharedPreferences(getString(R.string.location_preferences),
            Context.MODE_PRIVATE)

        //Initialising the recycler view, layout manager and using them
        recyclerView = binding.citySelectorRecyclerView
        layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager

        //Setting the adapter for the recycler view of the city
        //Also, setting up the click listener for the items and passing  it to the adapter
        val adapter = CitySelectorAdapter(arguments.cities,CitySelectorListener {

            activityViewModel.city = it

            //Getting the location type of where it is going to be used
            //0-> search location
            //1->New Pet location
            val locationType = sharedPreferences.getInt(getString(R.string.location_type),0)

            //Getting the state from the viewmodel
            val state = activityViewModel.state

            //Checking if the selected location is Union Territory
            if (state.equals("Andaman & Nicobar Islands") || state.equals("Chandigarh") ||
                state.equals("Daman & Diu")|| state.equals("Dadra & Nagar Haveli")||
                state.equals("Delhi") || state.equals("Lakshadweep")||
                state.equals("Pondicherry") || state.equals("Jammu & Kashmir")){
                //Putting the boolean for union territory to be true
                if (locationType == 0) {
                    sharedPreferences.edit().putBoolean(getString(R.string.isSearchLocationUnionTerritory), true).apply()
                } else if (locationType == 1){
                    sharedPreferences.edit().putBoolean(getString(R.string.isNewPetUnionTerritory), true).apply()
                }
            } else {
                //Putting the boolean for union territory to be false
                if (locationType == 0) {
                    sharedPreferences.edit().putBoolean(getString(R.string.isSearchLocationUnionTerritory), false).apply()
                } else if (locationType == 1){
                    sharedPreferences.edit().putBoolean(getString(R.string.isNewPetUnionTerritory), false).apply()
                }
            }

            //Checking if the location is union terrritory and if it is changing the texts of the layout
                if (locationType == 0) {
                    sharedPreferences.edit().putString(getString(R.string.search_location_city), it)
                        .putString(getString(R.string.search_location_state), state).apply()
                } else if (locationType == 1) {
                    sharedPreferences.edit().putString(getString(R.string.new_pet_city), it)
                        .putString(getString(R.string.new_pet_state), state).apply()
                }

            this.activity?.finish()
        })

        //Setting the above adapter to the recycler view of the layout
        recyclerView.adapter = adapter

        return binding.root
    }

}