package com.leafcode.petmint.myads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leafcode.petmint.databinding.FragmentMyAdsControllerBinding

//Controller fragment for my ads section
class MyAdsControllerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentMyAdsControllerBinding = FragmentMyAdsControllerBinding.inflate(layoutInflater,container,false)


        return binding.root
    }
}