package com.leafcode.petmint.helpandsupport

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentReviewBinding
import com.leafcode.petmint.helpandsupport.Review.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ReviewFragment : Fragment() {

    //Viewmodel of the activity of this fragment
    private val viewModel : HelpandSupportViewModel by activityViewModels()

    //Objects of Realtime Database and Firebase Authentication
    private lateinit var db: FirebaseDatabase
    private val auth : FirebaseAuth = Firebase.auth

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentReviewBinding = FragmentReviewBinding.inflate(layoutInflater,container,false)

        //Updating the title of the action bar
        viewModel.updateActionBarTitle(getString(R.string.review_String))

        //Getting the reference for the reviews from database
        db = Firebase.database
        val myRef = db.getReference(getString(R.string.review_location))

        //Declaring submit button
        val submitButton = binding.submitButton

        // Setting click listener for the submit button
        submitButton.setOnClickListener {

            if (!checkInternetConnectivity()){
                Toast.makeText(this.requireActivity(),"No Internet connection",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reviewContent:String

            //Returning if the review is empty
            if (binding.reviewTextView.text.toString().equals("")){
                return@setOnClickListener
            } else {
                reviewContent = binding.reviewTextView.text.toString()
            }

            //Getting the current date and time for the review
            val currentTime = Calendar.getInstance().time
            val date = SimpleDateFormat("MMM d,yy").format(currentTime)
            val time = SimpleDateFormat("h:mm:ss a").format(currentTime)

            //Creating the review id and review object
            val id = auth.currentUser?.displayName.toString() + "-$date-$time"
            val review = Review(
                id,
               reviewContent,
                auth.currentUser?.displayName.toString()
            )

            //Submitting the review at its desired location in the storage
            myRef.child(auth.uid.toString()).child("$date-$time").setValue(review).addOnSuccessListener {
                Toast.makeText(this.activity,"Review Submitted",Toast.LENGTH_SHORT).show()
            }
            this.findNavController().navigateUp()
        }

        return binding.root
    }

    private fun checkInternetConnectivity() : Boolean{

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo?.isConnected != null){
            return networkInfo.isConnected
        } else {
            return false
        }

    }

}