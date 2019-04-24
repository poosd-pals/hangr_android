package com.cop4331.group7.hangr

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.cop4331.group7.hangr.constants.CLOTHING_DB_STRING
import com.cop4331.group7.hangr.constants.HANGR_DB_STRING
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_hampr.*

class HamprActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var clothingRef: CollectionReference

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hampr)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        clothingRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab_clean_all.setOnClickListener { washClothes() }

        setupRecyclerView()
    }

    private fun washClothes() {
        val alert = AlertDialog.Builder(this)
            .setTitle("Wash Clothes")
            .setMessage("Are you sure you want remove everything from the Hampr?")
            .setPositiveButton("Yes"){_, _ ->
                // resets wearsLeft to the clothing's wearsTotal field
                for (index in 0 until viewAdapter.clothingKeys.size) {
                    clothingRef.document(viewAdapter.clothingKeys[index]).get().addOnSuccessListener { result ->
                        val wears = result!!["wearsTotal"] as Long
                        clothingRef.document(viewAdapter.clothingKeys[index]).update(mapOf("wearsLeft" to wears))

                    }
                }

                // update adapter after all items updated
                viewAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel"){_, _ ->
                // no action on cancel
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
