package com.leafcode.petmint.pets

import android.app.Application
import android.renderscript.Sampler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.leafcode.petmint.pet.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PetsViewModel(application : Application) : AndroidViewModel(application) {

    //Initiating a job and then finishing it when the activity is shut down
    private var viewModeljob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModeljob.cancel()
    }

    //Firebase authentication object
    private val auth : FirebaseAuth = Firebase.auth

    val userId = auth.uid.toString()
    val db = Firebase.database
    val adsRef = db.getReference("Advertisements")

    private var _loggedIn = MutableLiveData<Boolean>()
    val loggedIn : LiveData<Boolean>
    get() = _loggedIn

    //Function to check if user hasn't already signed in
    fun authoriseUser(){
        _loggedIn.value = auth.currentUser != null
    }

    //Live data for the pet list to show
    private var _petsList = MutableLiveData<MutableList<Pet>?>()
    val petsList : LiveData<MutableList<Pet>?>
    get() = _petsList

    fun updatePetsList(newList : MutableList<Pet>){
        _petsList.value = newList
    }

    private var _adsReady = MutableLiveData<Boolean>()
    val adsReady : LiveData<Boolean>
    get() = _adsReady

    fun adsAreReady(){
        _adsReady.value = true
    }

    fun adsPublished(){
        _adsReady.value = false
    }

    private var _fault = MutableLiveData<Boolean>()
    val fault : LiveData<Boolean>
    get() = _fault

    fun faultReceived(){
        _fault.value = true
    }

    fun faultDealt(){
        _fault.value = false
    }

    var selected_state : String = ""

    var previous_state : String = ""

    fun updatePreviousState(previous : String){
        previous_state = previous
    }

    var selected_city : String = ""

    var previous_city : String = ""

    fun updatePreviousCity(previous : String){
        previous_city = previous
    }

    val ads = mutableListOf<Pet>()

    fun getAds(){

        viewModelScope.launch {
            getAdsList()
        }

    }

    private suspend fun getAdsList(){

        return withContext(Dispatchers.Default){

            if (selected_state.equals("India") && selected_city.equals("All in India")){
                viewModelScope.launch {
                    getAdsCountry()
                }
            } else if (selected_state.isNotBlank() && selected_city.contains("All in")){
                viewModelScope.launch {
                    getAdsState()
                }
            } else if (selected_state.isNotBlank() && selected_city.isNotBlank()){
                viewModelScope.launch {
                    getAdsCity()
                }
            }

        }

    }

    private suspend fun getAdsCountry(){

        return withContext(Dispatchers.IO){

            try {
                ads.clear()

                adsRef.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            if (snapshot.hasChildren()) {
                                for (stateData in snapshot.children) {
                                    if (stateData.hasChildren()){
                                        for (cityData in stateData.children){
                                            if (cityData.hasChildren()){
                                                for (user in cityData.children){
                                                    if (user.hasChildren()){
                                                        for (ad in user.children){
                                                            val pet = ad.getValue(Pet::class.java)
                                                            ads.add(pet as Pet)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                ads.shuffle()
                            }
                        }
                        adsAreReady()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        faultReceived()
                        Log.e(PetsFragment.TAG,"Error in getting ads : $error")
                    }

                })

            } catch (e : Exception){
                Log.e(PetsFragment.TAG,"Error : $e")
                return@withContext
            } finally {
                adsReady
            }

        }

    }

    private suspend fun getAdsState(){

        return withContext(Dispatchers.IO){

            try {
                ads.clear()

                adsRef.child(selected_state).addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            if (snapshot.hasChildren()){
                                for(cityData in snapshot.children){
                                    if (cityData.hasChildren()){
                                        for (user in cityData.children){
                                            if (user.hasChildren()){
                                                for (ad in user.children){
                                                    val pet = ad.getValue(Pet::class.java)
                                                    ads.add(pet as Pet)
                                                }
                                            }
                                        }
                                    }
                                }
                                ads.shuffle()
                            }
                        }
                        adsAreReady()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        faultReceived()
                        Log.e(PetsFragment.TAG,"Error in getting ads : $error")
                    }

                })

            } catch (e : Exception){
                Log.e(PetsFragment.TAG,"Error : $e")
                return@withContext
            }

        }

    }

    private suspend fun getAdsCity(){

        return withContext(Dispatchers.IO){

            try {
                ads.clear()

                adsRef.child(selected_state).child(selected_city).addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            if (snapshot.hasChildren()){
                                for (user in snapshot.children){
                                    if (user.hasChildren()){
                                        for (ad in user.children){
                                            val pet = ad.getValue(Pet::class.java)
                                            ads.add(pet as Pet)
                                        }
                                    }
                                }
                                ads.shuffle()
                            }
                        }
                        adsAreReady()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        faultReceived()
                        Log.e(PetsFragment.TAG,"Error in getting ads : $error")
                    }

                })

            } catch (e : Exception){
                Log.e(PetsFragment.TAG,"Error : $e")
                return@withContext
            }

        }

    }
}