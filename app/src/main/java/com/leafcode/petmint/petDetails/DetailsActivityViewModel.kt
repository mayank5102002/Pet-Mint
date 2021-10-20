package com.leafcode.petmint.petDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafcode.petmint.pet.Pet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsActivityViewModel : ViewModel() {

    //Creating a job and finishing it when activity is finished
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Making title of the action bar to be live data to change it from within fragments
    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val auth = Firebase.auth
    val db = Firebase.database
    private val currentUserId = auth.currentUser?.uid.toString()

    fun updateActionBarTitle(title: String) = _title.postValue(title)

    private val _currentPet = MutableLiveData<Pet>()
    val currentPet : LiveData<Pet>
    get() = _currentPet

    fun setCurrentPet(current : Pet){
        _currentPet.value = current
    }

    fun checkForOwner() : Boolean{
        return currentUserId == currentPet.value?.userID
    }

    private val _error = MutableLiveData<Boolean>()
    val error : LiveData<Boolean>
        get() = _error

    fun errorFound(){
        _error.value = true
    }

    fun errorSolved(){
        _error.value = false
    }

    private val _adDeletion = MutableLiveData<Boolean>()
    val adDeletion : LiveData<Boolean>
        get() = _adDeletion

    fun adDeleted(){
        _adDeletion.value = true
    }

    fun adDoneDeleted(){
        _adDeletion.value = false
    }

    fun deleteAd(){

        viewModelScope.launch {
            deleteAdData()
        }

    }

    private suspend fun deleteAdData(){

        return withContext(Dispatchers.IO){

            val adsRef = db.getReference("Advertisements")
            val usersRef = db.getReference("Users")
            val current = currentPet.value
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            adsRef.child(current?.state!!).child(current.city!!).child(current.userID!!)
                .child(current.adID!!).removeValue().addOnFailureListener{
                    errorFound()
                    Log.e("delete Ad",it.toString())
                }.addOnSuccessListener {

                    usersRef.child(current.userID).child("AdReferences")
                        .child(current.adID).removeValue().addOnFailureListener {
                            errorFound()
                            Log.e("delete Ad",it.toString())
                        }.addOnSuccessListener {

                            val fileRef = storageRef.child(Firebase.auth.currentUser?.uid!!).child(current.adID)

                            fileRef.delete()
                            adDeleted()

                            /*fileRef.delete().addOnSuccessListener {
                                    adDeleted()
                                }.addOnFailureListener {
                                    errorFound()
                                    Log.e("delete Ad",it.toString())
                                }*/

                        }

                }

        }

    }

}