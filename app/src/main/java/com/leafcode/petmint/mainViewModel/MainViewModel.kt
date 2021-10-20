package com.leafcode.petmint.mainViewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Job

class MainViewModel(application : Application) : AndroidViewModel(application) {

    //Initialising a job and then finishing it when the activity is finished
    private var viewModeljob = Job()

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    override fun onCleared() {
        super.onCleared()
        viewModeljob.cancel()
    }
}