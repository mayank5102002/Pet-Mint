package com.leafcode.petmint.myads

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafcode.petmint.pet.AdvertisementLocation
import com.leafcode.petmint.pet.AdvertisementLocationReceive
import com.leafcode.petmint.pet.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAdsViewModel : ViewModel() {

    //Initiating a job and then finishing it when the activity is shut down
    private var viewModeljob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModeljob.cancel()
    }

    //Firebase authentication object
    private val auth : FirebaseAuth = Firebase.auth

    val userID = auth.uid.toString()

    //Live data for the my ads list to show
    private var _myAdsList = MutableLiveData<MutableList<Pet>?>()
    val myAdsList : LiveData<MutableList<Pet>?>
        get() = _myAdsList

    fun updateMyAdsList(newList : MutableList<Pet>){
        _myAdsList.value = newList
    }

    private var _setEmptyView = MutableLiveData<Boolean>()
    val setEmptyView : LiveData<Boolean>
    get() = _setEmptyView

    fun setViewAsEmpty(){
        _setEmptyView.value = true
    }

    fun ViewSetEmpty(){
        _setEmptyView.value = false
    }

    private var _locationReady = MutableLiveData<Boolean>()
    val locationReady : LiveData<Boolean>
        get() = _locationReady

    fun locationIsReady(){
        _locationReady.value = true
    }

    fun locationExecuted(){
        _locationReady.value = false
    }

    val locations = mutableListOf<AdvertisementLocation>()

    var previousLocations = mutableListOf<AdvertisementLocation>()

    fun setPrevious(newList : MutableList<AdvertisementLocation>){
        previousLocations.clear()
        previousLocations.addAll(newList)
    }

    private var _adsReady = MutableLiveData<Boolean>()
    val adsReady : LiveData<Boolean>
        get() = _adsReady

    fun adsAreReady(){
        _adsReady.value = true
    }

    fun adsExecuted(){
        _adsReady.value = false
    }

    val ads = mutableListOf<Pet>()
        get() = field

    private var _isFaulty = MutableLiveData<Boolean>()
    val isFaulty : LiveData<Boolean>
    get() = _isFaulty

    fun faultReceived(){
        _isFaulty.value = true
    }

    fun faultToastGiven(){
        _isFaulty.value = false
    }

    fun getLocations(){

        viewModelScope.launch {
            getLocationList()
        }

    }

    fun getAds(){

        viewModelScope.launch {
            getAdsList()
        }

    }

    private suspend fun getLocationList(){

        return withContext(Dispatchers.IO){

            val userId = Firebase.auth.uid.toString()
            val db = Firebase.database
            val userRef = db.getReference("Users").child(userId)
                .child("AdReferences")

            try {
                userRef.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.hasChildren()) {
                                locations.clear()
                                for (data in snapshot.children) {
                                    val reference = data.getValue(AdvertisementLocation::class.java)
                                    locations.add(reference!!)
                                }
                                locationIsReady()
                            } else {
                                setViewAsEmpty()
                            }
                        } else {
                            setViewAsEmpty()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(MyAds.TAG, "Location load cancelled with error $error")
                        faultReceived()
                    }

                })

//                get().addOnSuccessListener {
//                    if (it.exists()) {
//                        if (it.hasChildren()) {
//                                locations.clear()
//                                for (data in it.children) {
//                                    val reference = data.getValue(AdvertisementLocation::class.java)
//                                    locations.add(reference!!)
//                                }
//                                locationIsReady()
//                        } else {
//                            setViewAsEmpty()
//                        }
//                    } else {
//                        setViewAsEmpty()
//                    }
//                }.addOnFailureListener {
//                    Log.e(MyAds.TAG, "Location load cancelled with error $it")
//                    faultReceived()
//                }
            } catch (e : Exception){
                faultReceived()
                Log.e(MyAds.TAG, "Location load error $e")
            }

        }
    }

    private suspend fun getAdsList(){

        return withContext(Dispatchers.Default){

            val userId = Firebase.auth.uid.toString()
            val db = Firebase.database
            val adsRef = db.getReference("Advertisements")
            val Addedlocations = mutableListOf<AdvertisementLocationReceive>()
            Addedlocations.clear()
            ads.clear()

            try {
                for (current in locations) {
                    if (!current.state.isBlank() && !current.city.isBlank()) {
                        val recievedLocation =
                            AdvertisementLocationReceive(current.state, current.city)
                        if (!Addedlocations.contains(recievedLocation)) {
                            Addedlocations.add(recievedLocation)
                            viewModelScope.launch {
                                getCurrentAds(adsRef, current, userId)
                            }
                        }
                    }
                }
            } catch (e : Exception){
                Log.e(MyAds.TAG, "Ad load error $e")
                return@withContext
            }

        }

    }

    private suspend fun getCurrentAds(
        adsRef: DatabaseReference,
        current: AdvertisementLocation,
        userId: String ) {

        return withContext(Dispatchers.IO) {

            try {
                adsRef.child(current.state).child(current.city).child(userId).addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (data in snapshot.children) {
                                val pet = data.getValue(Pet::class.java)
                                ads.add(pet as Pet)
                            }
                            adsAreReady()
                        } else {
                            setViewAsEmpty()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i(MyAds.TAG, "Pet load cancelled with error $error")
                        faultReceived()
                    }

                })

//                get()
//                    .addOnSuccessListener {
//                        if (it.exists()) {
//                            for (data in it.children) {
//                                val pet = data.getValue(Pet::class.java)
//                                ads.add(pet as Pet)
//                            }
//                            adsAreReady()
//                        } else {
//                            setViewAsEmpty()
//                        }
//                    }.addOnFailureListener {
//                        Log.i(MyAds.TAG, "Pet load cancelled with error $it")
//                        faultReceived()
//                    }
            } catch (e : Exception){
                faultReceived()
                Log.e(MyAds.TAG, "AdsList load error $e")
            }
        }

    }

}