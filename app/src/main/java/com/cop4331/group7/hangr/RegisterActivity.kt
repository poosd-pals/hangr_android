package com.cop4331.group7.hangr

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import hideKeyboard
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        // Hide keyboard when anywhere outside a textfield or button is clicked
        layout.setOnClickListener { it.hideKeyboard() }

        button_register.setOnClickListener { attemptRegister() }
        button_login.setOnClickListener { moveToLoginActivity() }

        // submit form when enter is pressed on the soft keyboard in the last textfield
        field_password.setOnEditorActionListener { view, code, event ->
            if (code == KeyEvent.KEYCODE_ENTER || code == EditorInfo.IME_ACTION_DONE) { validateFields() }
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        return true
    }

    private fun attemptRegister() {
        button_register.rootView.hideKeyboard()
        button_register.isEnabled = false
        button_login.isEnabled = false

        if (validateFields()) {
            indeterminateBar.visibility = View.VISIBLE
            createUser()
        }
        else {
            button_register.isEnabled = true
            button_login.isEnabled = true
        }
    }

    private fun validateFields(): Boolean {
        var fieldToFocus: TextInputEditText? = null

        if (field_password.text.isNullOrBlank()) {
            field_password.error = "Please enter a password"
            fieldToFocus = field_password
        }
        else if (field_password.text!!.count() < 8) {
            field_password.error = "Your password must be at least 8 characters"
            fieldToFocus = field_password
        }

        if (field_name.text.isNullOrBlank()) {
            field_name.error = "Please enter your name"
            fieldToFocus = field_name
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

    private fun createUser() {
        auth.createUserWithEmailAndPassword(field_email.text.toString(), field_password.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(field_name.text.toString())
                        .build()

                    // TODO?: Optional profile photo upload, add url to profile above
                    // .setPhotoUri(URI)

                    auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener {
                        Toast.makeText(this, "Account created! You may now log in to your account", Toast.LENGTH_LONG).show()
                        moveToLoginActivity()
                    }
                }
                else {
                    // TODO: show error?
                }
            }
    }

    private fun moveToLoginActivity() {
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}