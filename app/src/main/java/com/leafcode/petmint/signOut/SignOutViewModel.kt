package com.leafcode.petmint.signOut

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafcode.petmint.myads.MyAds
import com.leafcode.petmint.pet.AdvertisementLocation
import com.leafcode.petmint.pet.AdvertisementLocationReceive
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignOutViewModel : ViewModel() {

    //Creating a job and finishing it when activity is finished
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val userId = Firebase.auth.uid.toString()
    val db = Firebase.database
    val userRef = db.getReference("Users").child(userId)
        .child("AdReferences")
    val adsRef = db.getReference("Advertisements")

    val locations = mutableListOf<AdvertisementLocation>()

    private val _error = MutableLiveData<Boolean>()
    val error : LiveData<Boolean>
    get() = _error

    private fun errorOccured(){
        _error.value = true
    }

    fun errorResolved(){
        _error.value = false
    }

    private val _adlocations = MutableLiveData<Boolean>()
    val adlocations : LiveData<Boolean>
        get() = _adlocations

    fun adlocationsReady(){
        _adlocations.value = true
    }

    fun adlocationsExecuted(){
        _adlocations.value = false
    }

    private val _user = MutableLiveData<Boolean>()
    val user : LiveData<Boolean>
        get() = _user

    fun userDeleted(){
        _user.value = true
    }

    fun userDeleteSuccessfull(){
        _user.value = false
    }

    private val _userDataDelete = MutableLiveData<Boolean>()
    val userDataDelete : LiveData<Boolean>
        get() = _userDataDelete

    fun deleteUser(){
        _userDataDelete.value = true
    }

    fun userDataDeleted(){
        _userDataDelete.value = false
    }

    private val _userDeleteError = MutableLiveData<Boolean>()
    val userDeleteError : LiveData<Boolean>
        get() = _userDeleteError

    fun userDeleteErrorOccured(){
        _userDeleteError.value = true
    }

    fun userDeleteErrorSolved(){
        _userDeleteError.value = false
    }

    fun getLocations(){

        viewModelScope.launch {
            getLocationList()
        }

    }

    fun deleteUserData(){

        viewModelScope.launch {
            deleteUserIdAndAds()
        }

    }

    fun deleteUserFinal(){

        val user = Firebase.auth.currentUser!!
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userDeleted()
                } else {
                    userDeleteErrorOccured()
                }
            }

    }

    private suspend fun getLocationList(){

        return withContext(Dispatchers.IO){

            try {
                userRef.get().addOnSuccessListener {
                    if (it.exists()) {
                        if (it.hasChildren()) {
                            locations.clear()
                            for (data in it.children) {
                                val reference = data.getValue(AdvertisementLocation::class.java)
                                locations.add(reference!!)
                            }
                            adlocationsReady()
                        } else {
                            deleteUser()
                        }
                    } else {
                        deleteUser()
                        Log.e("data", userDataDelete.value.toString())
                    }
                }.addOnFailureListener {
                    errorOccured()
                    Log.e(MyAds.TAG, "Location load cancelled with error $it")
                }
            } catch (e : Exception){
                errorOccured()
                Log.e("Getting Ad References", "Location load error $e")
            }

        }
    }

    private suspend fun deleteUserIdAndAds(){

        return  withContext(Dispatchers.IO){

            val storageRef = FirebaseStorage.getInstance().reference

            val Addedlocations = mutableListOf<AdvertisementLocationReceive>()
            Addedlocations.clear()

            try {
                for (current in locations) {
                    if (!current.state.isBlank() && !current.city.isBlank()) {
                        val recievedLocation = AdvertisementLocationReceive(current.state, current.city)
                        if (!Addedlocations.contains(recievedLocation)) {
                            Addedlocations.add(recievedLocation)
                            adsRef.child(current.state).child(current.city).child(userId)
                                .removeValue().addOnFailureListener {
                                    errorOccured()
                                }
                        }
                    }
                }


                db.getReference("Users").child(userId)
                    .removeValue().addOnFailureListener{
                        errorOccured()
                    }.addOnSuccessListener {
                        deleteUser()
                    }

            } catch (e : Exception){
                errorOccured()
                Log.e("Deleting Ads", "Ads delete error $e")
                return@withContext
            }

        }

    }

}