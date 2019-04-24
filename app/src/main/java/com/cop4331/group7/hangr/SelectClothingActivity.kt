package com.cop4331.group7.hangr

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.FirebaseClothingItemQueryBuilder
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.constants.CATEGORIES
import com.cop4331.group7.hangr.constants.CLOTHING_DB_STRING
import com.cop4331.group7.hangr.constants.DESIRED_CATEGORY
import com.cop4331.group7.hangr.constants.HANGR_DB_STRING
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_select_clothing.*
import kotlinx.android.synthetic.main.fragment_clothing.view.*
import kotlinx.android.synthetic.main.fragment_outfits.*

// display clothing items for user to add to outfit
class SelectClothingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var clothesRef: CollectionReference

    private lateinit var viewAdapter: GalleryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var outfit = mutableListOf<FirebaseClothingItem?>()
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_clothing)

        setActivityState()
        initSpinner()
        setupRecyclerView()
    }

    private fun initSpinner() {
        val categories = mutableListOf<String>()
        categories.addAll(CATEGORIES)
        categories.add(0, "All")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_outfit.adapter = adapter

        spinner_outfit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = parent!!.adapter.getItem(position)
                var query: Query
                if (position == 0)
                    query = clothesRef
                else
                    query = clothesRef.whereEqualTo("category", category)

                val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
                    .setQuery(query, FirebaseClothingItem::class.java)
                    .build()

                viewAdapter = GalleryAdapter(this@SelectClothingActivity, response, false)

                recycler_select_clothing.adapter = viewAdapter
                viewAdapter.startListening()
            }
        }
    }

    // manage initial activity setup
    private fun setActivityState() {
        // database
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(
            CLOTHING_DB_STRING)

        // UI
        title = "Select an item!"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // display clothing items filtered by "category"
    private fun setupRecyclerView() {
        val clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)

        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(clothesRef, FirebaseClothingItem::class.java)
            .build()

        viewManager = GridLayoutManager(this, 2)
        viewAdapter = GalleryAdapter(this, response, true)

        recycler_select_clothing.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.startListening()
    }
}
