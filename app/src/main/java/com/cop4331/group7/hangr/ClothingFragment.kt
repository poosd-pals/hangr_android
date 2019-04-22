package com.cop4331.group7.hangr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.FirebaseClothingItemQueryBuilder
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.constants.CATEGORIES
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_clothing.view.*
import kotlinx.android.synthetic.main.inner_filter_view.view.*

// displays user's existing clothing items
// starts activity to add or edit clothing items
class ClothingFragment: Fragment() {
    private lateinit var mView: View

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var viewAdapter: GalleryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_clothing, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setFragmentState()
        initExpandableListeners()

        return mView
    }

    private fun setFragmentState() {
        mView.fab_add_clothes.setOnClickListener { createNewClothingItem() }
        setupRecyclerView()
    }

    // inflates filter menu
    private fun initExpandableListeners() {
        val colors: ChipGroup = mView.filter_chip_group
        mView.filter_chip_group.isSingleSelection = false

        CATEGORIES.forEach {
            val chip = Chip(activity)
            chip.text = it
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { button, b -> Toast.makeText(button.context, "${button.text} checked is $b", Toast.LENGTH_SHORT).show() }
            colors.addView(chip)
            Log.d("whee", "created chip $it")
        }
    }

    // builds query and applies adapter to the recycler
    private fun setupRecyclerView() {
        val clothesRef = db.collection(auth.currentUser!!.uid)
        val clothingItemQuery = FirebaseClothingItemQueryBuilder(clothesRef)

        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(clothingItemQuery.build(), FirebaseClothingItem::class.java)
            .build()

        viewManager = GridLayoutManager(activity, 2)
        viewAdapter = GalleryAdapter(activity as Activity, response, false)

        mView.recycler_gallery.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    // starts activity to add clothing item
    private fun createNewClothingItem() {
        val intent = Intent(activity, AddOrEditClothingActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        viewAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        viewAdapter.stopListening()
    }
}