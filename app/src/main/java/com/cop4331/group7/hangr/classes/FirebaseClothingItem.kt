package com.cop4331.group7.hangr.classes

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

// Clothing Item Object
@Parcelize
data class FirebaseClothingItem (
    val name: String = "",                      // user defined item name
    val category: String = "",                  // selected category
    val wearsTotal: Int = 0,                    // wears before considered dirty
    val wearsLeft: Int = -1,                    // active wears remaining
    val colors: List<String> = emptyList(),     // colors of item
    val tags: List<String> = emptyList(),       // user-defined tags
    val imageUrl: String = "",
    val imageFilename: String = "",
    @ServerTimestamp val dateCreated: Timestamp? = null
): Parcelable