package com.cop4331.group7.hangr.classes

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cop4331.group7.hangr.AddOrEditClothingActivity
import com.cop4331.group7.hangr.EXTRA_MESSAGE
import com.cop4331.group7.hangr.R
import kotlinx.android.synthetic.main.gallery_img_view.view.*

class GalleryAdapter(private val myDataset: Array<String>) :
    RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button : LinearLayout = itemView.layout_gallery
        var placeHolder : TextView = itemView.text_placeholder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_img_view, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // TODO: get image from DB
        holder.placeHolder.text = myDataset[position]

        holder.button.setOnLongClickListener {
            val intent = Intent(it.context, AddOrEditClothingActivity::class.java)
            // TODO: put info regarding the view selected so fields in activity_add_or_edit_clothing can be populated
            intent.putExtra(EXTRA_MESSAGE, "ClosetGalleryActivity")
            it.context.startActivity(intent)
            true
        }
    }

    override fun getItemCount() = myDataset.size
}