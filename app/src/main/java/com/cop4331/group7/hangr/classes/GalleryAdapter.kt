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
        Picasso.get().load(model.imageUri).into(holder.imageView)

        holder.placeHolder.text = model.name
        holder.button.setOnLongClickListener {
            val intent = Intent(it.context, AddOrEditClothingActivity::class.java)
            // TODO: put info regarding the view selected so fields in activity_add_or_edit_clothing can be populated
            intent.putExtra(EXISTING_CLOTHING_ITEM_DATA, model)
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


//class GalleryAdapter(private val myDataset: Array<String>) :
//    RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.gallery_img_view, parent, false)
//
//        return MyViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        // TODO: get image from DB
//        holder.placeHolder.text = myDataset[position]
//
//        holder.button.setOnLongClickListener {
//            val intent = Intent(it.context, AddOrEditClothingActivity::class.java)
//            // TODO: put info regarding the view selected so fields in activity_add_or_edit_clothing can be populated
//            intent.putExtra(EXTRA_MESSAGE, "ClosetGalleryActivity")
//            it.context.startActivity(intent)
//            true
//        }
//    }
//
//    override fun getItemCount() = myDataset.size
