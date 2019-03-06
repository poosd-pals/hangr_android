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

        intent = this.intent

        // TODO: populate layout fields with info passed to this activity via intents
        // TODO: if we came from NewClothes, hide delete button
        if (intent != null) {
            var sourceActivity = intent.extras.getString(EXTRA_MESSAGE)
            if (!sourceActivity.equals("ClosetGalleryActivity")) {
                button_delete.isClickable = false
                button_delete.visibility = View.INVISIBLE
            }
        }



        // TODO: allow user to edit fields or add tags

        // TODO: update DB with new information

        // return to gallery
        button_cancel.setOnClickListener { moveToClosetGalleryActivity() }
    }

    private fun moveToClosetGalleryActivity() {
        intent = Intent(this, ClosetGalleryActivity::class.java)
        startActivity(intent)
    }
}
