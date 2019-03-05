package com.cop4331.group7.hangr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class EditClothing : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_clothing)

        // TODO: populate layout fields with info passed to this activity via intents
        // TODO: if we came from NewClothes, hide delete button

        // TODO: allow user to edit fields or add tags

        // TODO: update DB with new information

        // return to gallery
        val intent = Intent(this, ClosetGalleryActivity::class.java)
        startActivity(intent)
    }
}
