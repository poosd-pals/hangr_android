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
import com.cop4331.group7.hangr.classes.GalleryAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_closet_gallery.*

const val EXTRA_MESSAGE = "com.cop4331.group7.hangr.checkParent"

class ClosetGalleryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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

        fab_add_clothes.setOnClickListener { createNewClothingItem() }

        title = "Welcome, " + auth.currentUser!!.displayName + "!"

        // get navigation view and set current item to checked
        navigation.menu.getItem(1).isChecked = true
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // recycler view
        viewManager = GridLayoutManager(this@ClosetGalleryActivity, 2)
        viewAdapter = GalleryAdapter(
            arrayOf(
                "images",
                "loaded",
                "from",
                "DB",
                "should",
                "go",
                "here"
            )
        )

        recycler_gallery.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun createNewClothingItem() {
        val intent = Intent(this@ClosetGalleryActivity, AddOrEditClothingActivity::class.java)
        startActivity(intent)
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

    private fun handleLogout() {
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)

        // clear the backstack
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)
        finish()
    }
}
