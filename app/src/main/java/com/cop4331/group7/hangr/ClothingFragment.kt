package com.cop4331.group7.hangr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
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

// displays user's existing clothing items
// starts activity to add or edit clothing items
class ClothingFragment: Fragment() {
    private lateinit var mView: View

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var clothesRef: CollectionReference
    private lateinit var baseQuery: Query

    private lateinit var viewAdapter: GalleryAdapter
    private lateinit var viewManager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_clothing, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(
            CLOTHING_DB_STRING)
        baseQuery = clothesRef.whereGreaterThan("wearsLeft", 0)

        setFragmentState()
        initSpinner()

        return mView
    }

    private fun initSpinner() {
        val categories = mutableListOf<String>()
        categories.addAll(CATEGORIES)
        categories.add(0, "All")

        mView.spinner_filter.adapter = ArrayAdapter<String>(this.context, R.layout.spinner_inflator, CATEGORIES)

        mView.spinner_filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = parent!!.adapter.getItem(position)
                val query: Query
                if (position == 0)
                    query = baseQuery
                else
                    query = baseQuery.whereEqualTo("category", category)

                val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
                    .setQuery(query, FirebaseClothingItem::class.java)
                    .build()

                viewAdapter = GalleryAdapter(activity as Activity, response, false)

                mView.recycler_gallery.adapter = viewAdapter
                viewAdapter.startListening()
            }
        }
    }

    private fun setFragmentState() {
        mView.fab_add_clothes.setOnClickListener { createNewClothingItem() }
        setupRecyclerView()
    }

    // builds query and applies adapter to the recycler
    private fun setupRecyclerView() {
        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(baseQuery, FirebaseClothingItem::class.java)
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