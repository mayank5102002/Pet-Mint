package com.leafcode.petmint.helpandsupport

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentReportBugBinding
import com.leafcode.petmint.helpandsupport.bug.Bug
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ReportBugFragment : Fragment() {

    //Viewmodel of the activity of this fragment
    private val viewModel : HelpandSupportViewModel by activityViewModels()

    //Objects for Firebase Realtime Database and Firebase Authentication
    lateinit var db : FirebaseDatabase
    private val auth : FirebaseAuth = Firebase.auth

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentReportBugBinding = FragmentReportBugBinding.inflate(layoutInflater,container,false)

        //Updating the title of action bar
        viewModel.updateActionBarTitle(getString(R.string.report_bug))

        //Initialising the database reference for bug reports
        db = Firebase.database
        val myRef = db.getReference(getString(R.string.bug_report_location))

        //Declaring submit button
        val submitButton = binding.submitButton

        //Setting onClickListener for submit button
        submitButton.setOnClickListener {

            if (!checkInternetConnectivity()){
                Toast.makeText(this.requireActivity(),"No Internet connection",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bugContent:String

            //Returning if the report is empty
            if (binding.bugTextView.text.toString().equals("")){
                return@setOnClickListener
            } else {
                bugContent = binding.bugTextView.text.toString()
            }

            //Getting the current date and time to report bug
            val currentTime = Calendar.getInstance().time
            val date = SimpleDateFormat("MMM d,yy").format(currentTime)
            val time = SimpleDateFormat("h:mm:ss a").format(currentTime)

            //Creating the id for the bug and creating the bug object for current bug report
            val id = auth.currentUser?.displayName.toString() + "-$date-$time"
            val bug = Bug(
                id,
                bugContent,
                auth.currentUser?.displayName.toString()
            )

            //Submitting the bug object to its desired location in the realtime database
            myRef.child(auth.uid.toString()).child("$date-$time").setValue(bug).addOnSuccessListener {
                Toast.makeText(this.activity,"Bug succesfully reported", Toast.LENGTH_SHORT).show()
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