package com.cop4331.group7.hangr.classes

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class FirebaseClothingItem (
    val name: String = "",
    val category: String = "",
    val wears: Int = -1,
    val imageUri: String = "",
    @ServerTimestamp val dateCreated: Timestamp? = null
)