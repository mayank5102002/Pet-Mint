package com.leafcode.petmint.signIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.databinding.FragmentSignInBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInFragment : Fragment() {

    //Variable to access Firebase
    private lateinit var auth : FirebaseAuth

    //Activity Result Launcher Intent to launch sign-in
    private val signIn : ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(),this::onSignInResult)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentSignInBinding = FragmentSignInBinding.inflate(inflater,container,false)

        auth = Firebase.auth




        return binding.root
    }

    override fun onStart() {
        super.onStart()

        //Providers for Sign-in
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        // If there is no signed in user, launch FirebaseUI
        // Otherwise head to Pets Fragment
        if (Firebase.auth.currentUser == null){
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build()

            signIn.launch(signInIntent)
        } else {
            goToPetsFragment()
        }
    }

    //Function to head to Pets Fragment
    private fun goToPetsFragment(){
       this.findNavController().navigate(SignInFragmentDirections.actionSignInFragment2ToPetsFragment())
    }

    //Function to handle the result of Sign-in
    private fun onSignInResult(result : FirebaseAuthUIAuthenticationResult){
        //If sign-in successful, go to Pets Fragment
        //else, show a toast message with error
        if(result.resultCode == AppCompatActivity.RESULT_OK){
            Log.i(TAG,"Sign-in successful")
            goToPetsFragment()
        } else {
            val response = result.idpResponse
            if (response == null){
                Log.w(TAG,"User cancelled Sign-in")
            } else {
                Log.e(TAG,"Error in Sign-in",response.error)
            }
        }
    }

    //Companion object with TAG for log messages
    companion object {
        private const val TAG = "SignInActivity"
    }
}