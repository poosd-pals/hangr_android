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
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.FirebaseClothingItemQueryBuilder
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.constants.CATEGORIES
import com.cop4331.group7.hangr.constants.CLOTHING_DB_STRING
import com.cop4331.group7.hangr.constants.HANGR_DB_STRING
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_clothing.view.*
import kotlinx.android.synthetic.main.inner_filter_view.view.*

// displays user's existing clothing items
// starts activity to add or edit clothing items
class ClothingFragment: Fragment() {
    private lateinit var mView: View

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var queryBuilder: FirebaseClothingItemQueryBuilder
    private lateinit var clothesRef: CollectionReference

    private lateinit var viewAdapter: GalleryAdapter
    private lateinit var viewManager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_clothing, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(
            CLOTHING_DB_STRING)
        queryBuilder = FirebaseClothingItemQueryBuilder(clothesRef)

        setFragmentState()
        initExpandableListeners()

        return mView
    }

    private fun setFragmentState() {
        mView.fab_add_clothes.setOnClickListener { createNewClothingItem() }
        // TODO: this doesn't work?
        mView.recycler_gallery.setOnClickListener { collapseFilter() }
        setupRecyclerView()
    }

    private fun collapseFilter() {
        mView.expand_filter.collapse()
    }

    // inflates filter menu
    private fun initExpandableListeners() {
        val colors: ChipGroup = mView.filter_chip_group
        mView.filter_chip_group.isSingleSelection = false

        CATEGORIES.forEach {
             val chip = Chip(activity)
             chip.text = it
             chip.isCheckable = true
             chip.setOnCheckedChangeListener { button, isSelected ->
                 if (isSelected)
                     queryBuilder.addCategories(listOf(button.text as String))
                 else
                     queryBuilder.removeCategories(listOf(button.text as String))

                 val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
                     .setQuery(queryBuilder.build(), FirebaseClothingItem::class.java)
                     .build()

                 val viewAdapter = GalleryAdapter(activity as Activity, response, false)
                 mView.recycler_gallery.adapter = viewAdapter
                 viewAdapter.startListening()
                 collapseFilter()
             }
             colors.addView(chip)
             Log.d("whee", "created chip $it")
         }
         colors.isSingleSelection = true
    }

    // builds query and applies adapter to the recycler
    private fun setupRecyclerView() {
        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(queryBuilder.build(), FirebaseClothingItem::class.java)
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