package com.leafcode.petmint.splashPage

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

class SplashPageViewModel : ViewModel(){

    //Creating a job and finishing it when activity is finished
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val motoArray = arrayListOf<String>(
        "Give a floof a pawfect home",
        "Adopt, don't shoppe",
        "An animal's eyes have the power to speak a great language",
        "Dogs are not our whole life, but they make our lives whole",
        "A house is never lonely, where a living dog awaits...",
        "Pets make our lives worth living"
    )

    private var _mainActivity = MutableLiveData<Boolean>()
    val mainActivity : LiveData<Boolean>
    get() = _mainActivity

    fun goToMainActivity(){
        _mainActivity.value = true
    }

    fun mainActivityDone(){
        _mainActivity.value = false
    }

    fun countDown(){

        try {
            object : CountDownTimer(500, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    goToMainActivity()
                }
            }.start()
        }catch (e : Exception){
            Log.e("Splash Page",e.toString())
            goToMainActivity()
        }

    }

    fun getMoto() : String{

        motoArray.shuffle()
        return motoArray[2]

    }

}