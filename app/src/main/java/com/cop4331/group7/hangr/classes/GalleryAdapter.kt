package com.cop4331.group7.hangr.classes

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cop4331.group7.hangr.AddOrEditClothingActivity
import com.cop4331.group7.hangr.EXISTING_CLOTHING_ITEM_DATA
import com.cop4331.group7.hangr.EXISTING_CLOTHING_ITEM_PARENT_ID
import com.cop4331.group7.hangr.R.layout
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gallery_img_view.view.*

class ClothingGalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var button : LinearLayout = itemView.layout_gallery
    var placeHolder : TextView = itemView.text_placeholder
    var imageView: ImageView = itemView.image_view
}

class GalleryAdapter(options: FirestoreRecyclerOptions<FirebaseClothingItem>) :
    FirestoreRecyclerAdapter<FirebaseClothingItem, ClothingGalleryHolder>(options) {
    override fun onBindViewHolder(holder: ClothingGalleryHolder, position: Int, model: FirebaseClothingItem) {
        Picasso.get().load(model.imageUri).fit().into(holder.imageView)

        holder.placeHolder.text = model.name
        holder.button.setOnLongClickListener {
            val intent = Intent(it.context, AddOrEditClothingActivity::class.java)

            intent.putExtra(EXISTING_CLOTHING_ITEM_DATA, model)
            with (snapshots.getSnapshot(holder.adapterPosition)) {
                intent.putExtra(EXISTING_CLOTHING_ITEM_PARENT_ID, this.id)
            }

            it.context.startActivity(intent)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothingGalleryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layout.gallery_img_view, parent, false)

        return ClothingGalleryHolder(view)
    }

    override fun onError(e: FirebaseFirestoreException) {
        Log.e("error", e.message)
    }
}