package com.leafcode.petmint

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.leafcode.petmint.databinding.ActivityMainBinding
import com.leafcode.petmint.mainViewModel.MainViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    //Declaring different variables to use
    private val viewModel : MainViewModel by viewModels()
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setting the binding and the layout
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialising the views
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        //Initialising the ViewPager adapter with the tablayout
        val viewPagerAdapter = ViewPagerAdapter(this,supportFragmentManager)
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        MobileAds.initialize(this)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
    }
}