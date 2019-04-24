package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cop4331.group7.hangr.classes.FirebaseOutfitItem
import com.cop4331.group7.hangr.classes.OutfitsAdapter
import com.cop4331.group7.hangr.constants.HANGR_DB_STRING
import com.cop4331.group7.hangr.constants.OUTFITS_DB_STRING
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_outfits.view.*

// TODO: displays user's saved outfits (filter by favorites??)
class OutfitFragment: Fragment() {
    private lateinit var mView: View

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var viewAdapter: OutfitsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_outfits, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        mView.fab_add_outfit.setOnClickListener{ moveToCreateOutfit() }

        setupRecyclerView()

        return mView
    }

    private fun setupRecyclerView() {
        val outfits = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(OUTFITS_DB_STRING)

        val response = FirestoreRecyclerOptions.Builder<FirebaseOutfitItem>()
            .setQuery(outfits, FirebaseOutfitItem::class.java)
            .build()

        viewManager = GridLayoutManager(activity, 2)
        viewAdapter = OutfitsAdapter(mView.context, response, auth, db)

        mView.recycler_outfits.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.startListening()
    }

    private fun moveToCreateOutfit() {
        val intent = Intent(activity, CreateOutfitActivity::class.java)
        startActivity(intent)
    }
}