package com.cop4331.group7.hangr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.FirebaseClothingItemQueryBuilder
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.classes.OutfitAdapter
import com.cop4331.group7.hangr.constants.CLOTHING_DB_STRING
import com.cop4331.group7.hangr.constants.DESIRED_CATEGORY
import com.cop4331.group7.hangr.constants.HANGR_DB_STRING
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_select_clothing.*
import kotlinx.android.synthetic.main.fragment_clothing.view.*

// display clothing items for user to add to outfit
class SelectClothingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var viewAdapter: GalleryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var outfit = mutableListOf<FirebaseClothingItem?>()
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_clothing)

        setActivityState()
        setupRecyclerView()
    }

    // manage initial activity setup
    private fun setActivityState() {
        // database
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // UI
        title = "Select an item!"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // unpack intent
        if (intent.hasExtra(DESIRED_CATEGORY)) {
            category = intent.extras?.getString(DESIRED_CATEGORY)
        } else {
            Toast.makeText(this, "no category passed to intent... how?", Toast.LENGTH_LONG).show()
        }
    }

    // display clothing items filtered by "category"
    private fun setupRecyclerView() {
        val clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)
        val clothingItemQuery = FirebaseClothingItemQueryBuilder(clothesRef)

        // add category to filter
        clothingItemQuery.addCategories(listOf(category!!))

        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(clothingItemQuery.build(), FirebaseClothingItem::class.java)
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
