package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import hideKeyboard
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide keyboard when anywhere outside a textfield or button is clicked
        linearLayout.setOnClickListener { it.hideKeyboard() }

        button_login.setOnClickListener { attemptLogin() }
        button_register.setOnClickListener { moveToRegisterActivity() }
    }

    private fun attemptLogin() {
        // TODO: validate fields and attempt login
        Toast.makeText(this, "Login time!", Toast.LENGTH_LONG).show()
    }

    private fun moveToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }
}

