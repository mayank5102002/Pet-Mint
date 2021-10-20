package com.leafcode.petmint.helpandsupport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leafcode.petmint.databinding.FragmentHelpSupportBinding
import com.google.android.gms.ads.AdRequest

class HelpSupportFragment : Fragment() {

    //Viewmodel of the activity of this fragment
    private val viewModel : HelpandSupportViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentHelpSupportBinding = FragmentHelpSupportBinding.inflate(inflater,container,false)

        //Initialising Buttons
        val reportBugButton = binding.reportBugButton
        val submitReviewButton = binding.submitReviewButton
        val creditsButton = binding.creditsButton

        val helpAndSupportFragmentAd = binding.adView

        val adRequest = AdRequest.Builder().build()
        helpAndSupportFragmentAd.loadAd(adRequest)

        //Setting onClickListeners for buttons
        reportBugButton.setOnClickListener {
            goToReportBugFragment()
        }

        submitReviewButton.setOnClickListener {
            goToReviewFragment()
        }

        creditsButton.setOnClickListener {
            goToCreditsFragment()
        }

        //Setting the title of action bar
        viewModel.updateActionBarTitle("Help & Support")

        return binding.root
    }

    //Function to navigate to Bug Fragment
    fun goToReportBugFragment(){
        this.findNavController().navigate(HelpSupportFragmentDirections.actionHelpSupportFragmentToReportBugFragment())
    }

    //Function to navigate to Review Fragment
    fun goToReviewFragment(){
        this.findNavController().navigate(HelpSupportFragmentDirections.actionHelpSupportFragmentToReviewFragment())
    }

    //Function to navigate to Credits Fragment
    fun goToCreditsFragment(){
        this.findNavController().navigate(HelpSupportFragmentDirections.actionHelpSupportFragmentToCreditsFragment())
    }
}