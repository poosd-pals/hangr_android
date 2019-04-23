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

    // this screen is the center icon on the bottom nav bar
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            // move to hampr
            R.id.navigation_hampr -> {
                val intent = Intent(this@ClosetGalleryActivity, HamprActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {
                return@OnNavigationItemSelectedListener true
            }
            // move to outfit assembly
            R.id.navigation_outfit -> {
                val intent = Intent(this@ClosetGalleryActivity, CreateOutfitActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                finish()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

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

        title = "  Welcome, " + auth.currentUser!!.displayName + "!"
        actionBar?.setDisplayUseLogoEnabled(true)
        actionBar?.setLogo(R.drawable.hangr_header)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.hangr_header)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        // get navigation view and set current item to checked
        navigation.menu.getItem(1).isChecked = true
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
