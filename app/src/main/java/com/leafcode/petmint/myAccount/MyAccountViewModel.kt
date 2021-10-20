package com.leafcode.petmint.myAccount

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job

class MyAccountViewModel : ViewModel() {

    //Creating a job and finishing it when activity is finished
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Firebase authentication object
    private val auth : FirebaseAuth = Firebase.auth

    var _userName = auth.currentUser?.displayName.toString()
    var photoUrl = auth.currentUser?.photoUrl

    fun getUserName() : String{
        return _userName
    }

    //Function to update the info about the user
    fun authenticateUser(){
        _userName = auth.currentUser?.displayName.toString()
        photoUrl = auth.currentUser?.photoUrl
    }
}