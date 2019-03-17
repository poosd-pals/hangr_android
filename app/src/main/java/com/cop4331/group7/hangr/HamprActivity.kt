package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_hampr.*

class HamprActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_new_clothes -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                val intent = Intent(this@HamprActivity, ClosetGalleryActivity::class.java)
                startActivity(intent)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfit -> {
                val intent = Intent(this@HamprActivity, CreateOutfitActivity::class.java)
                startActivity(intent)
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
    }

    private fun moveToEditClothing() {
        intent = Intent(this, AddOrEditClothingActivity::class.java)
        intent.putExtra(EXTRA_MESSAGE, "HamprActivity")
        startActivity(intent)
    }
}