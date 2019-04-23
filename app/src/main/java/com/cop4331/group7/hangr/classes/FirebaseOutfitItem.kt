package com.cop4331.group7.hangr.classes

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseOutfitItem (
    val name: String = "",
    val isFavorite: Boolean = false,
    val clothingItems: List<String> = emptyList(),
    @ServerTimestamp val dateCreated: Timestamp? = null
): Parcelable