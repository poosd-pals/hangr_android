package com.cop4331.group7.hangr

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.Toast
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.FirebaseOutfitItem
import com.cop4331.group7.hangr.classes.OutfitAdapter
import com.cop4331.group7.hangr.constants.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_outfit.*
import java.lang.Exception

class CreateOutfitActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var viewAdapter: OutfitAdapter? = null
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var clothingReferences = mutableListOf<FirebaseClothingItem>()
    private var clothingKeys = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_outfit)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        title = "Assemble an Outfit!"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab_add_to_outfit.setOnClickListener { openCategoryDialog() }
        button_wear_outfit.setOnClickListener { wearOutfit() }
        button_save_outfit.setOnClickListener { saveOutfit() }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(this@CreateOutfitActivity)
        viewAdapter = OutfitAdapter(this, clothingReferences)

        recycler_outfit.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun updateRecycler() {
        viewAdapter!!.notifyDataSetChanged()
    }

    // prompt user which category they want to search for
    private fun openCategoryDialog() {
        // popup menu
        val dialog = AlertDialog.Builder(this)
            .setTitle("Select a category...")
            .setItems(
                CATEGORIES.toTypedArray() ) { _, i ->
                selectClothingItemWithCategory(CATEGORIES[i])
            }
            .create()

        dialog.show()
    }

    // starts activity, passing the category as extra
    private fun selectClothingItemWithCategory(category: String) {
        // open gallery filtered by category
        val intent = Intent(this, SelectClothingActivity::class.java)
        intent.putExtra(DESIRED_CATEGORY, category)
        startActivityForResult(intent, 1)
    }

    // account for each item being worn and return to closet gallery
    private fun wearOutfit() {
        // TODO: for each clothing in clothingReferences, decrement "wearsLeft remaining" field

        // move to closet
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

        return "potato"
    }

    private fun saveOutfitToFirebase(outfitName: String, isFavorite: Boolean) {
        val outfitItem = FirebaseOutfitItem(
            outfitName,
            isFavorite,
            clothingKeys as List<String>
        )

        with(db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(OUTFITS_DB_STRING)) {
            add(outfitItem).addOnCompleteListener { if (it.isSuccessful) finish() else handleFailure(it.exception) }
        }
    }

    private fun handleFailure(e: Exception?) {
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

                    Toast.makeText(this, "Item name: " + clothing?.name + ". Outfit size: " + clothingReferences.size, Toast.LENGTH_LONG).show()
                    updateRecycler()
                } else {
                    Toast.makeText(this, "putExtra failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
