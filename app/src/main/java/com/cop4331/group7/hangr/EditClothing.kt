package com.cop4331.group7.hangr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_edit_clothing.*

class EditClothing : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_clothing)

        val intent = this.intent

        if (intent != null && intent.hasExtra(EXTRA_MESSAGE)) {
            val intentFromActivity = intent.extras.getString(EXTRA_MESSAGE)
            // Can not "delete" when coming from NewClothes, cancel finishes intent
            if (intentFromActivity.equals("NewClothesActivity")) {
                button_delete.isClickable = false
                button_delete.visibility = View.INVISIBLE
                button_cancel.setOnClickListener { finish() }
            }
            else {
                // TODO: populate existing values if editing existing item from closet

                button_cancel.setOnClickListener { moveToClosetGalleryActivity() }
            }
        }

        // TODO: allow user to edit fields or add tags

        // TODO: update DB with new information
    }

    private fun moveToClosetGalleryActivity() {
        intent = Intent(this, ClosetGalleryActivity::class.java)
        startActivity(intent)
        finish()
    }
}
