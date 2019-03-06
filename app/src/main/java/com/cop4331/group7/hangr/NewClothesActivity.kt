package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_new_clothes.*

class NewClothesActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_new_clothes -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                val intent = Intent(this@NewClothesActivity, ClosetGalleryActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfit -> {
                val intent = Intent(this@NewClothesActivity, CreateOutfitActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_clothes)

        text_clothes.text = "New Clothes Activity"
        navigation.menu.getItem(0).isChecked = true

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
