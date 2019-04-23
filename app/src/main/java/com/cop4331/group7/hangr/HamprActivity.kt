package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_hampr.*

class HamprActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hampr)

        text_clothes.text = "Hampr Activity"

        button_edit.setOnClickListener { moveToEditClothing() }
    }

    private fun moveToEditClothing() {
        intent = Intent(this, AddOrEditClothingActivity::class.java)
        startActivity(intent)
    }
}
