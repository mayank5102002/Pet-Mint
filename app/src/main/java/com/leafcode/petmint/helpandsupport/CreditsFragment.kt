package com.leafcode.petmint.helpandsupport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.leafcode.petmint.R
import com.leafcode.petmint.databinding.FragmentCreditsBinding

//Fragment for credits page
class CreditsFragment : Fragment() {

    //Viewmodel of the activity help and support
    private val viewModel : HelpandSupportViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentCreditsBinding = FragmentCreditsBinding.inflate(layoutInflater,container,false)


        //Updating the title bar of the activity using its viewmodel
        viewModel.updateActionBarTitle(getString(R.string.about_us_String))


        return binding.root
    }
}