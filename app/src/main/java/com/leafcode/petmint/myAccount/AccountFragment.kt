package com.leafcode.petmint.myAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leafcode.petmint.databinding.AccountFragmentBinding

class AccountFragment : Fragment() {

    //Fragment acting as navcontroller for my account fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : AccountFragmentBinding = AccountFragmentBinding.inflate(inflater,container,false)

        return binding.root
    }
}