package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.FirebaseClothingItemQueryBuilder
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.constants.CATEGORIES
import com.cop4331.group7.hangr.constants.DESIRED_CATEGORY
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_closet_gallery.*
import kotlinx.android.synthetic.main.inner_filter_view.*


class ClosetGalleryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var queryBuilder: FirebaseClothingItemQueryBuilder

    private var isSelectingForOutfit = false
    private var category: String? = null

    // go to activity when navigation button is pressed
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_hampr -> {
                val intent = Intent(this@ClosetGalleryActivity, HamprActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfit -> {
                val intent = Intent(this@ClosetGalleryActivity, CreateOutfitActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closet_gallery)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        queryBuilder = FirebaseClothingItemQueryBuilder(db.collection(auth.currentUser!!.uid))

        setActivityState()
        setupRecyclerView()
        initExpandableListeners()
    }

    private fun initExpandableListeners() {
        val colors: ChipGroup = filter_chip_group
        filter_chip_group.isSingleSelection = false

        CATEGORIES.forEach {
            val chip = Chip(this)
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

                val viewAdapter = GalleryAdapter(this, response, isSelectingForOutfit)
                recycler_gallery.adapter = viewAdapter
                viewAdapter.startListening()
            }
            colors.addView(chip)
            Log.d("whee", "created chip $it")
        }
        colors.isSingleSelection = true
    }

    override fun onStart() {
        super.onStart()
        with (recycler_gallery.adapter as GalleryAdapter) { this.startListening()}
    }

    override fun onStop() {
        super.onStop()
        with (recycler_gallery.adapter as GalleryAdapter) { this.stopListening() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.gallery_actionbar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionbar_logout -> {
                handleLogout()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActivityState() {
        if (intent.hasExtra(DESIRED_CATEGORY)) {
            isSelectingForOutfit = true
            title = "Select an item!"

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // hide fab and bottom nav
            fab_add_clothes.hide()
            navigation.visibility = View.GONE

            // existing outfit and desired category from outfit being assembled
            category = intent.extras?.getString(DESIRED_CATEGORY)
        } else {
            title = "Welcome, " + auth.currentUser!!.displayName + "!"
            fab_add_clothes.setOnClickListener { createNewClothingItem() }

            // get navigation view and set current item to checked
            navigation.menu.getItem(1).isChecked = true
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        }

    }

    private fun setupRecyclerView() {
        viewManager = GridLayoutManager(this@ClosetGalleryActivity, 2)

        val clothesRef = db.collection(auth.currentUser!!.uid)
        val clothingItemQuery = FirebaseClothingItemQueryBuilder(clothesRef)

        // filter for selected category if applicable
        if (isSelectingForOutfit && !category.isNullOrBlank())
            clothingItemQuery.addCategories(listOf(category!!))

        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(clothingItemQuery.build(), FirebaseClothingItem::class.java)
            .build()

        val viewAdapter = GalleryAdapter(this, response, isSelectingForOutfit)

        recycler_gallery.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun createNewClothingItem() {
        val intent = Intent(this@ClosetGalleryActivity, AddOrEditClothingActivity::class.java)
        startActivity(intent)
    }

    private fun handleLogout() {
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
