package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import hideKeyboard
import kotlinx.android.synthetic.main.activity_add_or_edit_clothing.*

class AddOrEditClothingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_clothing)

        val intent = this.intent

        if (intent != null && intent.hasExtra(EXTRA_MESSAGE)) {
            val intentFromActivity = intent.extras.getString(EXTRA_MESSAGE)
            // Can not "delete" when coming from NewClothes, cancel finishes intent
            if (intentFromActivity.equals("Add")) {
                button_delete.isClickable = false
                button_delete.visibility = View.INVISIBLE
            }
            else {
                // TODO: populate existing values if editing existing item from closet

            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // hide keyboard when clicking on the scrollview or nested linear layout
        add_or_edit_layout.setOnClickListener { it.hideKeyboard() }
        nested_layout.setOnClickListener { it.hideKeyboard() }

        // TODO: allow user to edit fields or add tags

        button_finish.setOnClickListener { updateAndFinish() }
    }

    fun updateAndFinish() {
        // TODO: store these in an object
        val clothingName = edit_clothing_name.text
        val clothingCategory = edit_clothing_category.text
        val clothingWears = edit_clothing_wears.text

        // store as string array? or just space separated tags?
        val colors = edit_clothing_colors.text
        val tags = edit_clothing_tags.text

        // TODO: save object to DB

        Toast.makeText(this, "Update DB with new info", Toast.LENGTH_LONG).show()
        finish()
    }
}
