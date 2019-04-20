package com.cop4331.group7.hangr.classes

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.cop4331.group7.hangr.ClothingFragment
import com.cop4331.group7.hangr.OutfitFragment

// TODO: super hacky. Make addFragment function?
// Adapter to manage fragments in the main gallery
class GalleryPagerAdapter(fragment: FragmentManager): FragmentPagerAdapter(fragment) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ClothingFragment()
            else -> OutfitFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Clothing"
            else -> "Outfits"
        }
    }
}