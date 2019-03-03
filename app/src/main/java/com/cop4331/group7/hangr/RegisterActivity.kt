package com.cop4331.group7.hangr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import hideKeyboard
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Hide keyboard when anywhere outside a textfield or button is clicked
        layout.setOnClickListener { it.hideKeyboard() }

        button_register.setOnClickListener { attemptRegister() }
        button_login.setOnClickListener { moveToLoginActivity() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
    private fun attemptRegister() {
        // TODO: validate fields and register to firebase
        Toast.makeText(this, "time to register!", Toast.LENGTH_LONG).show()
    }

    private fun moveToLoginActivity() {
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}

