package com.cop4331.group7.hangr.classes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cop4331.group7.hangr.R
import kotlinx.android.synthetic.main.outfit_clothing_view.view.*

class AssembleOutfitAdapter(private val context: Context, private val outfit: MutableList<FirebaseClothingItem>):
    RecyclerView.Adapter<AssembleOutfitAdapter.OutfitClothingHolder>() {
    override fun onBindViewHolder(holder: OutfitClothingHolder, position: Int) {
        val clothingItem = outfit[position]

        // load image or placeholder into imageView
        if (clothingItem.imageUrl.isNotBlank()) {
            Glide
                .with(holder.imageView.context)
                .load(clothingItem.imageUrl)
                .centerCrop()
                .placeholder(CreateCircularDrawable.make(holder.imageView.context))
                .into(holder.imageView)
        } else {
            Glide.with(holder.imageView.context)
                .load(R.drawable.image_placeholder)
                .into(holder.imageView)
        }


        holder.name.text = clothingItem.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitClothingHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.outfit_clothing_view, parent, false)

        return OutfitClothingHolder(view)
    }

    override fun getItemCount(): Int {
        return outfit.size
    }

    class OutfitClothingHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.text_outfit_name
        val imageView: ImageView = itemView.image_clothing_in_outfit
    }
}