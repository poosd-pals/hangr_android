package com.cop4331.group7.hangr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.cop4331.group7.hangr.classes.AssembleOutfitAdapter
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.FirebaseOutfitItem
import com.cop4331.group7.hangr.constants.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_outfit.*

class CreateOutfitActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var clothingRef: CollectionReference
    private lateinit var outfitData: FirebaseOutfitItem

    private var viewAdapter: AssembleOutfitAdapter? = null
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var clothingReferences = mutableListOf<FirebaseClothingItem>()
    private var clothingKeys = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_outfit)

        initFirebase()
        setState()
        setupRecyclerView()
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        clothingRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)
    }

    private fun setState() {
        // TODO: pull in existing clothing data when editing outfit
        if (intent.hasExtra(EXISTING_OUTFIT_ITEM_DATA)) {
            title = "Edit Outfit"

            outfitData = intent.extras?.getParcelable(EXISTING_OUTFIT_ITEM_DATA)!!
            var str = ""
            val size = outfitData.clothingItems.size - 1

            Toast.makeText(this, "" + outfitData.clothingItems.size + " items in outfit", Toast.LENGTH_LONG).show()

            // for each clothing item in the outfit, make a FirebaseCLothingItem and add to list
            for (i in 0..size) {
                Log.d("CreateOutfitActivity", "pulling from DB")
                clothingRef.document(outfitData.clothingItems[i]).get().addOnCompleteListener {
                    val res = it.result!!
                    val item = FirebaseClothingItem(
                        res["name"].toString(),
                        res["category"].toString(),
                        (res["wearsTotal"] as Long).toInt(),
                        (res["wearsLeft"] as Long).toInt(),
                        res["colors"] as List<String>,
                        res["tags"] as List<String>,
                        res["imageUrl"].toString(),
                        res["imageFilename"].toString()
                    )
                    str += res.toString()
                    clothingReferences.add(item)
                }
            }
            if (str == "")
                Toast.makeText(this, "empty str", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this, str, Toast.LENGTH_LONG).show()
        } else {
            title = "Assemble an Outfit"
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab_add_to_outfit.setOnClickListener { selectClothingItem() }
        button_wear_outfit.setOnClickListener { wearOutfit() }
        button_save_outfit.setOnClickListener { saveOutfit() }
    }

    private fun setupRecyclerView() {
        viewManager = GridLayoutManager(this@CreateOutfitActivity, 2)
        viewAdapter = AssembleOutfitAdapter(this, clothingReferences)

        recycler_outfit.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun updateRecycler() {
        viewAdapter!!.notifyDataSetChanged()
    }

    // starts activity for choosing clothing items
    private fun selectClothingItem() {
        val intent = Intent(this, SelectClothingActivity::class.java)
        startActivityForResult(intent, 1)
    }

    // account for each item being worn and return to closet gallery
    private fun wearOutfit() {
        for (index in 0 until clothingReferences.count()) {
            val wears = clothingReferences[index].wearsLeft - 1
            clothingRef.document(clothingKeys[index]).update(mapOf("wearsLeft" to wears))
        }

        intent = Intent(this, ClosetGalleryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveOutfit() {
        val name = openNameDialog()
        val isFavorite = false
        saveOutfitToFirebase(name, isFavorite)
    }

    private fun openNameDialog(): String {

        return "Casual Friday"
    }

    private fun saveOutfitToFirebase(outfitName: String, isFavorite: Boolean) {
        val outfitItem = FirebaseOutfitItem(
            outfitName,
            isFavorite,
            clothingKeys as List<String>
        )

        with(db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(OUTFITS_DB_STRING)) {
            add(outfitItem).addOnCompleteListener { if (it.isSuccessful) finish() else handleFailure() }
        }
    }

    private fun handleFailure() {
        Toast.makeText(this, "Better luck next time, bud!", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.hasExtra(SELECTED_OUTFIT)) {
                    val clothing = data.extras?.getParcelable<FirebaseClothingItem>(SELECTED_OUTFIT)!!
                    clothingReferences.add(clothing)
                    val clothingKey = data.extras?.getString(EXISTING_CLOTHING_ITEM_PARENT_ID)!!
                    clothingKeys.add(clothingKey)

                    updateRecycler()
                }
            }
        }
    }
}
