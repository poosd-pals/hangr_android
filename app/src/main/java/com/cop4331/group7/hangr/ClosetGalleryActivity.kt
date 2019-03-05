package com.cop4331.group7.hangr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_closet_gallery.*
import kotlinx.android.synthetic.main.gallery_img_view.view.*

class ClosetGalleryActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // go to activity when navigation button is pressed
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_new_clothes -> {
                val intent = Intent(this@ClosetGalleryActivity, NewClothesActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gallery -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfit -> {
                val intent = Intent(this@ClosetGalleryActivity, CreateOutfitActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_closet_gallery)

        // navigation
        navigation.menu.getItem(1).isChecked = true
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // recycler view
        viewManager = GridLayoutManager(this@ClosetGalleryActivity, 2)
        viewAdapter = MyAdapter(arrayOf("images", "loaded", "from", "DB", "should", "go", "here"))

        recycler_gallery.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    class MyAdapter(private val myDataset: Array<String>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var button : LinearLayout = itemView.layout_gallery
            var placeHolder : TextView = itemView.text_placeholder
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_img_view, parent, false)

            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // TODO: get image from DB
            holder.placeHolder.text = myDataset[position]

            holder.button.setOnLongClickListener {
                val intent = Intent(it.context, EditClothing::class.java)
                // TODO: put info regarding the view selected so fields in activity_edit_clothing can be populated
                // intent.putExtra()
                it.context.startActivity(intent)
                true
            }
        }

        override fun getItemCount() = myDataset.size
    }
}
