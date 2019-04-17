package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.constants.DESIRED_CATEGORY
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_closet_gallery.*
import java.util.ArrayList

class ClosetGalleryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var viewAdapter: GalleryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var isSelectingForOutfit = false
    private var outfit: ArrayList<FirebaseClothingItem>? = null
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

        setActivityState()

        // get navigation view and set current item to checked
        navigation_gallery.menu.getItem(1).isChecked = true
        navigation_gallery.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        viewAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        viewAdapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.actionbar, menu)
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
            fab_add_clothes.hide()
            // TODO: is this necessary with .hide()?
            fab_add_clothes.isClickable = false

            // existing outfit and desired category from outfit being assembled
            category = intent.extras?.getString(DESIRED_CATEGORY)
        } else {
            title = "Welcome, " + auth.currentUser!!.displayName + "!"
            fab_add_clothes.setOnClickListener { createNewClothingItem() }
        }

    }

    private fun setupRecyclerView() {
        viewManager = GridLayoutManager(this@ClosetGalleryActivity, 2)

        val clothesRef = db.collection(auth.currentUser!!.uid)
        val query: Query

        // filter for selected category if applicable
        if (isSelectingForOutfit) {
            query = clothesRef.whereEqualTo("category", category)
        } else {
            query = clothesRef
        }

        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(query, FirebaseClothingItem::class.java)
            .build()

        viewAdapter = GalleryAdapter(this, response, isSelectingForOutfit)

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
