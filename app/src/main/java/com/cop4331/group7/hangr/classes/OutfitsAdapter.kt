package com.cop4331.group7.hangr.classes

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cop4331.group7.hangr.AddOrEditClothingActivity
import com.cop4331.group7.hangr.CreateOutfitActivity
import com.cop4331.group7.hangr.R
import com.cop4331.group7.hangr.constants.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.outfit_overview.view.*

class OutfitsAdapter(private val context: Context, private val options: FirestoreRecyclerOptions<FirebaseOutfitItem>,
                     private val auth: FirebaseAuth, private val db: FirebaseFirestore):
    FirestoreRecyclerAdapter<FirebaseOutfitItem, OutfitsAdapter.OutfitsHolder>(options) {

    override fun onBindViewHolder(holder: OutfitsHolder, position: Int, model: FirebaseOutfitItem) {
        val imageviewRef = listOf(holder.quad1, holder.quad2, holder.quad3, holder.quad4)
        var upper = model.clothingItems.size - 1
        if (upper > 3) upper = 3
        for (i in 0..upper) {
            if (!model.clothingItems[i].isNullOrBlank()) {
                val clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)
                clothesRef.document(model.clothingItems[i]).get().addOnCompleteListener {
                    if (it.result!!["imageUrl"].toString().isNotBlank()) {
                        Glide
                            .with(imageviewRef[i].context)
                            .load(it.result!!["imageUrl"])
                            .centerCrop()
                            .placeholder(CreateCircularDrawable.make(imageviewRef[i].context))
                            .into(imageviewRef[i])
                    }
                    else {
                        Glide
                            .with(imageviewRef[i].context)
                            .load(R.drawable.image_placeholder)
                            .into(imageviewRef[i])
                    }
                }
            } else {
                Glide
                    .with(imageviewRef[i].context)
                    .load(R.drawable.image_placeholder)
                    .into(imageviewRef[i])
            }
        }

        holder.name.text = model.name

        holder.overview.setOnLongClickListener {
            val intent = Intent(it.context, CreateOutfitActivity::class.java)

            intent.putExtra(EXISTING_OUTFIT_ITEM_DATA, model)
            with (snapshots.getSnapshot(holder.adapterPosition)) {
                intent.putExtra(EXISTING_CLOTHING_ITEM_PARENT_ID, this.id)
            }

            it.context.startActivity(intent)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.outfit_overview, parent, false)

        return OutfitsHolder(view)
    }

    class OutfitsHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val overview: LinearLayout = itemView.layout_outfit_overview
        val name: TextView = itemView.text_outfit
        val quad1: ImageView = itemView.image_quad_1
        val quad2: ImageView = itemView.image_quad_2
        val quad3: ImageView = itemView.image_quad_3
        val quad4: ImageView = itemView.image_quad_4
    }
}