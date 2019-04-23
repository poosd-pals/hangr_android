package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_or_edit_clothing.view.*
import kotlinx.android.synthetic.main.activity_closet_gallery.*
import kotlinx.android.synthetic.main.activity_hampr.*

class HamprActivity : AppCompatActivity() {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var viewAdapter: GalleryAdapter



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_hampr -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                val intent = Intent(this@HamprActivity, ClosetGalleryActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfit -> {
                val intent = Intent(this@HamprActivity, CreateOutfitActivity::class.java)
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
        setContentView(R.layout.activity_hampr)

        text_clothes.text = "Hampr Activity"
        navigation.menu.getItem(0).isChecked = true
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        button_edit.setOnClickListener { moveToEditClothing() }

        setupRecyclerView()
    }

    private fun moveToEditClothing() {
        intent = Intent(this, AddOrEditClothingActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        viewManager = GridLayoutManager(this@HamprActivity, 2)


        val query = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)
        val dirtyClothes = query.whereEqualTo("wearsRemaining", 0)
        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(query, FirebaseClothingItem::class.java)
            .build()

        viewAdapter = GalleryAdapter(response)

        recycler_gallery.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewAdapter.startListening()
    }
}
