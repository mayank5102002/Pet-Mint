package com.leafcode.petmint.helpandsupport

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

//Viewmodel for help and support activity
class HelpandSupportViewModel : ViewModel() {

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

}