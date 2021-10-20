package com.leafcode.petmint.newpet

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafcode.petmint.pet.AdvertisementLocation
import com.leafcode.petmint.pet.Pet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

//View Model for the Add pet activity
class AddPetViewModel : ViewModel() {

    //Creating a job and finishing it when activity is finished
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Title for the action bar
    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    fun updateActionBarTitle(title: String) = _title.postValue(title)

    //Initialising the objects for the new pet to be created
    private var _adNumber = MutableLiveData<Int>()
    val adNumber : LiveData<Int>
    get() = _adNumber

    fun setAdNumber(i: Int){
        _adNumber.value = i
    }

    var UserId : String = Firebase.auth.uid.toString()
        get() = field

    val photoUrl = Firebase.auth.currentUser?.photoUrl
        get() = field

    var adId : String = "$UserId-${adNumber.value}"
        get() = field

    fun setAdID(){
        adId = "$UserId-${adNumber.value}"
    }

    var nameOfPet : String = "Charlie"
        set(value) {
            field = value
        }

    var gender : Int = 0
        set(value) {
            field = value
        }

    var animalType : String = "Dog"
        set(value) {
            field = value
        }

    var breed : String = "Pitbull"
        set(value) {
            field = value
        }

    var description : String = "A dog"
        set(value) {
            field = value
        }

    var mobileNumber : String = Firebase.auth.currentUser?.phoneNumber.toString()
        get() = field
        set(value) {
            field = value
        }

    var ownerName : String = Firebase.auth.currentUser?.displayName.toString()
        get() = field
        set(value) {
            field = value
        }

    val userName : String = Firebase.auth.currentUser?.displayName.toString()

    var image1 : String? = ""
        set(value) {
            field = value
        }
    var image2 : String? = ""
        set(value) {
            field = value
        }
    var image3 : String? = ""
        set(value) {
            field = value
        }

    val currentTime = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat")
    var postingDate : String = SimpleDateFormat("d MMM,yyyy").format(currentTime)

    var city : String = ""
        set(value) {
            field = value
        }

    var state : String = ""
        set(value) {
            field = value
        }

    var country : String = "India"

    var address : String = ""
        set(value) {
            field = value
        }

    var pincode : String = ""
        set(value) {
            field = value
        }

    //Function to create Pet Object to submit
    fun getNewPet() : Pet {
        val newPet = Pet(
            _adNumber.value!!,
            UserId,
            adId,
            nameOfPet,
            gender,
            animalType,
            breed,
            description,
            mobileNumber,
            ownerName,
            userName,
            image1,
            image2,
            image3,
            postingDate,
            city,
            state,
            country,
            address,
            pincode
        )
        return newPet
    }

    //Function to make the ad reference object to submit
    fun getAdvertisementLocation() : AdvertisementLocation{
        val location = AdvertisementLocation(
            _adNumber.value!!,
            UserId,
            adId,
            state,
            city
        )
        return location
    }

    //Live Data to confirm how many images have been uploaded successfully
    private val _imagesUploadConfirm = MutableLiveData<Int>()
    val imagesUploadConfirm: LiveData<Int>
        get() = _imagesUploadConfirm

    fun initialiseImageUploadConfirm(){
        _imagesUploadConfirm.value = 0
    }

    fun updateImageUploadConfirm(){
        _imagesUploadConfirm.value = _imagesUploadConfirm.value?.plus(1)
    }

    //Function to confirm to upload ad
    private val _uploadAd = MutableLiveData<Boolean>()
    val uploadAd : LiveData<Boolean>
        get() = _uploadAd

    fun readyUploadAd(){
        _uploadAd.value = true
    }

    fun adUpload(){
        _uploadAd.value = false
    }

    //Function to confirm the ad has been uploaded
    private val _aduploaded = MutableLiveData<Boolean>()
    val adUploaded : LiveData<Boolean>
    get() = _aduploaded

    fun adUploadSuccessful(){
        _aduploaded.value = true
    }

    private val _fault = MutableLiveData<Boolean>()
    val fault: LiveData<Boolean>
    get() = _fault

    private fun faultReceived(){
        _fault.value = true
    }

    private fun faultDealt(){
        _fault.value = false
    }

    fun uploadImages(i : Int,p:String){

        viewModelScope.launch {
            putImageInStorage(i,p)
        }

    }

    fun uploadAd(){

        viewModelScope.launch {
            uploadAdAndLocation()
        }

    }

    //Function to upload the image
    private suspend fun putImageInStorage(i : Int,p:String){

        return withContext(Dispatchers.IO) {

            val storageRef = FirebaseStorage.getInstance().reference

            //Returning if the string is empty
            if (p.isEmpty()) {
                return@withContext
            }

            //Meta data for the image
            val metadata = storageMetadata {
                contentType = "image/jpg"
            }

            //Reference variable to upload the image
            val usersRef = storageRef.child(Firebase.auth.currentUser?.uid!!).child(adId)
                .child("Image:${i + 1}")

            //Uploading the image in its desired location
            usersRef.putFile(Uri.parse(p), metadata).addOnSuccessListener { taskSnapShot ->
                taskSnapShot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        //Getting the download url for the image uploaded and adding it to the viewmodel
                        when (i) {
                            0 -> {
                                image1 = uri.toString()
                                updateImageUploadConfirm()
                            }
                            1 -> {
                                image2 = uri.toString()
                                updateImageUploadConfirm()
                            }
                            else -> {
                                image3 = uri.toString()
                                updateImageUploadConfirm()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            "ImageSelectionFragment",
                            "Image upload task was unsuccessful.", e
                        )
                        faultReceived()
                    }

            }
        }
    }

    //Function to upload the ad
    private suspend fun uploadAdAndLocation(){

        return withContext(Dispatchers.IO) {

            val db = Firebase.database

            //Getting the ad and ad reference object from the viewmodel
            val newPetAdvertisement = getNewPet()
            val newPetAdvertisementLocation = getAdvertisementLocation()

            //Setting up the references for the database
            val adsRef = db.getReference("Advertisements")
            val usersRef = db.getReference("Users")

            //Submitting the ad reference for the ad in the desired location
            //Adding on success listener
            //Also adding on failure listener and returning if it is unsuccessful
            usersRef.child(Firebase.auth.uid.toString()).child("AdReferences").child(adId)
                .setValue(newPetAdvertisementLocation)
                .addOnFailureListener {
                    faultReceived()
                    Log.e("location error", "error in location upload")
                    return@addOnFailureListener
                }.addOnSuccessListener {
                    //Uploading the ad on successful submission of the ad reference
                    adsRef.child(state).child(city).child(Firebase.auth.uid.toString())
                        .child(adId).setValue(newPetAdvertisement).addOnFailureListener {
                            faultReceived()
                            Log.e("ad error", "error in ad upload")
                            return@addOnFailureListener
                        }.addOnSuccessListener {
                            //Setting the viewmodel to be aware of ad submission
                            usersRef.child(Firebase.auth.uid.toString()).child("adNumber")
                                .setValue(newPetAdvertisement.adNumber)
                                .addOnSuccessListener {
                                    adUploadSuccessful()
                                    faultReceived()
                                }.addOnFailureListener {
                                    faultReceived()
                                    Log.e("adNumber error", "error in adNumber upload")
                                    return@addOnFailureListener
                                }
                        }
                }
        }

    }

}