package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import hideKeyboard
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null)
            moveToClosetGallery()

        // Hide keyboard when anywhere outside a textfield or button is clicked
        linearLayout.setOnClickListener { it.hideKeyboard() }

        button_login.setOnClickListener { attemptLogin() }
        button_register.setOnClickListener { moveToRegisterActivity() }

        // MARK: Temporary to bypass login screen
        button_bypass.setOnClickListener { loginToDummyAccount() }
    }

    private fun loginToDummyAccount() {
        auth.signInWithEmailAndPassword("admin@admin.com", "password1")
            .addOnCompleteListener {
                if (it.isSuccessful) { moveToClosetGallery() }
                else { handleLoginFailure(it) }
            }
    }

    private fun attemptLogin() {
        button_login.rootView.hideKeyboard()

        if (!validateFields())
            return

        button_login.isEnabled = false
        button_register.isEnabled = false
        button_bypass.isEnabled = false
        indeterminateBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(field_email.text.toString(), field_password.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) { moveToClosetGallery() }
                else { handleLoginFailure(it) }
            }
    }

    private fun handleLoginFailure(task: Task<AuthResult>) {
        if (task.exception == null) {
            Toast.makeText(this, "Something went wrong attempting to login!", Toast.LENGTH_LONG).show()
        }
        else {
            when (task.exception){
                is FirebaseAuthInvalidUserException -> {
                    field_email.requestFocus()
                    field_email.error = task.exception!!.localizedMessage
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    field_password.requestFocus()
                    field_password.error = task.exception!!.localizedMessage
                }
                else -> {
                    Toast.makeText(this, task.exception!!.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        button_login.isEnabled = true
        button_register.isEnabled = true
        button_bypass.isEnabled = true
        indeterminateBar.visibility = View.INVISIBLE
    }

    private fun validateFields(): Boolean {
        var fieldToFocus: TextInputEditText? = null

        if (field_password.text.isNullOrBlank()) {
            field_password.error = "Please enter a password"
            fieldToFocus = field_password
        }

        if (field_email.text.isNullOrBlank()) {
            field_email.error = "Please enter your email"
            fieldToFocus = field_email
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(field_email.text.toString()).matches()) {
            field_email.error = "Please enter a valid email"
            fieldToFocus = field_email
        }

        if (fieldToFocus == null)
            return true

        fieldToFocus.requestFocus()
        return false
    }

    private fun moveToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        finish()
    }

    private fun moveToClosetGallery() {
        val intent = Intent(this, ClosetGalleryActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        finish()
    }
}

