package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_create_outfit.*

class CreateOutfitActivity : AppCompatActivity() {

    // go to activity when navigation button is pressed
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_new_clothes -> {
                val intent = Intent(this@CreateOutfitActivity, NewClothesActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                val intent = Intent(this@CreateOutfitActivity, ClosetGalleryActivity::class.java)
                startActivity(intent)
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
        var title = findViewById<TextView>(R.id.text_outfit)
        title.setText("New Outfit Activity")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation)
        bottomNavigationView.menu.getItem(2).isChecked = true

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
