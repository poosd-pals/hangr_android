package com.cop4331.group7.hangr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser != null) {
            // TODO: create intent with user data, pass to new activity for logged in area
        }

    }
}
