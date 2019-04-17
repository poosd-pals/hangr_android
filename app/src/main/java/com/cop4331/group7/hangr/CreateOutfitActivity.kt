package com.cop4331.group7.hangr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.cop4331.group7.hangr.classes.FirebaseClothingItem
import com.cop4331.group7.hangr.classes.OutfitAdapter
import com.cop4331.group7.hangr.constants.DESIRED_CATEGORY
import com.cop4331.group7.hangr.constants.SELECTED_OUTFIT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_closet_gallery.*
import kotlinx.android.synthetic.main.activity_create_outfit.*

class CreateOutfitActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var viewAdapter: OutfitAdapter? = null
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var outfit = mutableListOf<FirebaseClothingItem?>()

    // go to activity when navigation button is pressed
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_hampr -> {
                val intent = Intent(this@CreateOutfitActivity, HamprActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                val intent = Intent(this@CreateOutfitActivity, ClosetGalleryActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfit -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_outfit)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        title = "Assemble an Outfit!"

        navigation_outfit.menu.getItem(2).isChecked = true
        navigation_outfit.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fab_add_to_outfit.setOnClickListener { selectClothing() }
        button_finish_outfit.setOnClickListener { finalizeOutfit() }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(this@CreateOutfitActivity)
        viewAdapter = OutfitAdapter(this, outfit)

        recycler_outfit.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun updateRecycler() {
        viewAdapter!!.notifyDataSetChanged()
    }

    // open gallery filtered by category and add to recycler
    private fun selectClothing() {
        // popup category menu
        val category = selectCategory()

        // open gallery filtered by category
        val intent = Intent(this, ClosetGalleryActivity::class.java)
        intent.putExtra(DESIRED_CATEGORY, category)
        startActivityForResult(intent, 1)

    }

    private fun selectCategory(): String {
        // popup menu

        // capture user's desired category

        return "Accessories"
    }

    // account for each item being worn and return to closet
    private fun finalizeOutfit() {
        // TODO: for each clothing in outfit, decrement "wears remaining" field

        // move to closet
        intent = Intent(this, ClosetGalleryActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.hasExtra(SELECTED_OUTFIT)) {
                    val clothing = data.extras?.getParcelable<FirebaseClothingItem>(SELECTED_OUTFIT)
                    outfit.add(clothing)

                    Toast.makeText(this, "Item name: " + clothing?.name + ". Outfit size: " + outfit.size, Toast.LENGTH_LONG).show()
                    updateRecycler()
                } else {
                    Toast.makeText(this, "putExtra failed", Toast.LENGTH_LONG).show()
                }

            }
        }
    }
}
