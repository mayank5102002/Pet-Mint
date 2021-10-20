package com.leafcode.petmint.locationSelector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

//Viewmodel for Location Selector
class LocationSelectorViewModel : ViewModel() {

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

    fun updateActionBarTitle(title: String) = _title.postValue(title)

    //Variable for the state selected
    var state : String = "Haryana"
        get() = field
        set(value) {
            field = value
        }

    //Variable for the City Selected
    var city : String = "Karnal"
        get() = field
        set(value) {
            field = value
        }
}