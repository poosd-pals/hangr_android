package com.cop4331.group7.hangr.classes

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.cop4331.group7.hangr.ClothingFragment
import com.cop4331.group7.hangr.OutfitFragment

// Adapter to manage fragments in the main gallery
class GalleryPagerAdapter(fragment: FragmentManager): FragmentPagerAdapter(fragment) {
    private val fragments = listOf(ClothingFragment(), OutfitFragment())
    private val fragmentTitles = listOf("Clothing", "Outfits")

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitles[position]
    }
}