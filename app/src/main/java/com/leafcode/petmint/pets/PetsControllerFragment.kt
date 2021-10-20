package com.leafcode.petmint.pets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leafcode.petmint.databinding.FragmentPetsControllerBinding

class PetsControllerFragment : Fragment() {

    //Fragment as the navController for Pet-Place
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentPetsControllerBinding = FragmentPetsControllerBinding.inflate(inflater,container,false)


        return binding.root
    }

}