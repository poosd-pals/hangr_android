package com.cop4331.group7.hangr

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_outfits.view.*

// TODO: displays user's saved outfits (filter by favorites??)
class OutfitFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        view.button_outfits_tab.setOnClickListener{
            Toast.makeText(activity, "Outfit Tab", Toast.LENGTH_LONG).show()
        }

        return view
    }
}