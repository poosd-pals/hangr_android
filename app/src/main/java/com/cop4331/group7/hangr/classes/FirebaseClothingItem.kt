package com.cop4331.group7.hangr.classes

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseClothingItem (
    val name: String = "",
    val category: String = "",
    val wears: Int = -1,
    val imageUri: String = "",
    @ServerTimestamp val dateCreated: Timestamp? = null
) : Parcelable