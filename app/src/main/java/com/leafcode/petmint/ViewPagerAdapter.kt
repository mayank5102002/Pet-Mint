package com.leafcode.petmint

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.leafcode.petmint.myAccount.AccountFragment
import com.leafcode.petmint.myads.MyAdsControllerFragment
import com.leafcode.petmint.pets.PetsControllerFragment

//Names for different tabs for the tablayout
private val TAB_TITLES = arrayOf(
    R.string.my_pets,
    R.string.my_ads,
    R.string.my_account
)

class ViewPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    //Function for getting the item at a certain position
    override fun getItem(position: Int): Fragment {
        when(position){
            0-> return PetsControllerFragment()
            1-> return MyAdsControllerFragment()
            else -> return AccountFragment()
        }
    }

    //Function to get the name of the page
    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    //Function to get the number of pages to form
    override fun getCount(): Int {
        // Show 3 total pages.
        return 3
    }
}