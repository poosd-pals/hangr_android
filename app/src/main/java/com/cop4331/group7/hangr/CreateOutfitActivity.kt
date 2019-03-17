package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_outfit.*

class CreateOutfitActivity : AppCompatActivity() {

    // go to activity when navigation button is pressed
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_new_clothes -> {
                val intent = Intent(this@CreateOutfitActivity, HamprActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                val intent = Intent(this@CreateOutfitActivity, ClosetGalleryActivity::class.java)
                startActivity(intent)
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

        text_outfit.text = "New Outfit Activity"
        navigation.menu.getItem(2).isChecked = true

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        button_add_clothes.setOnClickListener { moveToCloset() }
    }

    private fun moveToCloset() {
        // TODO: popup asking to select a category

        // move to closet
        intent = Intent(this, ClosetGalleryActivity::class.java)
        intent.putExtra(EXTRA_MESSAGE, "AddItem to Outfit")
        startActivity(intent)
    }
}
