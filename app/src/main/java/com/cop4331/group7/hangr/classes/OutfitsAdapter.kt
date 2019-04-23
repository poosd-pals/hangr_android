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
import com.cop4331.group7.hangr.constants.CLOTHING_DB_STRING
import com.cop4331.group7.hangr.constants.HANGR_DB_STRING
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.outfit_overview.view.*

class OutfitsAdapter(private val context: Context, private val options: FirestoreRecyclerOptions<FirebaseOutfitItem>,
                     private val auth: FirebaseAuth, private val db: FirebaseFirestore):
    FirestoreRecyclerAdapter<FirebaseOutfitItem, OutfitsAdapter.OutfitsHolder>(options) {

    override fun onBindViewHolder(holder: OutfitsHolder, position: Int, model: FirebaseOutfitItem) {
        val imageview_ref = listOf(holder.quad1, holder.quad2, holder.quad3, holder.quad4)
        var upper = model.clothingItems.size - 1
        if (upper > 3) upper = 3
        for (i in 0..upper) {
            if (!model.clothingItems[i].isNullOrBlank()) {
                val clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)
                val clothing = clothesRef.document(model.clothingItems[i]).get().addOnCompleteListener {
                    if (it.result!!["imageUrl"].toString().isNotBlank()) {
                        Glide
                            .with(imageview_ref[i].context)
                            .load(it.result!!["imageUrl"])
                            .centerCrop()
                            .placeholder(CreateCircularDrawable.make(imageview_ref[i].context))
                            .into(imageview_ref[i])
                    }
                    else {
                        Glide
                            .with(imageview_ref[i].context)
                            .load(R.drawable.ic_add_a_photo_black_24dp)
                            .into(imageview_ref[i])
                    }
                }
            } else {
                Glide
                    .with(imageview_ref[i].context)
                    .load(R.drawable.ic_add_a_photo_black_24dp)
                    .into(imageview_ref[i])
            }
        }

        // val clothesRef = db.collection(HANGR_DB_STRING).document(auth.currentUser!!.uid).collection(CLOTHING_DB_STRING)
        // val clothingItem = clothesRef.document(ids[position]).get().result!!

        holder.name.text = model.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.outfit_overview, parent, false)

        return OutfitsHolder(view)
    }

    class OutfitsHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.text_outfit
        val quad1: ImageView = itemView.image_quad_1
        val quad2: ImageView = itemView.image_quad_2
        val quad3: ImageView = itemView.image_quad_3
        val quad4: ImageView = itemView.image_quad_4
    }
}