package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.cop4331.group7.hangr.classes.GalleryPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_closet_gallery.*

// user home screen
// shows clothing and saved outfits
class ClosetGalleryActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.activity_closet_gallery)

        auth = FirebaseAuth.getInstance()

        setActivityState()
    }

    // set fragments and screen UI
    private fun setActivityState() {
        val fragmentAdapter = GalleryPagerAdapter(supportFragmentManager)

        viewpager_gallery.adapter = fragmentAdapter
        tabs_gallery.setupWithViewPager(viewpager_gallery)

        title = "Welcome, " + auth.currentUser!!.displayName + "!"
    }

    // inflates options menu to logout
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.gallery_actionbar, menu)

        return true
    }

    // perform selected action
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionbar_logout -> {
                handleLogout()
                return true
            }
            R.id.actionbar_hampr -> {
                val intent = Intent(this, HamprActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // logout and return to sign-in
    private fun handleLogout() {
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
