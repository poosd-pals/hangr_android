package com.cop4331.group7.hangr.classes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cop4331.group7.hangr.R
import kotlinx.android.synthetic.main.outfit_clothing_view.view.*

class OutfitAdapter(private val context: Context, private val outfit: MutableList<FirebaseClothingItem>):
    RecyclerView.Adapter<OutfitAdapter.OutfitClothingHolder>() {
    override fun onBindViewHolder(holder: OutfitClothingHolder, position: Int) {
        val clothingItem = outfit[position]

        Glide.with(context)
            .load(clothingItem.imageUrl)
            .into(holder.imageView)

        holder.name.text = clothingItem.name

        holder.parentLayout.setOnClickListener {
            Toast.makeText(context, clothingItem.name, Toast.LENGTH_LONG).show()
        }
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
        val parentLayout: LinearLayout = itemView.layout_outfit_item
    }
}