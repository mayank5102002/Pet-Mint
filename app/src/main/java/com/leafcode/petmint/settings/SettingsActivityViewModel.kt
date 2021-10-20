package com.leafcode.petmint.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivityViewModel : ViewModel() {

    //Creating a job and finishing it when activity is finished
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Live data for the title of the action bar
    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    fun updateActionBarTitle(title: String) = _title.postValue(title)

    var auth = Firebase.auth
    val user = auth.currentUser
    var userName = user?.displayName
    var photoUrl = user?.photoUrl
    var email = user?.email

    private var _nameChange = MutableLiveData<Boolean>()
    val nameChange : LiveData<Boolean>
    get() = _nameChange

    fun namechangeOccured(){
        _nameChange.value = true
    }

    fun namechangeSuccessful(){
        _nameChange.value = false
    }

    fun changeName(){
        userName = user?.displayName
    }

    private var _emailChange = MutableLiveData<Boolean>()
    val emailChange : LiveData<Boolean>
        get() = _emailChange

    fun emailchangeOccured(){
        _emailChange.value = true
    }

    fun emailchangeSuccessful(){
        _emailChange.value = false
    }

    fun changeEmail(){
        email = user?.email
    }

    private var _passwordChange = MutableLiveData<Boolean>()
    val passwordChange : LiveData<Boolean>
        get() = _passwordChange

    fun passwordchangeOccured(){
        _passwordChange.value = true
    }

    fun passwordchangeSuccessful(){
        _passwordChange.value = false
    }

    private var _profilePicChange = MutableLiveData<Boolean>()
    val profilePicChange : LiveData<Boolean>
        get() = _profilePicChange

    fun profilePicchangeOccured(){
        _profilePicChange.value = true
    }

    fun profilePicchangeSuccessful(){
        _profilePicChange.value = false
    }

    private var _error = MutableLiveData<Boolean>()
    val error : LiveData<Boolean>
        get() = _error

    fun errorOccured(){
        _error.value = true
    }

    fun errorSolved(){
        _error.value = false
    }

    private var _passworderror = MutableLiveData<Boolean>()
    val passworderror : LiveData<Boolean>
        get() = _passworderror

    fun passworderrorOccured(){
        _passworderror.value = true
    }

    fun passworderrorSolved(){
        _passworderror.value = false
    }

    private var _emailEmpty = MutableLiveData<Boolean>()
    val emailEmpty : LiveData<Boolean>
        get() = _emailEmpty

    fun emailIsEmpty(){
        _emailEmpty.value = true
    }

    fun emailEmptyAfter(){
        _emailEmpty.value = false
    }

    private var _verificationemail = MutableLiveData<Boolean>()
    val verificationemail : LiveData<Boolean>
        get() = _verificationemail

    fun verificationEmailToSend(){
        _verificationemail.value = true
    }

    fun verificationEmailSent(){
        _verificationemail.value = false
    }

    fun updateUserName(newName : String){

        viewModelScope.launch {
            updateName(newName)
        }

    }

    fun updateEmailAddress(newEmail : String){

        viewModelScope.launch {
            updateEmail(newEmail)
        }

    }

    fun updateNewPassword(newPassword : String){

        viewModelScope.launch {
            updatePassword(newPassword)
        }

    }

    fun updateProfilePicture(newPicture : Uri){

        viewModelScope.launch {
            updateImage(newPicture)
        }

    }

    fun resetPassword(){

        viewModelScope.launch {
            resetPasswordEmail()
        }

    }

    private suspend fun updateName(newName: String){

        return withContext(Dispatchers.IO){

            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }

            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        namechangeOccured()
                    }
                }.addOnFailureListener{
                    errorOccured()
                    Log.e("User name Change",it.toString())
                }

        }

    }

    private suspend fun updateEmail(newEmail: String){

        return withContext(Dispatchers.IO){

            user!!.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emailchangeOccured()
                    }
                }.addOnFailureListener{
                    errorOccured()
                    Log.e("Email Change",it.toString())
                }
        }

    }

    private suspend fun updatePassword(newPassword: String){

        return withContext(Dispatchers.IO){

            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        passwordchangeOccured()
                    }
                }.addOnFailureListener{
                    passworderrorOccured()
                    Log.e("Password Change",it.toString())
                }

        }

    }

    private suspend fun updateImage(newPicture: Uri){

        return withContext(Dispatchers.IO){

            val profileUpdates = userProfileChangeRequest {
                photoUri = newPicture
            }

            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        profilePicchangeOccured()
                    }
                }.addOnFailureListener{
                    errorOccured()
                    Log.e("Profile Picture Change",it.toString())
                }

        }

    }

    private suspend fun resetPasswordEmail(){

        return withContext(Dispatchers.IO){

            if (email != null || email != ""){

                auth.sendPasswordResetEmail(email!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            verificationEmailToSend()
                        }
                    }.addOnFailureListener {
                        errorOccured()
                    }

            } else {
                emailIsEmpty()
            }

        }

    }

}