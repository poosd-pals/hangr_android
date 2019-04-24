package com.cop4331.group7.hangr

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.constants.CLOTHING_DB_STRING
import com.cop4331.group7.hangr.constants.HANGR_DB_STRING
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_hampr.*
import kotlinx.android.synthetic.main.fragment_clothing.*

class HamprActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hampr)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab_clean_all.setOnClickListener { washClothes() }

        setupRecyclerView()
    }

    private fun washClothes() {
        val alert = AlertDialog.Builder(this)
            .setTitle("Wash Clothes")
            .setMessage("Are you sure you want remove everything from the Hampr?")
            .setPositiveButton("Yes"){dialog, which ->
                Toast.makeText(this, "Washing clothes", Toast.LENGTH_LONG).show()
                // TODO: reset the wearsLeft values to wearsTotal

                // update recycler
                viewAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel"){dialog, which ->
                Toast.makeText(this, "Laundry is later", Toast.LENGTH_LONG).show()
            }
            .create()

        alert.show()
    }

    private fun setupRecyclerView() {
        viewManager = GridLayoutManager(this, 2)

        val query = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)
        val dirtyClothes = query.whereEqualTo("wearsLeft", 0)

        val response = FirestoreRecyclerOptions.Builder<FirebaseClothingItem>()
            .setQuery(dirtyClothes, FirebaseClothingItem::class.java)
            .build()

        viewAdapter = GalleryAdapter(this, response, false)

        recycler_hampr.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.startListening()
    }
}
